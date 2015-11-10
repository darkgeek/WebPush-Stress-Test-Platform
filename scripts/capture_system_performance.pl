#!/usr/bin/env perl
use strict;
use 5.010;
use warnings;

use threads;
use threads::shared;

use Data::Dumper;
use IO::Socket;
use Net::hostent;

use constant PORT => 9002;
my $stat_task;
my $stop_task :shared = 0;
my @stats;
my $push_server_pid;

sub get_current_stat_by_pid {
    my $pid = shift;
    my $res = {};

    my $raw_res = `ps -o pcpu,pmem,pid`;
    foreach my $line (split /^/, $raw_res) {
        my @words = split /\s+/, $line;
        clean_blank_item(\@words);

        if (defined $words[2] and $words[2] eq "$pid") {
            $res->{pid} = $pid;
            $res->{cpu} = $words[0];
            $res->{mem} = $words[1];
            last;
        }
    }

    return $res;
}

sub stat_job {
    my $client = shift;
    say "Push server pid is $push_server_pid";
    while (!$stop_task) {
        push @stats, get_current_stat_by_pid($push_server_pid);
        sleep(1);
    }

    say "stop this job!";
    print Dumper(@stats);

    my $cal_res = calculate();
    print $client $cal_res->{cpu}."-".$cal_res->{mem};
    close $client;
}

sub calculate {
    my $cpu_total = 0;
    my $mem_total = 0;
    my $cal_res = {};

    foreach my $stat (@stats) {
        my $cpu = $stat->{cpu};
        my $mem = $stat->{mem};
        
        $cpu_total += $cpu;
        $mem_total += $mem;
    }

    $cal_res->{cpu} = $cpu_total * 1.0 / @stats * 1.0;
    $cal_res->{mem} = $mem_total * 1.0 / @stats * 1.0;

    return $cal_res;
}

sub clean_blank_item {
    my $raw_array = shift;
    my $arr_size = @$raw_array;

    for (my $i = 0; $i < $arr_size; $i++) {
        my $item = $raw_array->[$i];
        if ($item eq "") {
            remove_item_in_array($raw_array, $i);
            $arr_size--;
        }
    }
}

sub remove_item_in_array {
    my ($array, $index) = @_;

    if ($index < 0 or $index > @$array) {
        return;
    }

    splice(@$array, $index, 1);
}

sub start_server {
    my $server = IO::Socket::INET->new(Proto     => "tcp", 
                                       LocalPort => PORT,
                                       Listen    => SOMAXCONN,
                                       Reuse     => 1);
    die "can't setup server" unless $server;
    say "[Server $0 accepting clients...]";

    while (my $client = $server->accept()) {
        $client->autoflush(1);
        my $hostinfo = gethostbyaddr($client->peeraddr);
        printf "[Connect from %s]\n", $hostinfo ? $hostinfo->name : $client->peerhost;
        while ( <$client> ) {
            next unless /\S/;       # blank line
            if    (/quit|exit/i)    { last                                       }
            elsif (/start [0-9]+/i)        {say "Start!"; start_stat_task($client, $_);}
            elsif (/stop/i)         {say "Stop!"; stop_stat_task();}
        }
        close $client;
    }
}

sub start_stat_task {
    my $client = shift;
    my $raw_cmd = shift;
    my @params = split ' ', $raw_cmd;

    $push_server_pid = $params[1];
    $stat_task = threads->create(\&stat_job, $client) unless defined $stat_task;
}

sub stop_stat_task {
    $stop_task = 1;
}

start_server();
