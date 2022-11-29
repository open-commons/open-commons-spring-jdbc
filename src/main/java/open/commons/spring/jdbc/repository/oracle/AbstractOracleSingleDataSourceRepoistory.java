/*
 * Copyright 2021 Park Jun-Hong_(parkjunhong77@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 *
 * This file is generated under this project, "open-commons-spring-jdbc".
 *
 * Date  : 2021. 12. 16. 오후 4:47:21
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository.oracle;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import open.commons.core.Result;
import open.commons.core.function.SQLTripleFunction;
import open.commons.core.text.NamedTemplate;
import open.commons.spring.jdbc.repository.AbstractGenericRepository;
import open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository;

/**
 * Oracle DBMS 연동을 위한 클래스.
 * 
 * @since 2021. 12. 16.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
public abstract class AbstractOracleSingleDataSourceRepoistory<T> extends AbstractSingleDataSourceRepository<T> {

    protected static final String TN_TABLE_NAME = "TABLE_NAME";
    protected static final String TN_USING_DUAL_ON = "USING_DUAL_ON_CLAUSE";
    protected static final String TN_UPDATE_SET = "UPDATE_SET_CLAUSE";
    protected static final String TN_INSERT = "INSERT_CLAUSE";
    protected static final String QUERY_TPL_INSERT_OR_UPDATE = new StringBuilder() //
            .append("MERGE INTO") //
            .append(" {").append(TN_TABLE_NAME).append("} ") //
            .append("USING DUAL ON") //
            .append(" ") //
            // 'USING DUAL ON' 구문 작성
            .append(" {").append(TN_USING_DUAL_ON).append("} ") //
            .append(" ") //
            .append("WHEN MATCHED THEN") //
            .append(" ") //
            .append("UPDATE SET") //
            .append(" ") //
            // 'UPDATE SET' 구문 작성
            .append(" {").append(TN_UPDATE_SET).append("} ") //
            .append(" ") //
            .append("WHEN NOT MATCHED THEN") //
            .append(" ") //
            .append("INSERT") //
            .append(" ") //
            // 'INSERT' 구문 작성
            .append(" {").append(TN_INSERT).append("} ") //
            .toString();
    /**
     * 'INSERT or Nothing'
     * 
     * @version 0.4.0
     * @since 2022. 11. 2.
     */
    protected static final String QUERY_TPL_INSERT_OR_NOTHING = new StringBuilder() //
            .append("MERGE INTO") //
            .append(" {").append(TN_TABLE_NAME).append("} ") //
            .append("USING DUAL ON") //
            .append(" ") //
            // 'USING DUAL ON' 구문 작성
            .append(" {").append(TN_USING_DUAL_ON).append("} ") //
            .append(" ") //
            .append("WHEN NOT MATCHED THEN") //
            .append(" ") //
            .append("INSERT") //
            .append(" ") //
            // 'INSERT' 구문 작성
            .append(" {").append(TN_INSERT).append("} ") //
            .toString();

    protected static final String QUERY_FOR_OFFSET = "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 16.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractOracleSingleDataSourceRepoistory(@NotNull Class<T> entityType) {
        this(entityType, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 16.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     * @param forceToPrimitive
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractOracleSingleDataSourceRepoistory(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        super(entityType, forceToPrimitive);
    }

    /**
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createParametersForInsertOrNothing(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected Object createParametersForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs) {

        // #1. 'USING DUAL ON' 파라미터
        List<String> pkClmns = getVariableBindingColumnNames(method);
        Object[] paramsUsingDualOn = getColumnValues(data, pkClmns);
        // #2. 'INSERT' 파라미터
        List<String> insertColumn = getColumnNames();
        Object[] paramsInsert = getColumnValues(data, insertColumn);

        Object[] params = new Object[paramsUsingDualOn.length + paramsInsert.length];
        System.arraycopy(paramsUsingDualOn, 0, params, 0, paramsUsingDualOn.length);
        System.arraycopy(paramsInsert, 0, params, paramsUsingDualOn.length, paramsInsert.length);

        return params;

    }

    /**
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createParametersForInsertOrUpdate(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected Object createParametersForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs) {

        // #1. 'USING DUAL ON' 파라미터
        List<String> pkClmns = getVariableBindingColumnNames(method);
        Object[] paramsUsingDualOn = getColumnValues(data, pkClmns);
        // #2. 'UPDATE SET' 파라미터
        List<String> updatableClmns = getUpdatableColumnNames();
        Object[] paramsUpdateSet = getColumnValues(data, updatableClmns);
        // #3. 'INSERT' 파라미터
        List<String> insertColumn = getColumnNames();
        Object[] paramsInsert = getColumnValues(data, insertColumn);

        Object[] params = new Object[paramsUsingDualOn.length + paramsUpdateSet.length + paramsInsert.length];
        System.arraycopy(paramsUsingDualOn, 0, params, 0, paramsUsingDualOn.length);
        System.arraycopy(paramsUpdateSet, 0, params, paramsUsingDualOn.length, paramsUpdateSet.length);
        System.arraycopy(paramsInsert, 0, params, paramsUsingDualOn.length + paramsUpdateSet.length, paramsInsert.length);

        return params;
    }

    /**
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createQueryForInsertOrNothing(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected String createQueryForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs) {
        // #0. 쿼리 구문 선언
        NamedTemplate queryTpl = new NamedTemplate(QUERY_TPL_INSERT_OR_NOTHING);

        // #1. 'USING DUAL ON' 쿼리
        List<String> pkClmns = getVariableBindingColumnNames(method);
        String clauseUsingDualOn = String.join(" AND " //
                , pkClmns.stream() //
                        .map(clmn -> clmn + " = ?") //
                        .collect(Collectors.toList()) //
        );

        // #2. 'INSERT' 쿼리
        String clauseInsert = new StringBuffer() //
                .append(" (")//
                .append(queryForColumnNames()) //
                .append(") ") //
                .append("VALUES") //
                .append(" (")//
                .append(queryForVariableBinding()) //
                .append(") ") //
                .toString();

        // #3. 쿼리 & 파라미터 병합
        queryTpl.addValue(TN_TABLE_NAME, getTableName());
        queryTpl.addValue(TN_USING_DUAL_ON, clauseUsingDualOn);
        queryTpl.addValue(TN_INSERT, clauseInsert);

        return queryTpl.format();
    }

    /**
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createQueryForInsertOrUpdate(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected String createQueryForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs) {

        // #0. 쿼리 구문 선언
        NamedTemplate queryTpl = new NamedTemplate(QUERY_TPL_INSERT_OR_UPDATE);

        // #1. 'USING DUAL ON' 쿼리
        List<String> pkClmns = getVariableBindingColumnNames(method);
        String clauseUsingDualOn = String.join(" AND " //
                , pkClmns.stream() //
                        .map(clmn -> clmn + " = ?") //
                        .collect(Collectors.toList()) //
        );

        // #2. 'UPDATE SET' 쿼리
        String clauseUpdateSet = createColumnAssignQueries(new StringBuffer(), ",", getUpdatableColumnValues()).toString();

        // #3. 'INSERT' 쿼리
        String clauseInsert = new StringBuffer() //
                .append(" (")//
                .append(queryForColumnNames()) //
                .append(") ") //
                .append("VALUES") //
                .append(" (")//
                .append(queryForVariableBinding()) //
                .append(") ") //
                .toString();

        // #4. 쿼리 & 파라미터 병합
        queryTpl.addValue(TN_TABLE_NAME, getTableName());
        queryTpl.addValue(TN_USING_DUAL_ON, clauseUsingDualOn);
        queryTpl.addValue(TN_UPDATE_SET, clauseUpdateSet);
        queryTpl.addValue(TN_INSERT, clauseInsert);

        return queryTpl.format();
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#executeUpdate(java.util.List,
     *      open.commons.function.SQLTripleFunction, int, java.lang.String)
     * 
     * @deprecated Use {@link AbstractGenericRepository#insert(List, int)}
     */
    @Override
    public <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String valueQuery) {
        throw new UnsupportedOperationException("#insert(List<T>, int) 를 사용하세요.");
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForOffset(int, int)
     */
    @Override
    protected String queryForOffset(@Min(0) int offset, @Min(1) int limit) {
        return QUERY_FOR_OFFSET;
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionConcatValue()
     */
    @Override
    protected String queryForPartitionConcatValue() {
        return "";
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionHeader()
     */
    @Override
    protected String queryForPartitionHeader() {
        return "INSERT ALL";
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionTail()
     */
    @Override
    protected String queryForPartitionTail() {
        return "SELECT 1 FROM DUAL";
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionValue()
     */
    @Override
    protected String queryForPartitionValue() {

        List<String> columns = getColumnNames();

        return new StringBuffer()//
                .append("INTO ") //
                .append(getTableName()) //
                .append(" ( ") //
                .append(String.join(", ", columns.toArray(new String[0]))) //
                .append(" ) ") //
                .append("VALUES") //
                .append(" ( ") //
                .append(queryForVariableBinding()) //
                .append(" )") //
                .toString();
    }
}
