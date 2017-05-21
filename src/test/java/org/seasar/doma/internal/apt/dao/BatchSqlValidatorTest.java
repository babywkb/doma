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
package org.seasar.doma.internal.apt.dao;

import java.util.LinkedHashMap;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import org.seasar.doma.internal.apt.AptException;
import org.seasar.doma.internal.apt.AptTestCase;
import org.seasar.doma.internal.apt.BatchSqlValidator;
import org.seasar.doma.internal.jdbc.sql.SqlParser;
import org.seasar.doma.jdbc.SqlNode;
import org.seasar.doma.message.Message;

/**
 * @author taedium
 * 
 */
public class BatchSqlValidatorTest extends AptTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addSourcePath("src/main/java");
        addSourcePath("src/test/java");
    }

    public void testEmbeddedVariable() throws Exception {
        Class<?> target = BatchSqlValidationDao.class;
        addCompilationUnit(target);
        addProcessor(new AptProcessor(env -> {
            ExecutableElement methodElement = createMethodElement(env, target,
                    "testEmbeddedVariable", String.class);
            LinkedHashMap<String, TypeMirror> parameterTypeMap = createParameterTypeMap(
                    methodElement);
            BatchSqlValidator validator = new BatchSqlValidator(
                    env, methodElement, parameterTypeMap,
                    "aaa/bbbDao/ccc.sql", false, false);
            SqlParser parser = new SqlParser(
                    "select * from emp /*# orderBy */");
            SqlNode sqlNode = parser.parse();
            sqlNode.accept(validator, null);
        }));
        compile();
        assertTrue(getCompiledResult());
        assertMessage(Message.DOMA4181);
    }

    public void testEmbeddedVariableSuppressed() throws Exception {
        Class<?> target = BatchSqlValidationDao.class;
        addCompilationUnit(target);
        addProcessor(new AptProcessor(env -> {
            ExecutableElement methodElement = createMethodElement(env, target,
                    "testEmbeddedVariableSuppressed", String.class);
            LinkedHashMap<String, TypeMirror> parameterTypeMap = createParameterTypeMap(
                    methodElement);
            BatchSqlValidator validator = new BatchSqlValidator(env,
                    methodElement, parameterTypeMap, "aaa/bbbDao/ccc.sql",
                    false, false);
            SqlParser parser = new SqlParser(
                    "select * from emp /*# orderBy */");
            SqlNode sqlNode = parser.parse();
            sqlNode.accept(validator, null);
        }));
        compile();
        assertTrue(getCompiledResult());
        assertNoMessage();
    }

    public void testIf() throws Exception {
        Class<?> target = BatchSqlValidationDao.class;
        addCompilationUnit(target);
        addProcessor(new AptProcessor(env -> {
            ExecutableElement methodElement = createMethodElement(env, target,
                    "testIf");
            LinkedHashMap<String, TypeMirror> parameterTypeMap = createParameterTypeMap(
                    methodElement);
            BatchSqlValidator validator = new BatchSqlValidator(env,
                    methodElement, parameterTypeMap, "aaa/bbbDao/ccc.sql",
                    false, false);
            SqlParser parser = new SqlParser(
                    "select * from emp where /*%if true*/ id = 1 /*%end */");
            SqlNode sqlNode = parser.parse();
            sqlNode.accept(validator, null);
        }));
        compile();
        assertTrue(getCompiledResult());
        assertMessage(Message.DOMA4182);
    }

    public void testIfSuppressed() throws Exception {
        Class<?> target = BatchSqlValidationDao.class;
        addCompilationUnit(target);
        addProcessor(new AptProcessor(env -> {
            ExecutableElement methodElement = createMethodElement(env, target,
                    "testIfSuppressed");
            LinkedHashMap<String, TypeMirror> parameterTypeMap = createParameterTypeMap(
                    methodElement);
            BatchSqlValidator validator = new BatchSqlValidator(
                    env, methodElement, parameterTypeMap,
                    "aaa/bbbDao/ccc.sql", false, false);
            SqlParser parser = new SqlParser(
                    "select * from emp where /*%if true*/ id = 1 /*%end */");
            SqlNode sqlNode = parser.parse();
            sqlNode.accept(validator, null);
        }));
        compile();
        assertTrue(getCompiledResult());
        assertNoMessage();
    }

    public void testIfAndEmbeddedVariable() throws Exception {
        Class<?> target = BatchSqlValidationDao.class;
        addCompilationUnit(target);
        addProcessor(new AptProcessor(env -> {
            ExecutableElement methodElement = createMethodElement(env, target,
                    "testIfAndEmbeddedVariable", String.class);
            LinkedHashMap<String, TypeMirror> parameterTypeMap = createParameterTypeMap(
                    methodElement);
            BatchSqlValidator validator = new BatchSqlValidator(env,
                    methodElement, parameterTypeMap, "aaa/bbbDao/ccc.sql",
                    false, false);
            SqlParser parser = new SqlParser(
                    "select * from emp where /*%if true*/ id = 1 /*%end */ /*# orderBy */");
            SqlNode sqlNode = parser.parse();
            sqlNode.accept(validator, null);
        }));
        compile();
        assertTrue(getCompiledResult());
        assertEquals(2, getDiagnostics().size());
    }

    public void testIfAndEmbeddedVariableSuppressed() throws Exception {
        Class<?> target = BatchSqlValidationDao.class;
        addCompilationUnit(target);
        addProcessor(new AptProcessor(env -> {
            ExecutableElement methodElement = createMethodElement(env, target,
                    "testIfAndEmbeddedVariableSuppressed", String.class);
            LinkedHashMap<String, TypeMirror> parameterTypeMap = createParameterTypeMap(
                    methodElement);
            BatchSqlValidator validator = new BatchSqlValidator(
                    env, methodElement, parameterTypeMap,
                    "aaa/bbbDao/ccc.sql", false, false);
            SqlParser parser = new SqlParser(
                    "select * from emp where /*%if true*/ id = 1 /*%end */ /*# orderBy */");
            SqlNode sqlNode = parser.parse();
            sqlNode.accept(validator, null);
        }));
        compile();
        assertTrue(getCompiledResult());
        assertNoMessage();
    }

    public void testPopulate() throws Exception {
        Class<?> target = BatchSqlValidationDao.class;
        addCompilationUnit(target);
        addProcessor(new AptProcessor(env -> {
            ExecutableElement methodElement = createMethodElement(env, target,
                    "testPopulate", String.class);
            LinkedHashMap<String, TypeMirror> parameterTypeMap = createParameterTypeMap(
                    methodElement);
            BatchSqlValidator validator = new BatchSqlValidator(
                    env, methodElement, parameterTypeMap,
                    "aaa/bbbDao/ccc.sql", false, true);
            SqlParser parser = new SqlParser(
                    "update emp set /*%populate*/ id = id");
            SqlNode sqlNode = parser.parse();
            sqlNode.accept(validator, null);
        }));
        compile();
        assertTrue(getCompiledResult());
        assertNoMessage();
    }

    public void testPopulate_noPopulatable() throws Exception {
        Class<?> target = BatchSqlValidationDao.class;
        addCompilationUnit(target);
        addProcessor(new AptProcessor(env -> {
            ExecutableElement methodElement = createMethodElement(env, target,
                    "testPopulate", String.class);
            LinkedHashMap<String, TypeMirror> parameterTypeMap = createParameterTypeMap(
                    methodElement);
            BatchSqlValidator validator = new BatchSqlValidator(env,
                    methodElement, parameterTypeMap, "aaa/bbbDao/ccc.sql",
                    false, false);
            SqlParser parser = new SqlParser(
                    "update emp set /*%populate*/ id = id");
            SqlNode sqlNode = parser.parse();
            try {
                sqlNode.accept(validator, null);
                fail();
            } catch (AptException expected) {
                System.out.println(expected.getMessage());
                assertEquals(Message.DOMA4270, expected.getMessageResource());
            }
        }));
        compile();
        assertTrue(getCompiledResult());
    }

}
