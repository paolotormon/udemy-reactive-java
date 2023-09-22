package com.learnreactiveprogramming.imperative;

import java.util.ArrayList;
import java.util.List;

public class ImperativeExample {
    public static void main(String[] args) {
        var namesList = List.of("Alex", "Ben", "Chloe");
        var newNamesList = namesGreaterThanLength(namesList, 3);
        System.out.println(newNamesList);
    }

    private static List<String> namesGreaterThanLength(List<String> namesList
            , int length) {
        var newNamesList = new ArrayList<String>();
        for (var name : namesList) {
            if (name.length() > length) {
                newNamesList.add(name);
            }
        }
        return newNamesList;

    }
}
