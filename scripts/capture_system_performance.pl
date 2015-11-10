#!/usr/bin/env perl
use strict;
use 5.010;
use warnings;
use Data::Dumper;

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

my $stat_result = get_current_stat_by_pid(1054);
print Dumper($stat_result);
