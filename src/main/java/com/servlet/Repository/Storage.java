package com.servlet.Repository;


import com.servlet.Repository.Message;
import com.servlet.Repository.Pairs;

import java.util.*;

public final class Storage {
    private static List<Pairs> history = Collections.synchronizedList(new ArrayList<Pairs>());

    public static void addMessage(String request,Message r) {
        Pairs pair = new Pairs(request,r);
        history.add(pair);
    }

    public static void addAll(List<Pairs> list) {
        history.addAll(list);
    }

    public static int getSize() {
        return history.size();
    }

    public static void deleteAll(){
        history.removeAll(history);
    }

    public static List<Pairs> getHistory(int index) {
        return history.subList(index, history.size());
    }
}
