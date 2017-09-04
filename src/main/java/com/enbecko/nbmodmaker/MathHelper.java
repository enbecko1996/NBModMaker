package com.enbecko.nbmodmaker;

import java.util.List;

public class MathHelper {
    public static int[] toIntArray(List<Integer> list){
        int[] ret = new int[list.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = list.get(i);
        return ret;
    }

    public static boolean isPowerOf2(int n) {
        return (n & (n - 1)) == 0;
    }
}
