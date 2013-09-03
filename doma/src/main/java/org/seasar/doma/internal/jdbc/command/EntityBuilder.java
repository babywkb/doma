/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.jdbc.command;

import static org.seasar.doma.internal.Constants.*;
import static org.seasar.doma.internal.util.AssertionUtil.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.doma.internal.jdbc.query.Query;
import org.seasar.doma.jdbc.JdbcMappingVisitor;
import org.seasar.doma.jdbc.MappedPropertyNotFoundException;
import org.seasar.doma.jdbc.Sql;
import org.seasar.doma.jdbc.entity.EntityPropertyType;
import org.seasar.doma.jdbc.entity.EntityType;
import org.seasar.doma.jdbc.entity.NamingType;
import org.seasar.doma.wrapper.Wrapper;

/**
 * @author taedium
 * 
 */
public class EntityBuilder<E> implements ResultBuilder<ResultSet, E> {

    protected final Query query;

    protected final EntityType<E> entityType;

    protected Map<Integer, String> indexMap;

    public EntityBuilder(Query query, EntityType<E> entityType) {
        assertNotNull(query, entityType);
        this.query = query;
        this.entityType = entityType;
    }

    @Override
    public E build(ResultSet resultSet) throws SQLException {
        assertNotNull(resultSet);
        if (indexMap == null) {
            indexMap = createIndexMap(resultSet.getMetaData(), entityType);
        }
        JdbcMappingVisitor jdbcMappingVisitor = query.getConfig().getDialect()
                .getJdbcMappingVisitor();
        if (entityType.isImmutable()) {
            Map<String, Object> values = new HashMap<String, Object>(
                    indexMap.size());
            for (Map.Entry<Integer, String> entry : indexMap.entrySet()) {
                Integer index = entry.getKey();
                String propertyName = entry.getValue();
                GetValueFunction function = new GetValueFunction(resultSet,
                        index);
                EntityPropertyType<E, ?> propertyType = entityType
                        .getEntityPropertyType(propertyName);
                Wrapper<?> wrapper = propertyType.getWrapper();
                wrapper.accept(jdbcMappingVisitor, function);
                values.put(propertyName, wrapper.get());
            }
            // TODO
            return entityType.newEntity(values);
        }
        E entity = entityType
                .newEntity(Collections.<String, Object> emptyMap());
        for (Map.Entry<Integer, String> entry : indexMap.entrySet()) {
            Integer index = entry.getKey();
            String propertyName = entry.getValue();
            GetValueFunction function = new GetValueFunction(resultSet, index);
            EntityPropertyType<E, ?> propertyType = entityType
                    .getEntityPropertyType(propertyName);
            Wrapper<?> wrapper = propertyType.getWrapper(entity);
            wrapper.accept(jdbcMappingVisitor, function);
        }
        entityType.saveCurrentStates(entity);
        return entity;
    }

    protected HashMap<Integer, String> createIndexMap(
            ResultSetMetaData resultSetMeta, EntityType<E> entityType)
            throws SQLException {
        HashMap<Integer, String> indexMap = new HashMap<Integer, String>();
        HashMap<String, String> columnNameMap = createColumnNameMap(entityType);
        int count = resultSetMeta.getColumnCount();
        for (int i = 1; i < count + 1; i++) {
            String columnName = resultSetMeta.getColumnLabel(i);
            String lowerCaseColumnName = columnName.toLowerCase();
            String propertyName = columnNameMap.get(lowerCaseColumnName);
            if (propertyName == null) {
                if (ROWNUMBER_COLUMN_NAME.equals(lowerCaseColumnName)) {
                    continue;
                }
                Sql<?> sql = query.getSql();
                NamingType namingType = entityType.getNamingType();
                throw new MappedPropertyNotFoundException(query.getConfig()
                        .getExceptionSqlLogType(), columnName,
                        namingType.revert(columnName), entityType
                                .getEntityClass().getName(), sql.getKind(),
                        sql.getRawSql(), sql.getFormattedSql(),
                        sql.getSqlFilePath());
            }
            indexMap.put(i, propertyName);
        }
        return indexMap;
    }

    protected HashMap<String, String> createColumnNameMap(
            EntityType<E> entityType) {
        List<EntityPropertyType<E, ?>> propertyTypes = entityType
                .getEntityPropertyTypes();
        HashMap<String, String> result = new HashMap<String, String>(
                propertyTypes.size());
        for (EntityPropertyType<E, ?> p : propertyTypes) {
            String columnName = p.getColumnName();
            result.put(columnName.toLowerCase(), p.getName());
        }
        return result;
    }

}