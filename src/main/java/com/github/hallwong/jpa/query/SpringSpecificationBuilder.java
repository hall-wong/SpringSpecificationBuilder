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

import com.github.hallwong.jpa.query.annotation.Restriction;
import com.github.hallwong.jpa.query.annotation.QueryRequirement;
import com.github.hallwong.jpa.query.meta.BuilderMetaKeeper;
import com.github.hallwong.jpa.query.meta.EntityMetaKeeper;
import com.github.hallwong.jpa.query.meta.RestrictionMeta;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SpringSpecificationBuilder<T> {

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends SpringSpecificationBuilder>, BuilderMetaKeeper> builderMetaStore = new HashMap<>();

    private static final Map<Class<?>, EntityMetaKeeper> entityMetaStore = new HashMap<>();

    private final List<FieldKeeper> fields = new ArrayList<>();

    protected SpringSpecificationBuilder() {
        super();
    }

    List<FieldKeeper> getFields() {
        return fields;
    }

    public final SpringSpecificationBuilder<T> set(String field, Object... params) {
        BuilderMetaKeeper meta;
        lock.readLock().lock();
        meta = builderMetaStore.get(this.getClass());
        if (meta == null) {
            lock.readLock().unlock();
            meta = readBuilderMeta();
        }
        lock.readLock().lock();
        try {
            RestrictionMeta restriction = meta.getRestriction(field);
            Class<?> fieldType;
            FieldKeeper fieldKeeper = new FieldKeeper();
            fieldKeeper.name = field;
            fieldKeeper.operator = restriction.getOperator();
            //judge field type by the given parameter 'params'
            //根据传入的params判断参数类型
            switch (params.length) {
                //some operator doesn't need a parameter
                //特殊的运算符，如NotNull，不需要参数
                case 0:
                    fieldType = null;
                    fieldKeeper.parameter = null;
                    break;
                case 1:
                    //TODO judge whether collection
                    fieldType = params[0].getClass();
                    fieldKeeper.parameter = params[0];
                    break;
                default:
                    fieldType = null;
                    for (Object param : params) {
                        if (fieldType == null) {
                            fieldType = param.getClass();
                        } else if (fieldType != param.getClass()) {
                            throw new IllegalArgumentException("params array contains different types");
                        }
                    }
                    fieldKeeper.parameter = params;
                    break;
            }
            fieldKeeper.operator.validateType(fieldType);
            fields.add(fieldKeeper);
        } finally {
            lock.readLock().unlock();
        }
        return this;
    }

    private BuilderMetaKeeper readBuilderMeta() {
        lock.writeLock().lock();
        try {
            BuilderMetaKeeper meta = new BuilderMetaKeeper();
            QueryRequirement queryRequirement = this.getClass().getAnnotation(QueryRequirement.class);
            if (queryRequirement == null) {
                throw new IllegalStateException(String.format("class %s is not correctly annotated!", this.getClass().getName()));
            }
            Restriction[] restrictions = queryRequirement.restrictions();
            if (restrictions.length == 0) {
                throw new IllegalStateException("restrictions is empty");
            }
            Type genericSuperclass = this.getClass().getGenericSuperclass();
            Type genericType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            Class<?> entityClass = (Class<?>) genericType;
            EntityMetaKeeper entityMeta = entityMetaStore.get(entityClass);
            if (entityMeta == null) {
                entityMeta = readEntityMeta(entityClass);
            }
            for (Restriction restriction : restrictions) {
                RestrictionMeta rm = new RestrictionMeta();
                rm.setOperator(restriction.operator());
                meta.addRestriction(restriction.field(), rm);
            }
            builderMetaStore.put(this.getClass(), meta);
            return meta;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private EntityMetaKeeper readEntityMeta(Class<?> entityClass) {
        Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
        //TODO support hibernate annotation
        //TODO add config to ignore this check
        if (entityAnnotation == null) {
            throw new IllegalStateException(entityClass.getName() + " isn't annotated as an entity");
        }
        Field[] entityFields = entityClass.getDeclaredFields();
        //TODO should consider fields in the super class
        EntityMetaKeeper meta = new EntityMetaKeeper(entityClass);
        if (entityFields.length == 0) {
            throw new IllegalStateException(entityClass.getName() + " doesn't have any fields");
        }
        boolean noColumn = true;
        for (Field entityField : entityFields) {
            if (entityField.getAnnotation(Transient.class) != null) {
                continue;
            }
            noColumn = false;
            //TODO should parse other entity or component into xx.yy
            meta.addField(entityField.getName(), entityField.getType());
        }
        if (noColumn) {
            throw new IllegalStateException(entityClass.getName() + " doesn't have any columns");
        }
        entityMetaStore.put(entityClass, meta);
        return meta;
    }

    public Specification<T> build() {
        return new SpringSpecificationImpl<>(this);
    }

}
