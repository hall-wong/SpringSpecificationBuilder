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
    Equal {
        @Override
        public void validateType(Class<?> fieldType) {
            //allow any type
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
    },
    In {
        @Override
        public void validateType(Class<?> fieldType) {
            allow(CharSequence.class);
        }
    },
    NotNull;

    public void validateType(Class<?> fieldType) {
        throw new UnsupportedOperationException();
    }

    private static void allow(Class<?> fieldType, Class<?>... s) {
        for (Class<?> s1 : s) {
            if (s1.isAssignableFrom(fieldType)) {
                return;
            }
        }
        throw new IllegalStateException("Doesn't support type: " + fieldType);
    }
}
