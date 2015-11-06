package im.darkgeek.stp.utils;

import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Created by justin on 15-11-6.
 */
public class AlgorithmUtils {
    public static class MedianFinder {

        PriorityQueue<Integer> maxheap = new PriorityQueue<Integer>();
        PriorityQueue<Integer> minheap = new PriorityQueue<Integer>(11, Collections.reverseOrder());

        // Adds a number into the data structure.
        public void addNum(int num) {
            maxheap.offer(num);
            minheap.offer(maxheap.poll());
            if(maxheap.size() < minheap.size()){
                maxheap.offer(minheap.poll());
            }
        }

        // Returns the median of current data stream
        public double findMedian() {
            return maxheap.size() == minheap.size() ? (double)(maxheap.peek() + minheap.peek()) / 2.0 : maxheap.peek();
        }
    };
}
