package com.oleksa.snapshot;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PriorityQueuePerf {

    private static final long MAX = 10000000;
    private static final long FIRST = 25;

    /**
     * This is implementation of first n sorted element algorithm
     *
     * Init entire priority queue and poll first n elements is faster than
     * Sort entire array and copy first n elements
     * only in case FIRST/MAX ratio is below 1/100, otherwise it's slower
     * and ration 1/10000 two queues faster - not clear!
     *
     * Also note: it is much faster to sort primitive ints than Integer objects
     * Stream is very sensible to above, very contrast perf
     */
    @Test
    void testGettingFirstElementsSorted() {
        StopWatch watch = new StopWatch();
        watch.start();
        List<Integer> integers = getRandomList();
        watch.stop();
        System.out.println(watch.getLastTaskTimeMillis() + " ms generate() O(MAX)");

        watch.start();
        PriorityQueue<Integer> queue = new PriorityQueue<>(integers);
        List<Integer> first = Stream.generate(queue::poll)
                .limit(FIRST)
                .collect(Collectors.toList());

        // or with passing custom comparator
        PriorityQueue<Integer> queue1 = new PriorityQueue<>(Comparator.reverseOrder());
        queue1.addAll(integers);
//        integers.forEach(num -> queue1.add(num)); // the same effect!

//        List<Integer> last = Stream.generate(queue1::poll)
//                .limit(FIRST)
//                .collect(Collectors.toList());
        watch.stop();
        System.out.println(first);
//        System.out.println(last);
        System.out.println(watch.getLastTaskTimeMillis() + " ms priority() O(MAX + FIRST * LOG(MAX))");

        watch.start();
//        Collections.sort(integers);
//        integers.sort(Integer::compareTo);
//        first = Stream.iterate(0, i -> i + 1).map(integers::get)
//                .limit(FIRST)
//                .collect(Collectors.toList());

        int[] ints = new int[(int) MAX];
        for (int i = 0; i < MAX; i++) {
            ints[i] = integers.get(i);
        }
        Arrays.sort(ints);
        first = Stream.iterate(0, i -> i + 1).map(j -> ints[j])
                .limit(FIRST)
                .collect(Collectors.toList());
//        last = Stream.iterate((int) MAX - 1, i -> i - 1).map(j -> ints[j])
//                .limit(FIRST)
//                .collect(Collectors.toList());
        watch.stop();
        System.out.println(first);
//        System.out.println(last);
        System.out.println(watch.getLastTaskTimeMillis() + " ms sort() O(MAX * LOG(MAX) + FIRST)");

        watch.start();
        first = integers.stream().mapToInt(Integer::intValue).sorted().limit(FIRST)
                .boxed().collect(Collectors.toList());
//        first = integers.stream().sorted().limit(FIRST) // never sort boxed ints
//                .collect(Collectors.toList());
        watch.stop();
        System.out.println(first);
        System.out.println(watch.getLastTaskTimeMillis() + " ms Stream.sorted() O? O(MAX * LOG(MAX) + FIRST)");
    }

    private List<Integer> getRandomList() {
        return Stream.generate(RandomUtils::nextInt)
                .limit(MAX)
                .collect(Collectors.toList());
    }
}
