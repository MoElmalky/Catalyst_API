package com.meshwarcoders.catalyst.util;

import java.util.*;

public class UtilFunctions {
    public static boolean compareLists(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) return false;

        // Create copies so we don't modify the original lists
        List<String> copy1 = new ArrayList<>(list1);
        List<String> copy2 = new ArrayList<>(list2);

        Collections.sort(copy1);
        Collections.sort(copy2);

        return copy1.equals(copy2);
    }

    public static int countCommonUnique(List<Integer> list1, List<Integer> list2) {
        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);

        // Keeps only the elements found in both sets
        set1.retainAll(set2);

        return set1.size();
    }
}
