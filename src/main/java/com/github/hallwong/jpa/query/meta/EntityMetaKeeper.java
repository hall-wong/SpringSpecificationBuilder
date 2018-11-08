/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.hallwong.jpa.query.meta;

import java.util.HashMap;
import java.util.Map;

public class EntityMetaKeeper {

    private final Class<?> entityType;

    private final Map<String, Class<?>> store = new HashMap<>();

    public EntityMetaKeeper(Class<?> entityType) {
        this.entityType = entityType;
    }

    public void addField(String field, Class<?> fieldType) {
        store.put(field, fieldType);
    }

    public Class<?> getFieldType(String field) {
        Class<?> fieldType = store.get(field);
        if (fieldType == null) {
            throw new NullPointerException(String.format("entity '%s' doesn't have any field named: %s", entityType.getName(), field));
        }
        return fieldType;
    }


}
