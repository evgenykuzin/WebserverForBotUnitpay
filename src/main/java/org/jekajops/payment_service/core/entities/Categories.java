package org.jekajops.payment_service.core.entities;

import java.util.*;

public class Categories extends HashMap<String, Set<String>> {
    public void add(String categoryName, String subcategoryName) {
        Set<String> subcategories = get(categoryName);
        if (subcategories != null) {
            subcategories.add(subcategoryName);
        } else {
            var subSet = new HashSet<String>();
            if (!subcategoryName.isEmpty()) subSet.add(subcategoryName);
            put(categoryName, subSet);
        }
    }

    public boolean containsPair(String category, String subcategory) {
        Set<String> subcategories = get(category);
        return containsKey(category) &&
                (subcategories.contains(subcategory) ||
                        subcategories.isEmpty() && subcategory.isEmpty());

    }
}
