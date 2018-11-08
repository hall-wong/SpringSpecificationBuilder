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

package com.github.hallwong.jpa.query;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

class SpringSpecificationImpl<T> implements Specification<T> {

    private SpringSpecificationBuilder<T> builder;

    SpringSpecificationImpl(SpringSpecificationBuilder<T> builder) {
        this.builder = builder;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate restrictions = cb.conjunction();
        List<FieldKeeper> fields = builder.getFields();
        for (FieldKeeper field : fields) {
            switch (field.operator) {
                case Equal:
                    restrictions = cb.and(restrictions, cb.equal(root.get(field.name), field.parameter));
                    break;
                case Contains:
                    restrictions = cb.and(restrictions, cb.like(root.get(field.name), "%" + field.parameter + "%"));
                    break;
                case StartWith:
                    restrictions = cb.and(restrictions, cb.like(root.get(field.name), field.parameter + "%"));
                    break;
                case EndWith:
                    restrictions = cb.and(restrictions, cb.like(root.get(field.name), "%" + field.parameter));
                    break;
                case In:
                    restrictions = cb.and(restrictions, root.get(field.name).in((Object[]) field.parameter));
                    break;
                case NotNull:
                    restrictions = cb.and(restrictions, cb.isNotNull(root.get(field.name)));
                    break;
            }
        }
        return restrictions;
    }
}
