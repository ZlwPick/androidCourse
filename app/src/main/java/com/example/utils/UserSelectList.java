package com.example.utils;

import java.util.ArrayList;
import java.util.List;

public class UserSelectList {
    //已经没有用了
    //0b00000001-A 0b00000010-B 0b00000100-C 0b00001000-C
    //select A: answerList.add(new Integer(1))
    //select B: answerList.add(new Integer(2))
    //select C: answerList.add(new Integer(4))
    //select D: answerList.add(new Integer(8))
    //select A,C,D: answerList.add(new Integer(13))
    private static List<Integer> answerList = new ArrayList<Integer>();

    public static void select(int r) {
        answerList.add(new Integer(r));
    }

    public static int get(int no) {
        return answerList.get(no);
    }

    public static void set(int no, int r) {
        answerList.set(no, new Integer(r));
    }
}
