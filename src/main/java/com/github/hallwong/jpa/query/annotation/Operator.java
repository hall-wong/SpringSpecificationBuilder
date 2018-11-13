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

package com.github.hallwong.jpa.query.annotation;

public enum Operator {

    Equal,
    In,
    NotNull {
        @Override
        public void validateType(Class<?> fieldType) {
            if (fieldType != null) {
                throw new IllegalArgumentException("you don't need set any restrictions for operator NotNull");
            }
        }
    },
    Contains {
        @Override
        public void validateType(Class<?> fieldType) {
            allow(fieldType, CharSequence.class);
        }
    },
    StartWith {
        @Override
        public void validateType(Class<?> fieldType) {
            allow(fieldType, CharSequence.class);
        }
    },
    EndWith {
        @Override
        public void validateType(Class<?> fieldType) {
            allow(fieldType, CharSequence.class);
        }
    };

    private static final Class<?>[] ALL_ALLOW_TYPES = {CharSequence.class, Number.class, Boolean.class};

    public void validateType(Class<?> fieldType) {
        allowAll(fieldType);
    }

    private static void allowAll(Class<?> fieldType) {
        allow(fieldType, ALL_ALLOW_TYPES);
    }

    private static void allow(Class<?> fieldType, Class<?>... allowedTypes) {
        for (Class<?> allowedType : allowedTypes) {
            if (allowedType.isAssignableFrom(fieldType)) {
                return;
            }
        }
        throw new IllegalStateException("Doesn't support type: " + fieldType);
    }
}
