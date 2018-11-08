package com.github.hallwong.jpa.query.meta;

import java.util.HashMap;
import java.util.Map;

public class BuilderMetaKeeper {

    private final Map<String, RestrictionMeta> store = new HashMap<>();

    public void addRestriction(String field, RestrictionMeta restriction) {
        store.put(field, restriction);
    }

    public RestrictionMeta getRestriction(String field) {
        RestrictionMeta restriction = store.get(field);
        if (restriction == null) {
            throw new NullPointerException(String.format("the restriction of the field '%s' is not declared", field));
        }
        return restriction;
    }


}
