package com.servlet;


import javafx.util.Pair;

import java.util.*;

public final class Storage {
    private static List<Pairs> history = Collections.synchronizedList(new ArrayList<Pairs>());

    public static void addMessage(String request,Message r) {
        Pairs pair = new Pairs(r,request);
        history.add(pair);
    }

    public static void addAll(List<Pairs> list) {
        history.addAll(list);
    }

    public static int getSize() {
        return history.size();
    }

    public static List<Pairs> getHistory(int index) {
        return history.subList(index, history.size());
    }
}
