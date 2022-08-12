package com.azhengapps.fencingpoolandscorekeeper.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * USA Fencing Pool Format. See https://www.usafencing.org/operations-manual.
 */
public class PoolFormat {

    private static int[][] POOL_FORMAT_4 = {
            { 1, 4 }, { 2, 3 }, { 1, 3 }, { 2, 4 }, { 3, 4 }, { 1, 2 }
    };

    private static int[][] POOL_FORMAT_5 = {
            { 1, 2 }, { 3, 4 }, { 5, 1 }, { 2, 3 }, { 5, 4 }, { 1, 3 },
            { 2, 5 }, { 4, 1 }, { 3, 5 }, { 4, 2 }
    };

    private static int[][] POOL_FORMAT_6 = {
            { 1, 2 }, { 4, 3 }, { 6, 5 }, { 3, 1 }, { 2, 6 }, { 5, 4 },
            { 1, 6 }, { 3, 5 }, { 4, 2 }, { 5, 1 }, { 6, 4 }, { 2, 3 },
            { 1, 4 }, { 5, 2 }, { 3, 6 }
    };

    private static int[][] POOL_FORMAT_7 = {
            { 1, 4 }, { 2, 5 }, { 3, 6 }, { 7, 1 }, { 5, 4 }, { 2, 3 },
            { 6, 7 }, { 5, 1 }, { 4, 3 }, { 6, 2 }, { 5, 7 }, { 3, 1 },
            { 4, 6 }, { 7, 2 }, { 3, 5 }, { 1, 6 }, { 2, 4 }, { 7, 3 },
            { 6, 5 }, { 1, 2 }, { 4, 7 }
    };

    private static int[][] POOL_FORMAT_8 = {
            { 2, 3 }, { 1, 5 }, { 7, 4 }, { 6, 8 }, { 1, 2 }, { 3, 4 },
            { 5, 6 }, { 8, 7 }, { 4, 1 }, { 5, 2 }, { 8, 3 }, { 6, 7 },
            { 4, 2 }, { 8, 1 }, { 7, 5 }, { 3, 6 }, { 2, 8 }, { 5, 4 },
            { 6, 1 }, { 3, 7 }, { 4, 8 }, { 2, 6 }, { 3, 5 }, { 1, 7 },
            { 4, 6 }, { 8, 5 }, { 7, 2 }, { 1, 3 }
    };

    private static int[][] POOL_FORMAT_9 = {
            { 1, 9 }, { 2, 8 }, { 3, 7 }, { 4, 6 }, { 1, 5 }, { 2, 9 },
            { 8, 3 }, { 7, 4 }, { 6, 5 }, { 1, 2 }, { 9, 3 }, { 8, 4 },
            { 7, 5 }, { 6, 1 }, { 3, 2 }, { 9, 4 }, { 5, 8 }, { 7, 6 },
            { 3, 1 }, { 2, 4 }, { 5, 9 }, { 8, 6 }, { 7, 1 }, { 4, 3 },
            { 5, 2 }, { 6, 9 }, { 8, 7 }, { 4, 1 }, { 5, 3 }, { 6, 2 },
            { 9, 7 }, { 1, 8 }, { 4, 5 }, { 3, 6 }, { 2, 7 }, { 9, 8 }
    };

    private static int[][] POOL_FORMAT_10 = {
            { 1, 4 }, { 6, 9 }, { 2, 5 }, { 7, 10 }, { 3, 1 }, { 8, 6 },
            { 4, 5 }, { 9, 10 }, { 2, 3 }, { 7, 8 }, { 5, 1 }, { 10, 6 },
            { 4, 2 }, { 9, 7 }, { 5, 3 }, { 10, 8 }, { 1, 2 }, { 6, 7 },
            { 3, 4 }, { 8, 9 }, { 5, 10 }, { 1, 6 }, { 2, 7 }, { 3, 8 },
            { 4, 9 }, { 6, 5 }, { 10, 2 }, { 8, 1 }, { 7, 4 }, { 9, 3 },
            { 2, 6 }, { 5, 8 }, { 4, 10 }, { 1, 9 }, { 3, 7 }, { 8, 2 },
            { 6, 4 }, { 9, 5 }, { 10, 3 }, { 7, 1 }, { 4, 8 }, { 2, 9 },
            { 3, 6 }, { 5, 7 }, { 1, 10 }
    };

    private static final Map<Integer, List<int[]>> poolBoutsMap;

    static {
        Map<Integer, List<int[]>> poolFormatMap = new HashMap<>();
        poolFormatMap.put(4, Arrays.asList(POOL_FORMAT_4));
        poolFormatMap.put(5, Arrays.asList(POOL_FORMAT_5));
        poolFormatMap.put(6, Arrays.asList(POOL_FORMAT_6));
        poolFormatMap.put(7, Arrays.asList(POOL_FORMAT_7));
        poolFormatMap.put(8, Arrays.asList(POOL_FORMAT_8));
        poolFormatMap.put(9, Arrays.asList(POOL_FORMAT_9));
        poolFormatMap.put(10, Arrays.asList(POOL_FORMAT_10));
        poolBoutsMap = Collections.unmodifiableMap(poolFormatMap);
    }

    public static Map<Integer, List<int[]>> getPoolBoutsOrderMap() {
        return poolBoutsMap;
    }
}
