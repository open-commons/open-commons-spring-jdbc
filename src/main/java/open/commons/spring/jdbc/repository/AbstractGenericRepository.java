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
 * Date  : 2021. 11. 26. 오후 12:45:07
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import open.commons.core.Result;
import open.commons.core.annotation.ColumnDef;
import open.commons.core.annotation.ColumnValue;
import open.commons.core.database.ConnectionCallbackBroker2;
import open.commons.core.database.DefaultConCallbackBroker2;
import open.commons.core.database.annotation.TableDef;
import open.commons.core.function.SQLConsumer;
import open.commons.core.function.SQLTripleFunction;
import open.commons.core.util.ArrayItr;
import open.commons.core.utils.ArrayUtils;
import open.commons.core.utils.AssertUtils;
import open.commons.core.utils.ExceptionUtils;
import open.commons.core.utils.ObjectUtils;
import open.commons.core.utils.SQLUtils;
import open.commons.core.utils.ThreadUtils;
import open.commons.spring.jdbc.dao.AbstractGenericDao;
import open.commons.spring.jdbc.repository.annotation.JdbcVariableBinder;
import open.commons.spring.jdbc.repository.annotation.JdbcVariableBinder.WhereCompare;
import open.commons.spring.jdbc.repository.exceptions.UnsupportedVariableBindingException;

/**
 * DBMS Table Entity에 기반하여 공통 기능을 제공하는 클래스.
 * 
 * @param <T>
 *            DBMS Table에 연결된 데이터 타입.
 * 
 * @since 2021. 11. 26.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 * 
 * @see TableDef
 * @see ColumnDef
 * @see ColumnValue
 */
public abstract class AbstractGenericRepository<T> extends AbstractGenericDao implements IGenericRepository<T> {

    /** DB Table 데이터 타입. */
    protected final Class<T> entityType;
    /**
     * 테이블 이름<br>
     *
     * @see TableDef
     * @see TableDef#table()
     */
    protected final String tableName;

    /**
     * 1개의 데이터를 추가하는 쿼리.<br>
     * 패턴:
     * <code>INSERT INTO {table-name} ( {comma-separated-column-names} ) VALUES ( {comma-separated-question-marks} )</code>
     * 
     * @see #queryForInsert()
     */
    protected final String QUERY_FOR_INSERT;

    /**
     * 전체 데이터를 조회하는 쿼리.<br>
     * 패턴: <code>SEELCT * FROM {table-name}</code>
     * 
     * @see #queryForSelect()
     */
    protected final String QUERY_FOR_SELECT;

    /**
     * 여러 개 데이터를 추가하는 쿼리 중 테이블 및 데이터 선언 관련 쿼리<br>
     * 
     * @see #queryForPartitionHeader()
     */
    protected final String QUERY_FOR_PARTITION_HEADER;
    /**
     * 여러 개 데이터를 추가하는 쿼리 중 데이터 연결(Variable Binding) 관련 쿼리<br>
     * 
     * @see #queryForPartitionValue()
     */
    protected final String QUERY_FOR_PARTITION_VALUE;
    /**
     * 여러 개 데이터를 추가하는 쿼리 중 데이터 연결(Variable Binding) 쿼리를 이어주는 쿼리<br>
     * 
     * @see #queryForPartitionConcatValue()
     */
    protected final String QUERY_FOR_PARTITION_CONCAT_VQ;
    /**
     * 여러 개 데이터를 추가하는 쿼리 중 마지막 쿼리<br>
     * 
     * @see #queryForPartitionTail()
     */
    protected final String QUERY_FOR_PARTITION_TAIL;

    /**
     * 데이터를 삭제하는 쿼리의 테이블 선언 관련 쿼리<br>
     * 패턴: <code>DELETE FROM {table-name}</code>
     * 
     * @see #queryForDeleteHeader()
     */
    protected final String QUERY_FOR_DELETE_HEADER;

    /**
     * 데이터를 변경하는 쿼리의 테이블 선언 관련 쿼리<br>
     * 패턴: <code>UPDATE {table-name}</code>
     * 
     * @see #queryForUpdateHeader()
     */
    protected final String QUERY_FOR_UPDATE_HEADER;

    /** Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부. */
    protected final boolean forceToPrimitive;

    /**
     * 데이터 개수를 제공하는 쿼리.<br>
     * 패턴: <code>SEELCT count(*) AS count FROM {table-name}</code>
     */
    protected final String QUERY_FOR_COUNT;

    /** 데이터를 제공하는 메소드가 없는 경우 로그 미발생 여부 */
    protected final boolean ignoreNoDataMethod;

    /**
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 6.		박준홍			최초 작성
     * </pre>
     * 
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @since 2021. 12. 6.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #AbstractGenericRepository(Class, boolean, boolean)
     */
    public AbstractGenericRepository(@NotNull Class<T> entityType) {
        this(entityType, true, true);
    }

    /**
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     * 
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #AbstractGenericRepository(Class, boolean, boolean)
     */
    public AbstractGenericRepository(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        this(entityType, forceToPrimitive, true);
    }

    /**
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2023. 8. 24.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     * @param ignoreNoDataMethod
     *            데이터를 제공하는 메소드가 없는 경우 로그 미발생 여부
     *
     * @since 2023. 8. 24.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractGenericRepository(@NotNull Class<T> entityType, boolean forceToPrimitive, boolean ignoreNoDataMethod) {

        this.entityType = entityType;
        this.tableName = getTableName();

        this.QUERY_FOR_INSERT = queryForInsert();
        this.QUERY_FOR_SELECT = queryForSelect();

        this.QUERY_FOR_PARTITION_HEADER = queryForPartitionHeader();
        this.QUERY_FOR_PARTITION_VALUE = queryForPartitionValue();
        this.QUERY_FOR_PARTITION_CONCAT_VQ = queryForPartitionConcatValue();
        this.QUERY_FOR_PARTITION_TAIL = queryForPartitionTail();

        this.QUERY_FOR_DELETE_HEADER = queryForDeleteHeader();
        this.QUERY_FOR_UPDATE_HEADER = queryForUpdateHeader();

        this.forceToPrimitive = forceToPrimitive;

        this.QUERY_FOR_COUNT = String.join(" ", "SELECT count(*) AS count FROM", getTableName());

        this.ignoreNoDataMethod = ignoreNoDataMethod;
    }

    /**
     * 주어진 조건에 맞는 데이터를 조회하는 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     *
     * @param queryHeader
     *            데이터 조회 쿼리.
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected void addOffsetClause(StringBuffer queryBuf, @Min(0) int offset, @Min(1) int limit) {
        queryBuf.append(" ");
        queryBuf.append(queryForOffset(offset, limit));
    }

    /**
     * 기존 쿼리에 정렬 기준 구문을 추가하여 반환합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     *
     * @param queryHeader
     *            데이터 조회 쿼리.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected void addOrderByClause(StringBuffer queryBuf, String... orderByArgs) {
        queryBuf.append(" ");
        queryBuf.append(createOrderByClause(orderByArgs));
    }

    /**
     * 주어진 파라미터를 이용하여 생성한 데이터 변경 쿼리를 제공합니다. <br>
     * 패턴: <code>{query} SET {column} = {variable-binding-query} (, {column} = {variable-binding-query})*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     * 
     * @param queryHeader
     *            중요 쿼리
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected void addSetClause(StringBuffer queryBuf) {

        List<ColumnValue> columns = getUpdatableColumnValues();

        if (columns.size() < 1) {
            throw new IllegalArgumentException(String.format("데이터를 변경할 컬럼 정보가 존재하지 않습니다. entity={}", this.entityType));
        }

        queryBuf.append(" ");
        queryBuf.append(createSetClause(columns));

        logger.debug("Query: {}", queryBuf.toString());
    }

    /**
     * 주어진 파라미터를 이용하여 생성한 데이터 조회 쿼리를 제공합니다. <br>
     * 패턴: <code>{query} WHERE {column} = {variable-binding-query} (AND {column} = {variable-binding-query})*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     * 
     * @param queryHeader
     *            중요 쿼리
     * @param method
     *            사용자 정의 메소드 정보
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected void addWhereClause(StringBuffer queryBuf, @NotNull Method method, Object... whereArgs) {

        List<JdbcVariableBinder> columns = getVariableBinders(method);

        if (hasNoWhereCompares(columns, WhereCompare.IN, WhereCompare.NOT_IN) && columns.size() != whereArgs.length) {
            logger.info("쿼리에 사용될 컬럼 개수({})와 파라미터 개수({})가 일치하지 않습니다.", columns.size(), whereArgs.length);
        }

        queryBuf.append(" ");
        queryBuf.append(createWhereClause(columns, "AND", whereArgs));
    }

    /**
     * 주어진 조건에 맞는 데이터를 조회하는 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     *
     * @param queryHeader
     *            데이터 조회 쿼리.
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String attachOffsetClause(String queryHeader, @Min(0) int offset, @Min(1) int limit) {

        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(queryHeader);
        queryBuf.append(" ");
        queryBuf.append(queryForOffset(offset, limit));

        return queryBuf.toString();
    }

    /**
     * 기존 쿼리에 정렬 기준 구문을 추가하여 반환합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     *
     * @param queryHeader
     *            데이터 조회 쿼리.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String attachOrderByClause(String queryHeader, String... orderByArgs) {

        StringBuffer sqlBuf = new StringBuffer();

        sqlBuf.append(queryHeader);
        sqlBuf.append(createOrderByClause(orderByArgs));

        return sqlBuf.toString();
    }

    /**
     * 주어진 파라미터를 이용하여 생성한 데이터 변경 쿼리를 제공합니다. <br>
     * 패턴: <code>{query} SET {column} = {variable-binding-query} (, {column} = {variable-binding-query})*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     * 
     * @param queryHeader
     *            중요 쿼리
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String attachSetClause(String queryHeader) {

        List<ColumnValue> columns = getUpdatableColumnValues();

        if (columns.size() < 1) {
            throw new IllegalArgumentException(String.format("데이터를 변경할 컬럼 정보가 존재하지 않습니다. entity={}", this.entityType));
        }

        return String.join(" ", queryHeader, createSetClause(columns));
    }

    /**
     * 주어진 파라미터를 이용하여 생성한 데이터 조회 쿼리를 제공합니다. <br>
     * 패턴: <code>{query} WHERE {column} = {variable-binding-query} (AND {column} = {variable-binding-query})*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     * 
     * @param queryHeader
     *            중요 쿼리
     * @param method
     *            사용자 정의 메소드 정보
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String attachWhereClause(String queryHeader, @NotNull Method method, Object... whereArgs) {

        List<JdbcVariableBinder> columns = getVariableBinders(method);

        if (hasNoWhereCompares(columns, WhereCompare.IN, WhereCompare.NOT_IN) && columns.size() != whereArgs.length) {
            logger.info("쿼리에 사용될 컬럼 개수({})와 파라미터 개수({})가 일치하지 않습니다.", columns.size(), whereArgs.length);
        }

        return String.join(" ", queryHeader, createWhereClause(columns, "AND", whereArgs));
    }

    /**
     * 주어진 파라미터에 <code>null</code>이 포함되어 있는지 여부를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param parameters
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final boolean containsNull(Object... parameters) {
        return ObjectUtils.containsNull(parameters);
    }

    /**
     *
     * @since 2021. 12. 28.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#countAll()
     */
    @Override
    public Result<Integer> countAll() {
        return executeCountOf(QUERY_FOR_COUNT);
    }

    /**
     *
     * @since 2022. 2. 11.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#countBy(java.util.Map)
     */
    @Override
    public Result<Integer> countBy(@NotNull Map<String, Object> clmnParams) {

        StringBuffer queryBuf = createQueryForSelectBy(clmnParams);
        Object[] params = createParametersForSelectBy(clmnParams);

        logger.debug("Query={}, params={}", queryBuf.toString(), Arrays.toString(params));

        return getCount(queryBuf.toString(), params);
    }

    /**
     * 주어진 조건에 해당하는 데이터 개수를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     *
     * @since 2021. 12. 28.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> countBy(@NotNull Method method, Object... whereArgs) {

        String query = createQueryForCountOf(QUERY_FOR_COUNT, method, whereArgs);

        logger.debug("Query: {}, where.columns={}", query, Arrays.toString(whereArgs));

        return executeCountOf(query, whereArgs);
    }

    /**
     * 주어진 조건에 해당하는 데이터 개수를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     *
     * @since 2021. 12. 28.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> countBy(Object... whereArgs) {
        return countBy(getCurrentMethod(1, whereArgs), whereArgs);
    }

    /**
     * 데이터 생성에 사용될 {@link ConnectionCallbackBroker2}를 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected final ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>> createBrokerForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs) {

        String query = createQueryForInsertOrNothing(data, method, whereArgs);
        Object params = createParametersForInsertOrNothing(data, method, whereArgs);
        SQLConsumer<PreparedStatement> setter = SQLConsumer.setParameters(params);

        logger.debug("query={}, data={}", query, params);

        return new DefaultConCallbackBroker2(query, setter, false);
    }

    /**
     * 데이터 생성에 사용될 {@link ConnectionCallbackBroker2}를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected final ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>> createBrokerForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs) {
        String query = createQueryForInsertOrUpdate(data, method, whereArgs);
        Object params = createParametersForInsertOrUpdate(data, method, whereArgs);
        SQLConsumer<PreparedStatement> setter = SQLConsumer.setParameters(params);

        logger.debug("query={}, data={}", query, params);

        return new DefaultConCallbackBroker2(query, setter, false);
    }

    /**
     * 컬럼에 값을 설정하는 쿼리를 제공합니다. <br>
     * 패턴: <code>{column} = {variable-binding-query} ( AND {column} = {variable-binding-query} )*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 1.		박준홍			최초 작성
     * 2022. 7. 14.     박준홍         반환 데이터 추가.
     * </pre>
     *
     * @param buf
     *            쿼리 버퍼
     * @param concat
     *            컬럼 설정쿼리 연결 문자열
     * @param columns
     *            컬럼 정보
     * 
     * @return 전달받은 쿼리 버퍼
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected StringBuffer createColumnAssignQueries(StringBuffer buf, String concat, List<ColumnValue> columns) {

        // variable binding
        Iterator<ColumnValue> itr = columns.iterator();
        buf.append(getAssignQuery(itr.next()));

        while (itr.hasNext()) {
            buf.append(" ");
            buf.append(concat);
            buf.append(" ");
            buf.append(getAssignQuery(itr.next()));
        }

        return buf;
    }

    /**
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @param data
     * @param partitionSize
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final <E> ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] createInsertBrokers(List<E> data, int partitionSize) {

        logger.debug("query.header={}, query.value={}, query.tail={}, data.size={}", QUERY_FOR_PARTITION_HEADER, QUERY_FOR_PARTITION_VALUE, QUERY_FOR_PARTITION_TAIL, data.size());

        return createConnectionCallbackBrokers(data, SQLTripleFunction.setParameters(), partitionSize //
                , QUERY_FOR_PARTITION_HEADER //
                , QUERY_FOR_PARTITION_VALUE //
                , QUERY_FOR_PARTITION_CONCAT_VQ //
                , QUERY_FOR_PARTITION_TAIL);
    }

    /**
     * 정렬을 위한 Order By 구분을 생성하여 제공합니다. <br>
     * 패턴: <code>ORDER BY {column} {direction}(, {column} {direction})*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     *
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String createOrderByClause(String... orderByArgs) {
        if (orderByArgs == null || orderByArgs.length < 1) {
            return "";
        }

        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(" ORDER BY ");

        ArrayItr<String> itr = new ArrayItr<>(orderByArgs);
        sqlBuf.append(itr.next());

        while (itr.hasNext()) {
            sqlBuf.append(", ");
            sqlBuf.append(itr.next());
        }

        return sqlBuf.toString();
    }

    /**
     * 데이터 생성에 사용될 파라미터를 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Object createParametersForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs) {
        return data;
    }

    /**
     * 데이터 생성에 사용될 파라미터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Object createParametersForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs) {
        return data;
    }

    private Object[] createParametersForSelectBy(Map<String, Object> clmnParams, Object... added) {
        return clmnParams.size() > 0 //
                ? ArrayUtils.add(clmnParams.values().toArray(new Object[0]), added) //
                : added;
    }

    /**
     * 주어진 조건에 해당하는 데이터 개수를 제공합니다.
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param selectQuery
     *            데이터 조회 쿼리.
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     *
     * @since 2021. 12. 28.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String createQueryForCountOf(@NotEmpty String selectQuery, @NotNull Method method, Object... whereArgs) {

        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(selectQuery);

        addWhereClause(queryBuf, method, whereArgs);

        return queryBuf.toString();
    }

    /**
     * 데이터 생성에 사용될 Query를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String createQueryForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs);

    /**
     * 데이터 생성에 사용될 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String createQueryForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs);

    /**
     * 데이터 정렬과 Pagination을 지원하는 쿼리를 제공합니다.<br>
     * 이 메소드를 호출하는 <code>DAO</code> 구현 클래스의 메소드의 파라미터는 다음과 같아야 합니다.
     * 
     * <ul>
     * <li>1 (*) : 쿼리 파라미터
     * <li>2 (int.class) : 데이터 시작 위치.
     * <li>3 (int.class) : 데이터 개수
     * <li>4 (String[].class) : 정렬 정보
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 2. 10.		박준홍			최초 작성
     * 2023. 8. 7.      박준홍         메소드 거리값 증가 누락 버그 수정
     * </pre>
     * 
     * @param distance
     *            <code>DAO</code> 구현 클래스의 메소드로부터 호출 거리.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * 
     * @return
     *
     * @since 2022. 2. 10.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String createQueryForOrderByForPagination(int distance, Object[] whereArgs, int offset, int limit, String... orderByArgs) {
        return createQueryForOrderByQueryForPagination(QUERY_FOR_SELECT, distance + 1, whereArgs, offset, limit, orderByArgs);
    }

    /**
     * 데이터 정렬과 Pagination을 지원하는 쿼리를 제공합니다.<br>
     * 이 메소드를 호출하는 <code>DAO</code> 구현 클래스의 메소드의 파라미터는 다음과 같아야 합니다.
     * 
     * <ul>
     * <li>1 (*) : 쿼리 파라미터
     * <li>2 (int.class) : 데이터 시작 위치.
     * <li>3 (int.class) : 데이터 개수
     * <li>4 (String[].class) : 정렬 정보
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     * 
     * @param queryForSelect
     *            데이터 조회 쿼리.
     * @param distance
     *            <code>DAO</code> 구현 클래스의 메소드로부터 호출 거리.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     *
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    @SuppressWarnings("unchecked")
    protected String createQueryForOrderByQueryForPagination(String queryForSelect, int distance, Object[] whereArgs, int offset, int limit, String... orderByArgs) {

        Class<?>[] parameterTypes = ArrayUtils.add(ObjectUtils.readClasses(this.forceToPrimitive, whereArgs), int.class, int.class, String[].class);

        Method method = getCurrentMethod(distance + 1, parameterTypes);

        return attachOffsetClause( //
                createQueryForSelectOrderBy(queryForSelect, method, whereArgs, orderByArgs) //
                , offset, limit);
    }

    /**
     * 주어진 컬럼/데이터 정보를 이용하여 조회쿼리를 생성합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 2. 11.		박준홍			최초 작성
     * </pre>
     *
     * @param clmnParams
     *            검색조건(컬럼이름과 데이터, 모두 'AND' 연산 처리됨).
     * @return
     *
     * @since 2022. 2. 11.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    private StringBuffer createQueryForSelectBy(Map<String, Object> clmnParams) {
        return createQueryForSelectBy(queryForSelect(), clmnParams);
    }

    /**
     * 주어진 컬럼/데이터 정보를 이용하여 조회쿼리를 생성합니다. <br>
     * 
     * <pre>
     * 
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     * @param clmnParams
     *            검색조건(컬럼이름과 데이터, 모두 'AND' 연산 처리됨).
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    private StringBuffer createQueryForSelectBy(String query, Map<String, Object> clmnParams) {
        StringBuffer queryBuf = new StringBuffer(query);

        if (clmnParams.size() > 0) {
            // 컬럼
            Iterator<String> clmns = clmnParams.keySet().iterator();
            queryBuf.append(" WHERE ");
            queryBuf.append(clmns.next());
            queryBuf.append(" = ? ");
            while (clmns.hasNext()) {
                queryBuf.append(" AND ");
                queryBuf.append(clmns.next());
                queryBuf.append(" = ? ");
            }
        }

        return queryBuf;
    }

    /**
     * 여러 개의 데이터를 제공하는 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     * 
     * @param selectQuery
     *            데이터 조회 쿼리.
     * @param method
     *            사용자 정의 메소드
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String createQueryForSelectForPagination(@NotEmpty String selectQuery, @NotNull Method method, @Min(0) int offset, @Min(1) int limit, Object... whereArgs) {

        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(selectQuery);

        addWhereClause(queryBuf, method, whereArgs);
        addOffsetClause(queryBuf, offset, limit);

        return queryBuf.toString();
    }

    /**
     * 데이터를 선택하는 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     * 
     * @param selectQuery
     *            데이터 조회 쿼리.
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     *
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String createQueryForSelectOrderBy(@NotEmpty String selectQuery, @NotNull Method method, Object[] whereArgs, String... orderByArgs) {

        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(selectQuery);

        addWhereClause(queryBuf, method, whereArgs);
        addOrderByClause(queryBuf, orderByArgs);

        return queryBuf.toString();
    }

    /**
     * 주어진 컬럼값을 변경하는 'Set' 구문을 제공합니다. <br>
     * 패턴: <code>SET {column} = ? (, {column} = ? )*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @param columns
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String createSetClause(@NotNull List<ColumnValue> columns) {

        StringBuffer buf = new StringBuffer();

        if (columns.size() > 1) {
            buf.append("SET");
            buf.append(" ");

            createColumnAssignQueries(buf, ",", columns);
        }

        return buf.toString();
    }

    /**
     * 주어진 컬럼명으로 '{컬럼 접속자}'로 연결된 Where 구문을 제공합니다. <br>
     * 패턴:
     * <code>WHERE {column} {concatenator} {variable-binding-query} ( AND {column} {concatenator} {variable-binding-query} )*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @param columns
     * @param concatenator
     *            Where 구문 컬럼 접속자
     * @param whereArgs
     *            파라미터 개수
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String createWhereClause(@NotNull List<JdbcVariableBinder> columns, String concatenator, Object... whereArgs) {

        if (columns.size() < 1) {
            return "";
        }

        StringBuffer buf = new StringBuffer();

        buf.append("WHERE");
        buf.append(" ");

        Iterator<JdbcVariableBinder> itr = columns.iterator();

        int paramCount = whereArgs.length;
        int posParam = 0;

        JdbcVariableBinder vbNow = itr.next();
        buf.append(getAssignQuery(vbNow, posParam, whereArgs));
        paramCount--;

        boolean hasNext = true;
        while (hasNext) {
            posParam++;
            JdbcVariableBinder vbLatest = vbNow;
            switch (vbLatest.operator()) {
                case IN:
                case NOT_IN:
                    if (itr.hasNext()) {
                        throw new UnsupportedVariableBindingException(
                                String.format("'IN' 구문 이후에 다른 연산자가 오는 경우는 지원하지 않습니다. 연산자=%s", columns.stream().map(c -> c.operator().get()).collect(Collectors.toList())));
                    }
                    for (int i = 0; i < paramCount; i++) {
                        buf.append(", ");
                        buf.append(" ?");
                    }
                    buf.append(")");
                    hasNext = false;
                    break;
                default:
                    if (!itr.hasNext()) {
                        hasNext = false;
                        break;
                    }

                    vbNow = itr.next();
                    buf.append(" ");
                    buf.append(concatenator);
                    buf.append(" ");
                    buf.append(getAssignQuery(vbNow, posParam, whereArgs));
                    paramCount--;
                    break;
            }
        }

        return buf.toString();
    }

    /**
     * 데이터를 삭제합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @param <V>
     * @param method
     *            사용자 정의 메소드 정보
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> deleteBy(@NotNull Method method, Object... whereArgs) {
        String query = attachWhereClause(QUERY_FOR_DELETE_HEADER, method, whereArgs);

        logger.debug("Query: {}, data={}", query, Arrays.toString(whereArgs));

        return executeUpdate(query, SQLConsumer.setParameters(whereArgs));
    }

    /**
     * 데이터를 삭제합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 3.        박준홍         최초 작성
     * </pre>
     *
     * @param <V>
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> deleteBy(Object... whereArgs) {
        return deleteBy(getCurrentMethod(1, whereArgs), whereArgs);
    }

    /**
     *
     * @since 2021. 12. 24.
     * @version 0.3.0
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
     * @since 2021. 12. 24.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#executeUpdate(java.util.List,
     *      open.commons.function.SQLTripleFunction, int, java.lang.String, java.lang.String)
     * 
     * @deprecated Use {@link AbstractGenericRepository#insert(List, int)}
     */
    @Override
    public <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String headerQuery, @NotNull String valueQuery) {
        throw new UnsupportedOperationException("#insert(List<T>, int) 를 사용하세요.");
    }

    /**
     * 컬럼에 값을 설정하는 쿼리를 제공합니다. <br>
     * 패턴: <code>{column-name} = {variable-binding-query}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 1.		박준홍			최초 작성
     * </pre>
     *
     * @param cv
     * @return
     *
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final String getAssignQuery(@NotNull ColumnValue cv) {
        return String.join(" = ", getColumnName(cv), cv.variableBinding());
    }

    /**
     * 컬럼에 값을 설정하는 쿼리를 제공합니다. <br>
     * 패턴: <code>{column-name} = {variable-binding-query}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 1.		박준홍			최초 작성
     * </pre>
     * 
     * @param vb
     * @param posParam
     *            현재 파라미터 위치
     * @param whereArgs
     *            전체 파라미터
     * @return
     *
     * @since 2021. 12. 13.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final String getAssignQuery(JdbcVariableBinder vb, int posParam, Object... whereArgs) {

        StringBuffer buf = new StringBuffer();

        buf.append(vb.name());
        buf.append(" ");

        switch (vb.operator()) {
            case EQ:
            case GE:
            case GT:
            case LE:
            case LT:
            case NOT:
                buf.append(vb.operator().get());
                buf.append(" ");
                buf.append(vb.variableBinding());
                break;
            case IN:
            case NOT_IN:
                buf.append(vb.operator().get());
                buf.append(" (");
                buf.append(vb.variableBinding());
                break;
            case LIKE:
                buf.append(vb.operator().get());
                buf.append(" ");
                buf.append(vb.variableBinding());
                whereArgs[posParam] = String.join((String) whereArgs[posParam], "%", "%");
                break;
            case LIKE_PRE:
                buf.append(vb.operator().get());
                buf.append(" ");
                buf.append(vb.variableBinding());
                whereArgs[posParam] = String.join((String) whereArgs[posParam], "%", "");
                break;
            case LIKE_POST:
                buf.append(vb.operator().get());
                buf.append(" ");
                buf.append(vb.variableBinding());
                whereArgs[posParam] = String.join((String) whereArgs[posParam], "", "%");
                break;
            case NOT_LIKE:
                buf.append(vb.operator().get());
                buf.append(" ");
                buf.append(vb.variableBinding());
                whereArgs[posParam] = String.join((String) whereArgs[posParam], "%", "%");
                break;
            case NOT_LIKE_PRE:
                buf.append(vb.operator().get());
                buf.append(" ");
                buf.append(vb.variableBinding());
                whereArgs[posParam] = String.join((String) whereArgs[posParam], "%", "");
                break;
            case NOT_LIKE_POST:
                buf.append(vb.operator().get());
                buf.append(" ");
                buf.append(vb.variableBinding());
                whereArgs[posParam] = String.join((String) whereArgs[posParam], "", "%");
                break;
            case IS_NOT_NULL:
                buf.append(vb.operator().get());
                break;
            default:
                throw new UnsupportedOperationException(String.format("지원하지 않는 연산자입니다. 입력=%s", vb.operator()));
        }

        return buf.toString();
    }

    /**
     * 컬럼 데이터를 제공하는 {@link Method} 목록을 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    private final List<Method> getColumnMethods() {

        List<Method> methods = Arrays.stream(this.entityType.getMethods()) // create methods stream
                .filter(m -> {
                    ColumnValue annoCv = m.getAnnotation(ColumnValue.class);
                    // start - 컬럼 생성시 정의된 'default' 속성에 따라서 생성되는 컬럼을 제외 : 2022. 11. 1. 오후 2:28:00
                    return annoCv != null && !annoCv.defaultColumn();
                    // end - 컬럼 생성시 정의된 'default' 속성에 따라서 생성되는 컬럼을 제외 : 2022. 11. 1. 오후 2:28:00

                }) // check annotation
                .collect(Collectors.toList());

        // start - 데이터 제공 메소드 미정의 허용 : 2022. 3. 23. 오후 2:54:34
        if (methods.size() < 1) {
            if (!this.ignoreNoDataMethod) {
                logger.warn("DBMS Table에 연결된 Entity에서 데이터 제공 함수를 발견하지 못하였습니다. entity={}", this.entityType);
            }
            return methods;
        } else {
        }
        // end - 데이터 제공 메소드 미정의 허용 : 2022. 3. 23. 오후 2:54:34

        // DB Entity 객체의 컬럼 정렬 여부 적용 - 2022. 1. 7. 오전 11:44:06 / Park_Jun_Hong (parkjunhong77@gmail.com)
        SQLUtils.sortColumns(this.entityType, methods);

        return methods;
    }

    /**
     * {@link ColumnValue#name()}이 기본값 (빈 문자열)일 경우를 {@link Method#getName()}값을 이용하여 컬럼명을 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 25.		박준홍			최초 작성
     * </pre>
     *
     * @param clmnValue
     * @return
     *
     * @since 2022. 11. 25.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    private String getColumnName(@NotNull ColumnValue clmnValue) {

        Optional<Method> opt = getUpdatableColumnsAsStream().filter(m -> clmnValue.equals(m.getAnnotation(ColumnValue.class))).findAny();

        if (opt.isPresent()) {
            return SQLUtils.getColumnName(clmnValue, opt.get());
        } else {
            throw ExceptionUtils.newException(IllegalArgumentException.class, "'%s'에 해당하는 Method가 존재하지 않습닏.", clmnValue);
        }
    }

    /**
     * 컬럼명을 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected final List<String> getColumnNames() {
        return getColumnsAsStream() //
                .map(m -> SQLUtils.getColumnName(m)) //
                .collect(Collectors.toList());
    }

    /**
     * 컬럼 데이터를 제공하는 {@link Method} 목록을 제공합니다.<br>
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 7. 14.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2022. 7. 14.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected final List<Method> getColumns() {
        return getColumnsAsStream() //
                .collect(Collectors.toList());
    }

    /**
     * 컬럼 데이터를 제공하는 {@link Method} 목록을 {@link Stream}으로 제공합니다.<br>
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 7. 14.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2022. 7. 14.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected final Stream<Method> getColumnsAsStream() {
        return getColumnMethods().stream();
    }

    /**
     * 컬럼에 해당하는 데이터를 제공합니다. 파라미터로 컬럼이 없는 경우 전체 컬럼 데이터를 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 7. 14.     박준홍         최초 작성
     * </pre>
     *
     * @param data
     *            데이터
     * @param clmns
     *            컬럼명.
     * @return
     *
     * @since 2022. 7. 14.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     * 
     * @see SQLUtils#sortColumns(Class, List)
     * @see SQLUtils#isSortedColumns(Class)
     */
    protected Object[] getColumnValues(T data, List<String> clmns) {
        // 설정된 컬럼이 없는 경우
        if (clmns == null || clmns.isEmpty()) {
            return getColumnsAsStream() //
                    .map(m -> {
                        try {
                            return m.invoke(data);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            String errMsg = String.format("'%s' 정보를 변경하기 위한 정보를 생성 도중 에러가 발생하였습니다. 원인=%s", data.getClass(), e.getMessage());
                            logger.error(errMsg, e);
                            throw new UnsupportedOperationException(errMsg, e);
                        }
                    }).toArray();
        } else {
            Map<String, Method> clmnMethods = getColumnsAsStream() //
                    .collect(Collectors.toMap(m ->
                    // start - ColumnValue#name() 기본값 (빈 문자열 ("")) 지원 : 2022. 11. 25. 오후 6:12:34
                    SQLUtils.getColumnNameByColumnValue(m)
                    // end - ColumnValue#name() 기본값 (빈 문자열 ("")) 지원 : 2022. 11. 25. 오후 6:12:34
                            , m -> m));

            return clmns.stream() //
                    .filter(clmn -> clmnMethods.containsKey(clmn)) //
                    .map(clmn -> {
                        try {
                            return clmnMethods.get(clmn).invoke(data);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            String errMsg = String.format("'%s' 정보를 변경하기 위한 정보를 생성 도중 에러가 발생하였습니다. 원인=%s", data.getClass(), e.getMessage());
                            logger.error(errMsg, e);
                            throw new UnsupportedOperationException(errMsg, e);
                        }
                    }) //
                    .toArray();
        }
    }

    /**
     * 컬럼에 해당하는 데이터를 제공합니다. 파라미터로 컬럼이 없는 경우 전체 컬럼 데이터를 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 7. 14.		박준홍			최초 작성
     * </pre>
     *
     * @param data
     *            데이터
     * @param clmns
     *            컬럼명.
     * @return
     *
     * @since 2022. 7. 14.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     * 
     * @see SQLUtils#sortColumns(Class, List)
     * @see SQLUtils#isSortedColumns(Class)
     */
    protected Object[] getColumnValues(T data, String... clmns) {
        return getColumnValues(data, clmns != null ? Arrays.asList(clmns) : null);
    }

    /**
     * 이 메소드({@link #getCurrentMethod(Class...)})를 호출하는 메소드 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param parameterTypes
     *            <code>Caller</code> 메소드 파라미터 타입.
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #getCurrentMethod(int, Class...)
     */
    protected final Method getCurrentMethod(@NotEmpty Class<?>... parameterTypes) {
        return getCurrentMethod(1, parameterTypes);
    }

    /**
     * 이 메소드({@link #getCurrentMethod(int, Class...)})를 호출하는 메소드 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param distance
     *            이 메소드를 호출하는 함수와의 거리
     * @param parameterTypes
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final Method getCurrentMethod(int distance, @NotEmpty Class<?>... parameterTypes) {

        String name = ThreadUtils.getMethodName(distance + 1);

        try {
            return getClass().getMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            String errMsg = String.format("메소드 정보 추출 중 에러가 발생하였습니다. 이름=%s, 파라미터=%s, 원인=%s", name, Arrays.toString(parameterTypes), e.getMessage());
            logger.error(errMsg, e);
            throw new InternalError(errMsg, e);
        }
    }

    /**
     * 이 메소드({@link #getCurrentMethod(int, Object...)})를 호출하는 메소드 정보를 제공합니다. <br>
     * 파라미터에 <code>null</code>이 포함된 경우 예외를 발생시키며, 포함 여부는 {@link #containsNull(Object...)} 을 이용해서 확인할 수 있습니다.
     * <code>null</code>이 포함된 경우에는 {@link #getCurrentMethod(Class...)} 를 사용해야 합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param distance
     *            이 메소드를 호출하는 함수와의 거리
     * @param parameterTypes
     *            <code>Caller</code> 메소드 파라미터
     * @return
     * @throws IllegalArgumentException
     *             파라미터에 <code>null</code> 이 포함된 경우.
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #getCurrentMethod(int, Class...)
     */
    protected final Method getCurrentMethod(int distance, @NotEmpty Object... parameters) throws IllegalArgumentException {
        AssertUtils.assertNulls("Class 정보를 추출하기 위한 데이터에 'null'이 포함될 수 없습니다.", IllegalArgumentException.class, parameters);

        return getCurrentMethod(distance + 1, ObjectUtils.readClasses(this.forceToPrimitive, parameters));
    }

    /**
     * 이 메소드({@link #getCurrentMethod(Object...)})를 호출하는 메소드 정보를 제공합니다. <br>
     * 파라미터에 <code>null</code>이 포함된 경우 예외를 발생시키며, 포함 여부는 {@link #containsNull(Object...)} 을 이용해서 확인할 수 있습니다.
     * <code>null</code>이 포함된 경우에는 {@link #getCurrentMethod(Class...)} 를 사용해야 합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param parameterTypes
     *            <code>Caller</code> 메소드 파라미터
     * @return
     * @throws IllegalArgumentException
     *             파라미터에 <code>null</code> 이 포함된 경우.
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #getCurrentMethod(int, Object...)
     */
    protected final Method getCurrentMethod(@NotEmpty Object... parameters) throws IllegalArgumentException {
        return getCurrentMethod(1, parameters);
    }

    /**
     * Entity의 컬럼 정보를 {@link Iterator} 형태로 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 1.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected List<ColumnValue> getEntityColumnValues() {
        return getColumnMethods().stream() //
                .map(m -> m.getAnnotation(ColumnValue.class)) //
                .collect(Collectors.toList());
    }

    /**
     *
     * @since 2021. 12. 6.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#getEntityType()
     */
    @Override
    public Class<T> getEntityType() {
        return this.entityType;
    }

    /**
     * 여러 개의 데이터를 저장하는 경우 한번에 저장할 데이터 개수를 반환합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected int getPartitionSize() {
        return 100;
    }

    /**
     *
     * @since 2021. 12. 7.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#getTableName()
     */
    @Override
    public String getTableName() {
        TableDef tblAnno = this.entityType.getAnnotation(TableDef.class);
        AssertUtils.assertNull("DBMS Table에 연결된 Entity 정의가 존재하지 않습니다.", tblAnno, UnsupportedOperationException.class);
        return tblAnno.table();
    }

    /**
     * 변경 대상인 컬럼 목록을 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final List<String> getUpdatableColumnNames() {
        return getUpdatableColumnsAsStream().map(m -> SQLUtils.getColumnNameByColumnValue(m)).collect(Collectors.toList());
    }

    /**
     * 변경 대상인 컬럼 데이터를 제공하는 {@link Method} 목록을 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final List<Method> getUpdatableColumns() {
        return getUpdatableColumnsAsStream() //
                .collect(Collectors.toList());
    }

    /**
     * 변경 대상인 컬럼 데이터를 제공하는 {@link Method} 목록을 {@link Stream}으로 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 14.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 12. 14.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final Stream<Method> getUpdatableColumnsAsStream() {
        return getColumnMethods().stream() //
                .filter(m -> m.getAnnotation(ColumnValue.class).updatable());
    }

    /**
     * 변경 대상인 컬럼 정보를 {@link Iterator}형태로 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 1.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final List<ColumnValue> getUpdatableColumnValues() {
        return getUpdatableColumnsAsStream().map(m -> m.getAnnotation(ColumnValue.class)) //
                .collect(Collectors.toList());
    }

    /**
     * 데이터 갱신에 사용될 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 1.		박준홍			최초 작성
     * </pre>
     *
     * @param data
     * @return
     *
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Object[] getUpdateParameters(T data) {
        return getUpdatableColumns().stream() //
                .map(m -> {
                    try {
                        return m.invoke(data);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        String errMsg = String.format("'%s' 정보를 변경하기 위한 정보를 생성 도중 에러가 발생하였습니다. 원인=%s", data.getClass(), e.getMessage());
                        logger.error(errMsg, e);
                        throw new UnsupportedOperationException(errMsg, e);
                    }
                }).toArray();
    }

    /**
     * 메소드 파라미터에에서 'Where'절에 사용될 컬럼정보을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 1.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @return
     *
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    private List<JdbcVariableBinder> getVariableBinders(@NotNull Method method) {

        List<JdbcVariableBinder> whereColumns = getVariableBindingParametersAsStream(method) //
                .map(p -> p.getAnnotation(JdbcVariableBinder.class)) //
                .collect(Collectors.toList());

        return whereColumns;
    }

    /**
     * 메소드 파라미터에서 'Where'절에 사용될 컬럼목록을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected List<String> getVariableBindingColumnNames(@NotNull Method method) {

        List<String> columnNames = getVariableBindingParametersAsStream(method) //
                .map(p -> p.getAnnotation(JdbcVariableBinder.class).name()) //
                .collect(Collectors.toList());

        return columnNames;
    }

    /**
     * 주어진 메소드 파라미터 중에 {@link JdbcVariableBinder} 어노테이션이 설정되어 있는 파라미터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected List<Parameter> getVariableBindingParameters(@NotNull Method method) {
        return getVariableBindingParametersAsStream(method).collect(Collectors.toList());
    }

    /**
     * 주어진 메소드 파라미터 중에 {@link JdbcVariableBinder} 어노테이션이 설정되어 있는 파라미터를 {@link Stream} 형태로 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 14.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     * @return
     *
     * @since 2021. 12. 14.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Stream<Parameter> getVariableBindingParametersAsStream(@NotNull Method method) {
        return Arrays.stream(method.getParameters()) //
                .filter(param -> param.isAnnotationPresent(JdbcVariableBinder.class));
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#insert(java.util.List)
     */
    @Override
    public Result<Integer> insert(List<T> data) {
        return insert(data, getPartitionSize());
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#insert(java.util.List, int)
     */
    @Override
    public Result<Integer> insert(List<T> data, @Min(1) int partitionSize) {
        ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] brokers = createInsertBrokers(data, partitionSize);
        return executeUpdate(brokers);
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#insert(java.lang.Object)
     */
    @Override
    public Result<Integer> insert(T data) {

        logger.debug("query={}, data={}", QUERY_FOR_INSERT, data);

        return executeUpdate(QUERY_FOR_INSERT, SQLConsumer.setParameters(data));
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 아무런 동작을 하지 않습니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 2.		박준홍			최초 작성
     * </pre>
     *
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 2.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    @SuppressWarnings("unchecked")
    protected Result<Integer> insertOrNothingBy(T data, @NotNull Method method, Object... whereArgs) {
        ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>> broker = createBrokerForInsertOrNothing(data, method, whereArgs);
        return executeUpdate(broker);
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 아무런 동작을 하지 않습니다.<br>
     * 
     * 파라미터 중에 2번째({@link Object} ...)은 DBMS에 따라 구현할 때 사용되지 않을 수도 있습니다.
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 2.		박준홍			최초 작성
     * </pre>
     *
     * @param data
     *            추가할 데이터
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 2.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> insertOrNothingBy(T data, Object... whereArgs) {
        return insertOrNothingBy(data, getCurrentMethod(1, ArrayUtils.objectArray(data, whereArgs)), whereArgs);
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 아무런 동작을 하지 않습니다.<br>
     * 
     * 파라미터 중에 2번째({@link Method}), 3번째({@link Object} ...)은 DBMS에 따라 구현할 때 사용되지 않을 수도 있습니다.
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 7. 13.     박준홍         최초 작성
     * </pre>
     *
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 7. 13.
     * @version 2.0.0
     * @author parkjunhong77@gmail.com
     */
    @SuppressWarnings("unchecked")
    protected Result<Integer> insertOrUpdateBy(T data, @NotNull Method method, Object... whereArgs) {
        ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>> broker = createBrokerForInsertOrUpdate(data, method, whereArgs);
        return executeUpdate(broker);
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 설정된 데이터를 갱신합니다. <br>
     * 
     * 파라미터 중에 2번째({@link Object} ...)은 DBMS에 따라 구현할 때 사용되지 않을 수도 있습니다.
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 7. 14.		박준홍			최초 작성
     * </pre>
     *
     * @param data
     *            추가할 데이터
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 7. 14.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> insertOrUpdateBy(T data, Object... whereArgs) {
        return insertOrUpdateBy(data, getCurrentMethod(1, ArrayUtils.objectArray(data, whereArgs)), whereArgs);
    }

    /**
     * 컬럼이름을 콤마(,)로 연결시킨 문자열을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #getColumnNames()
     */
    protected String queryForColumnNames() {
        return String.join(", ", getColumnNames().toArray(new String[0]));
    }

    /**
     * 데이터를 삭제하는 쿼리의 테이블 선언 관련 쿼리를 제공합니다.<br>
     * 패턴: <code>DELETE FROM {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #queryForDeleteHeader(String)
     */
    protected String queryForDeleteHeader() {
        return queryForDeleteHeader(this.tableName);
    }

    /**
     * 데이터를 삭제하는 쿼리의 테이블 선언 관련 쿼리를 제공합니다.<br>
     * 패턴: <code>DELETE FROM {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param tableName
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForDeleteHeader(String tableName) {
        return new StringBuffer() //
                .append("DELETE FROM") //
                .append(" ") //
                .append(tableName) //
                .toString();
    }

    /**
     * 1개의 데이터를 추가하는 쿼리를 제공합니다.<br>
     * 패턴:
     * <code>INSERT INTO {table-name} ( {comma-separated-column-names} ) VALUES ( {comma-separated-question-marks} )</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #queryForInsert(String)
     */
    protected String queryForInsert() {
        return queryForInsert(this.tableName);
    }

    /**
     * 1개의 데이터를 추가하는 쿼리를 제공합니다.<br>
     * 패턴:
     * <code>INSERT INTO {table-name} ( {comma-separated-column-names} ) VALUES ( {comma-separated-question-marks} )</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param tableName
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForInsert(String tableName) {
        return new StringBuffer() //
                .append("INSERT INTO") //
                .append(" ") //
                .append(tableName) //
                .append(" (")//
                .append(queryForColumnNames()) //
                .append(") ") //
                .append("VALUES") //
                .append(" (")//
                .append(queryForVariableBinding()) //
                .append(") ") //
                .toString();
    }

    /**
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @param offset
     *            데이터 읽는 시작 위치 ('0'부터 시작)
     * @param limit
     *            데이터 개수
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String queryForOffset(@Min(0) int offset, @Min(1) int limit);

    /**
     * 여러 개의 데이터를 추가하는 쿼리의 '데이터 쿼리' 연결 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String queryForPartitionConcatValue();

    /**
     * 여러 개의 데이터를 추가하는 쿼리의 '헤더' 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String queryForPartitionHeader();

    /**
     * 여러 개의 데이터를 추가하는 쿼리의 '마지막' 쿼리/정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String queryForPartitionTail();

    /**
     * 여러 개의 데이터를 추가하는 쿼리의 '데이터' 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String queryForPartitionValue();

    /**
     * 전체 데이터를 조회하는 쿼리를 제공합니다. <br>
     * 패턴: <code>SEELCT * FROM {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see #queryForSelect(String)
     */
    protected String queryForSelect() {
        return queryForSelect(this.tableName);
    }

    /**
     * 전체 데이터를 조회하는 쿼리를 제공합니다. <br>
     * 패턴: <code>SEELCT * FROM {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param tableName
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForSelect(@NotEmpty String tableName) {
        return new StringBuffer() //
                .append("SELECT") //
                .append(" ") //
                .append("*") //
                .append(" ") //
                .append("FROM") //
                .append(" ") //
                .append(tableName) //
                .toString();
    }

    /**
     * 데이터를 변경하는 쿼리의 테이블 선언 관련 쿼리를 제공합니다.<br>
     * 패턴: <code>UPDATE {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * 2022. 11. 15.        박준홍     내구 구현을 {@link #queryForUpdateHeader(String)}를 이용.
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see #queryForUpdateHeader(String)
     */
    protected String queryForUpdateHeader() {
        return queryForUpdateHeader(this.tableName);
    }

    /**
     * 데이터를 변경하는 쿼리의 테이블 선언 관련 쿼리를 제공합니다.<br>
     * 패턴: <code>UPDATE {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param tableName
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForUpdateHeader(@NotEmpty String tableName) {
        return new StringBuffer() //
                .append("UPDATE") //
                .append(" ") //
                .append(tableName) //
                .toString();
    }

    /**
     * 컬럼 개수에 맞는 '?'로만 구성된 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForVariableBinding() {

        Iterator<ColumnValue> itr = getEntityColumnValues().iterator();

        final StringBuffer queryBuf = new StringBuffer();

        if (itr.hasNext()) {
            queryBuf.append(itr.next().variableBinding());

            while (itr.hasNext()) {
                queryBuf.append(", ");
                queryBuf.append(itr.next().variableBinding());
            }
        }

        return queryBuf.toString();
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectAll()
     */
    @Override
    public Result<List<T>> selectAll() {

        logger.debug("Query: {}", QUERY_FOR_SELECT);

        return getList(QUERY_FOR_SELECT, SQLConsumer.setParameters(), this.entityType);
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectAll(int, int)
     */
    @Override
    public Result<List<T>> selectAll(@Min(0) int offset, @Min(1) int limit) {
        return selectAllByQuery(QUERY_FOR_SELECT, offset, limit);
    }

    /**
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectAll(int, int, java.lang.String[])
     */
    @Override
    public Result<List<T>> selectAll(@Min(0) int offset, @Min(1) int limit, String... orderByArgs) {
        return selectAllByQuery(QUERY_FOR_SELECT, offset, limit, orderByArgs);
    }

    /**
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectAll(java.lang.String[])
     */
    @Override
    public Result<List<T>> selectAll(String... orderByArgs) {
        return selectAllByQuery(QUERY_FOR_SELECT, orderByArgs);
    }

    /**
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectAllByQuery(java.lang.String)
     */
    @Override
    public Result<List<T>> selectAllByQuery(@NotEmpty String queryForSelect) {

        logger.debug("Query: {}", queryForSelect);

        return getList(QUERY_FOR_SELECT, SQLConsumer.setParameters(), this.entityType);
    }

    /**
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectAllByQuery(java.lang.String, int, int)
     */
    @Override
    public Result<List<T>> selectAllByQuery(@NotEmpty String queryForSelect, @Min(0) int offset, @Min(1) int limit) {

        String addedQuery = attachOffsetClause(queryForSelect, offset, limit);

        logger.debug("Query: {}, offset={}, limit={}", addedQuery, offset, limit);

        return getList(addedQuery, SQLConsumer.setParameters(offset, limit), this.entityType);
    }

    /**
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectAllByQuery(java.lang.String, int, int,
     *      java.lang.String[])
     */
    @Override
    public Result<List<T>> selectAllByQuery(String queryForSelect, @Min(0) int offset, @Min(1) int limit, String... orderByArgs) {

        StringBuffer queryBuf = new StringBuffer();

        queryBuf.append(queryForSelect);
        addOrderByClause(queryBuf, orderByArgs);
        addOffsetClause(queryBuf, offset, limit);

        logger.debug("Query: {}, offset={}, limit={}", queryBuf.toString(), offset, limit);

        return getList(queryBuf.toString(), SQLConsumer.setParameters(offset, limit), this.entityType);
    }

    /**
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectAllByQuery(java.lang.String,
     *      java.lang.String[])
     */
    @Override
    public Result<List<T>> selectAllByQuery(String queryForSelect, String... orderByArgs) {

        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(queryForSelect);
        addOrderByClause(queryBuf, orderByArgs);

        logger.debug("Query: {}", queryBuf.toString());

        return getList(queryBuf.toString(), SQLConsumer.setParameters(), this.entityType);
    }

    /**
     *
     * @since 2022. 2. 11.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectBy(java.util.Map, int, int, java.lang.String[])
     */
    @Override
    public Result<List<T>> selectBy(@NotNull Map<String, Object> clmnParams, int offset, int limit, String... orderByArgs) {
        return selectByQuery(queryForSelect(), clmnParams, offset, limit, orderByArgs);
    }

    /**
     *
     * @since 2022. 2. 11.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectBy(java.util.Map, java.lang.String[])
     */
    @Override
    public Result<List<T>> selectBy(@NotNull Map<String, Object> clmnParams, String... orderByArgs) {
        return selectByQuery(queryForSelect(), clmnParams, orderByArgs);
    }

    /**
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectByQuery(java.lang.String, java.util.Map, int,
     *      int, java.lang.String[])
     */
    @Override
    public Result<List<T>> selectByQuery(String queryForSelect, @NotNull Map<String, Object> clmnParams, int offset, int limit, String... orderByArgs) {

        StringBuffer queryBuf = createQueryForSelectBy(queryForSelect, clmnParams);

        addOrderByClause(queryBuf, orderByArgs);
        addOffsetClause(queryBuf, offset, limit);

        Object[] params = createParametersForSelectBy(clmnParams, offset, limit);

        logger.debug("Query={}, params={}", queryBuf.toString(), Arrays.toString(params));

        return getList(queryBuf.toString(), SQLConsumer.setParameters(params), getEntityType());
    }

    /**
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#selectByQuery(java.lang.String, java.util.Map,
     *      java.lang.String[])
     */
    @Override
    public Result<List<T>> selectByQuery(String queryForSelect, @NotNull Map<String, Object> clmnParams, String... orderByArgs) {

        StringBuffer queryBuf = createQueryForSelectBy(queryForSelect, clmnParams);

        addOrderByClause(queryBuf, orderByArgs);

        Object[] params = createParametersForSelectBy(clmnParams);

        logger.debug("Query={}, params={}", queryBuf.toString(), Arrays.toString(params));

        return getList(queryBuf.toString(), SQLConsumer.setParameters(params), getEntityType());
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     * 
     * @see #selectMultiByQuery(String, Method, Object...)
     */
    protected Result<List<T>> selectMultiBy(@NotNull Method method, Object... whereArgs) {
        return selectMultiByQuery(QUERY_FOR_SELECT, method, whereArgs);

    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 30.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2021. 11. 30.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     * @see #selectMultiByQuery(String, Method, Object[], String...)
     */
    protected Result<List<T>> selectMultiBy(@NotNull Method method, Object[] whereArgs, String... columnNames) {
        return selectMultiByQuery(QUERY_FOR_SELECT, method, whereArgs, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiBy(@NotNull Object... whereArgs) {
        return selectMultiBy(getCurrentMethod(1, whereArgs), whereArgs);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiBy(@NotNull Object[] whereArgs, String... columnNames) {
        return selectMultiBy(getCurrentMethod(1, whereArgs), whereArgs, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiByForPagination(@NotNull Method method, @Min(0) int offset, @Min(1) int limit, Object... whereArgs) {
        return selectMultiByQueryForPagination(QUERY_FOR_SELECT, method, offset, limit, whereArgs);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiByForPagination(@NotNull Method method, @Min(0) int offset, @Min(1) int limit, Object[] whereArgs, String... columnNames) {
        return selectMultiByQueryForPagination(QUERY_FOR_SELECT, method, offset, limit, whereArgs, columnNames);

    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     * 
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiByQuery(@NotEmpty String queryForSelect, @NotNull Method method, Object... whereArgs) {

        String query = attachWhereClause(queryForSelect, method, whereArgs);

        logger.debug("Query: {}, where.columns={}", query, Arrays.toString(whereArgs));

        return getList(query, SQLConsumer.setParameters(whereArgs), this.entityType);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     * 
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     *
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiByQuery(@NotEmpty String queryForSelect, @NotNull Method method, Object[] whereArgs, String... columnNames) {

        String query = attachWhereClause(queryForSelect, method, whereArgs);

        logger.debug("Query: {}, where.columns={}", query, Arrays.toString(whereArgs));

        return getList(query, SQLConsumer.setParameters(whereArgs), this.entityType, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiByQuery(@NotEmpty String queryForSelect, @NotNull Object... whereArgs) {
        return selectMultiByQuery(queryForSelect, getCurrentMethod(1, whereArgs), whereArgs);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiByQuery(@NotEmpty String queryForSelect, @NotNull Object[] whereArgs, String... columnNames) {
        return selectMultiBy(queryForSelect, getCurrentMethod(1, whereArgs), whereArgs, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param method
     *            사용자 정의 메소드
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiByQueryForPagination(@NotEmpty String queryForSelect, @NotNull Method method, @Min(0) int offset, @Min(1) int limit, Object... whereArgs) {

        String query = createQueryForSelectForPagination(queryForSelect, method, offset, limit, whereArgs);

        logger.debug("Query: {}", query);

        return getList(query, SQLConsumer.setParameters(ArrayUtils.objectArray(whereArgs, offset, limit)), this.entityType);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     * @param method
     *            사용자 정의 메소드
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiByQueryForPagination(@NotEmpty String queryForSelect, @NotNull Method method, @Min(0) int offset, @Min(1) int limit, Object[] whereArgs,
            String... columnNames) {

        String query = createQueryForSelectForPagination(queryForSelect, method, offset, limit, whereArgs);

        logger.debug("Query: {}, offset={}, limit={}", query, offset, limit);

        return getList(query, SQLConsumer.setParameters(ArrayUtils.objectArray(whereArgs, offset, limit)), this.entityType, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * 
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     * 
     * @see #selectMultiOrderByQuery(String, Method, Object[], String...)
     */
    protected Result<List<T>> selectMultiOrderBy(@NotNull Method method, Object[] whereArgs, String... orderByArgs) {
        return selectMultiOrderByQuery(QUERY_FOR_SELECT, method, whereArgs, orderByArgs);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiOrderBy(@NotNull Method method, Object[] whereArgs, String[] orderByArgs, String... columnNames) {
        return selectMultiOrderByQuery(QUERY_FOR_SELECT, method, whereArgs, orderByArgs, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     *
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiOrderBy(@NotNull Object[] whereArgs, String... orderByArgs) {

        Class<?>[] parameterTypes = ArrayUtils.add(ObjectUtils.readClasses(this.forceToPrimitive, whereArgs), String[].class);

        return selectMultiOrderBy(getCurrentMethod(1, parameterTypes), whereArgs, orderByArgs);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiOrderBy(@NotNull Object[] whereArgs, String[] orderByArgs, String... columnNames) {

        Class<?>[] parameterTypes = ArrayUtils.add(ObjectUtils.readClasses(this.forceToPrimitive, whereArgs), String[].class);

        return selectMultiOrderBy(getCurrentMethod(1, parameterTypes), whereArgs, orderByArgs, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiOrderByForPagination(@NotNull Method method, @Min(0) int offset, @Min(1) int limit, Object[] whereArgs, String... orderByArgs) {
        return selectMultiOrderByQueryForPagination(QUERY_FOR_SELECT, method, offset, limit, whereArgs, orderByArgs);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     *
     * @param method
     *            사용자 정의 메소드
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     * 
     * @see #selectMultiOrderByQueryForPagination(String, Method, int, int, Object[], String[], String...)
     */
    protected Result<List<T>> selectMultiOrderByForPagination(@NotNull Method method, @Min(0) int offset, @Min(1) int limit, Object[] whereArgs, String[] orderByArgs,
            String... columnNames) {
        return selectMultiOrderByQueryForPagination(QUERY_FOR_SELECT, method, offset, limit, whereArgs, orderByArgs, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 이 메소드를 호출하는 <code>DAO</code> 구현 클래스의 메소드의 파라미터는 다음과 같아야 합니다.
     * 
     * <ul>
     * <li>1 (*) : 쿼리 파라미터
     * <li>2 (int.class) : 데이터 시작 위치.
     * <li>3 (int.class) : 데이터 개수
     * <li>4 (String[].class) : 정렬 정보
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 2. 10.		박준홍			최초 작성
     * </pre>
     * 
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     *
     * @return
     *
     * @since 2022. 2. 10.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiOrderByForPagination(Object[] whereArgs, @Min(0) int offset, @Min(1) int limit, String... orderByArgs) {

        String query = createQueryForOrderByForPagination(1, whereArgs, offset, limit, orderByArgs);

        logger.debug("Query: {}, where.columns={}, offset={}, limit={}", query, Arrays.toString(whereArgs), offset, limit);

        return getList(query, SQLConsumer.setParameters(ArrayUtils.objectArray(whereArgs, offset, limit)), this.entityType);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 이 메소드를 호출하는 <code>DAO</code> 구현 클래스의 메소드의 파라미터는 다음과 같아야 합니다.
     * 
     * <ul>
     * <li>1 (*) : 쿼리 파라미터
     * <li>2 (int.class) : 데이터 시작 위치.
     * <li>3 (int.class) : 데이터 개수
     * <li>4 (String[].class) : 정렬 정보
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 2. 10.		박준홍			최초 작성
     * </pre>
     * 
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     *
     * @return
     *
     * @since 2022. 2. 10.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiOrderByForPagination(Object[] whereArgs, @Min(0) int offset, @Min(1) int limit, String[] orderByArgs, String... columnNames) {

        String query = createQueryForOrderByForPagination(1, whereArgs, offset, limit, orderByArgs);

        logger.debug("Query: {}, offset={}, limit={}", query, offset, limit);

        return getList(query, SQLConsumer.setParameters(ArrayUtils.objectArray(whereArgs, offset, limit)), this.entityType, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiOrderByQuery(@NotEmpty String queryForSelect, @NotNull Method method, Object[] whereArgs, String... orderByArgs) {

        String query = createQueryForSelectOrderBy(queryForSelect, method, whereArgs, orderByArgs);

        logger.debug("Query: {}", query);

        return getList(query, SQLConsumer.setParameters(whereArgs), this.entityType);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiOrderByQuery(@NotEmpty String queryForSelect, @NotNull Method method, Object[] whereArgs, String[] orderByArgs, String... columnNames) {

        String query = createQueryForSelectOrderBy(queryForSelect, method, whereArgs, orderByArgs);

        logger.debug("Query: {}", query);

        return getList(query, SQLConsumer.setParameters(whereArgs), this.entityType, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiOrderByQuery(@NotEmpty String queryForSelect, @NotNull Object[] whereArgs, String... orderByArgs) {

        Class<?>[] parameterTypes = ArrayUtils.add(ObjectUtils.readClasses(this.forceToPrimitive, whereArgs), String[].class);

        return selectMultiOrderByQuery(queryForSelect, getCurrentMethod(1, parameterTypes), whereArgs, orderByArgs);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiOrderByQuery(@NotEmpty String queryForSelect, @NotNull Object[] whereArgs, String[] orderByArgs, String... columnNames) {

        Class<?>[] parameterTypes = ArrayUtils.add(ObjectUtils.readClasses(this.forceToPrimitive, whereArgs), String[].class);

        return selectMultiOrderByQuery(queryForSelect, getCurrentMethod(1, parameterTypes), whereArgs, orderByArgs, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param method
     *            사용자 정의 메소드
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiOrderByQueryForPagination(@NotEmpty String queryForSelect, @NotNull Method method, @Min(0) int offset, @Min(1) int limit,
            Object[] whereArgs, String... orderByArgs) {

        String query = attachOffsetClause( //
                createQueryForSelectOrderBy(queryForSelect, method, whereArgs, orderByArgs) //
                , offset, limit);

        logger.debug("Query: {}, where.columns={}, offset={}, limit={}", query, Arrays.toString(whereArgs), offset, limit);

        return getList(query, SQLConsumer.setParameters(ArrayUtils.objectArray(whereArgs, offset, limit)), this.entityType);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리.
     * @param method
     *            사용자 정의 메소드
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiOrderByQueryForPagination(@NotEmpty String queryForSelect, @NotNull Method method, @Min(0) int offset, @Min(1) int limit,
            Object[] whereArgs, String[] orderByArgs, String... columnNames) {

        String query = attachOffsetClause( //
                createQueryForSelectOrderBy(queryForSelect, method, whereArgs, orderByArgs) //
                , offset, limit);

        logger.debug("Query: {}, offset={}, limit={}", query, offset, limit);

        return getList(query, SQLConsumer.setParameters(ArrayUtils.objectArray(whereArgs, offset, limit)), this.entityType, columnNames);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 이 메소드를 호출하는 <code>DAO</code> 구현 클래스의 메소드의 파라미터는 다음과 같아야 합니다.
     * 
     * <ul>
     * <li>1 (*) : 쿼리 파라미터
     * <li>2 (int.class) : 데이터 시작 위치.
     * <li>3 (int.class) : 데이터 개수
     * <li>4 (String[].class) : 정렬 정보
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiOrderByQueryForPagination(@NotEmpty String queryForSelect, Object[] whereArgs, @Min(0) int offset, @Min(1) int limit,
            String... orderByArgs) {

        String query = createQueryForOrderByQueryForPagination(queryForSelect, 1, whereArgs, offset, limit, orderByArgs);

        logger.debug("Query: {}, where.columns={}, offset={}, limit={}", query, Arrays.toString(whereArgs), offset, limit);

        return getList(query, SQLConsumer.setParameters(ArrayUtils.objectArray(whereArgs, offset, limit)), this.entityType);
    }

    /**
     * 주어진 조건에 맞는 여러 개의 데이터를 제공합니다. <br>
     * 이 메소드를 호출하는 <code>DAO</code> 구현 클래스의 메소드의 파라미터는 다음과 같아야 합니다.
     * 
     * <ul>
     * <li>1 (*) : 쿼리 파라미터
     * <li>2 (int.class) : 데이터 시작 위치.
     * <li>3 (int.class) : 데이터 개수
     * <li>4 (String[].class) : 정렬 정보
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiOrderByQueryForPagination(@NotEmpty String queryForSelect, Object[] whereArgs, @Min(0) int offset, @Min(1) int limit, String[] orderByArgs,
            String... columnNames) {

        String query = createQueryForOrderByQueryForPagination(queryForSelect, 1, whereArgs, offset, limit, orderByArgs);

        logger.debug("Query: {}, offset={}, limit={}", query, offset, limit);

        return getList(query, SQLConsumer.setParameters(ArrayUtils.objectArray(whereArgs, offset, limit)), this.entityType, columnNames);
    }

    /**
     * 주어진 조건에 맞는 1개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     * 
     * @param required
     *            필수 존재 여부.
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     * 
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     */
    protected Result<T> selectSingleBy(boolean required, @NotNull Method method, Object... whereArgs) {
        return selectSingleByQuery(QUERY_FOR_SELECT, required, method, whereArgs);
    }

    /**
     * 주어진 조건에 맞는 1개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 30.		박준홍			최초 작성
     * </pre>
     * 
     * @param required
     *            필수 존재 여부.
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     *
     * @return
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2021. 11. 30.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<T> selectSingleBy(boolean required, @NotNull Method method, Object[] whereArgs, String... columnNames) {
        return selectSingleByQuery(QUERY_FOR_SELECT, required, method, whereArgs, columnNames);
    }

    /**
     * 주어진 조건에 맞는 1개의 데이터를 제공합니다. <br>
     * 이 메소드를 호출하는 <code>DAO</code> 구현 클래스의 메소드의 파라미터는 다음과 같아야 합니다.
     * 
     * <ul>
     * <li>1 (*) : 쿼리 파라미터
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     * 
     * @param required
     *            필수 존재 여부.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     */
    protected Result<T> selectSingleBy(boolean required, Object... whereArgs) {
        return selectSingleBy(required, getCurrentMethod(1, whereArgs), whereArgs);
    }

    /**
     * 주어진 조건에 맞는 1개의 데이터를 제공합니다. <br>
     * 이 메소드를 호출하는 <code>DAO</code> 구현 클래스의 메소드의 파라미터는 다음과 같아야 합니다.
     * 
     * <ul>
     * <li>1 (*) : 쿼리 파라미터
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     * 
     * @param required
     *            필수 존재 여부.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     *
     * @return
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<T> selectSingleBy(boolean required, Object[] whereArgs, String... columnNames) {
        return selectSingleBy(required, getCurrentMethod(1, whereArgs), whereArgs, columnNames);
    }

    /**
     * 주어진 조건에 맞는 1개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리.
     * @param required
     *            필수 존재 여부.
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     * 
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<T> selectSingleByQuery(@NotEmpty String queryForSelect, boolean required, @NotNull Method method, Object... whereArgs) {
        String query = attachWhereClause(queryForSelect, method, whereArgs);
        return getObject(query, SQLConsumer.setParameters(whereArgs), this.entityType, required);
    }

    /**
     * 주어진 조건에 맞는 1개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리
     * @param required
     *            필수 존재 여부.
     * @param method
     *            사용자 정의 메소드
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     *
     * @return
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<T> selectSingleByQuery(@NotEmpty String queryForSelect, boolean required, @NotNull Method method, Object[] whereArgs, String... columnNames) {
        String query = attachWhereClause(queryForSelect, method, whereArgs);
        return getObject(query, SQLConsumer.setParameters(whereArgs), this.entityType, required, columnNames);
    }

    /**
     * 주어진 조건에 맞는 1개의 데이터를 제공합니다. <br>
     * 이 메소드를 호출하는 <code>DAO</code> 구현 클래스의 메소드의 파라미터는 다음과 같아야 합니다.
     * 
     * <ul>
     * <li>1 (*) : 쿼리 파라미터
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리.
     * @param required
     *            필수 존재 여부.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<T> selectSingleByQuery(@NotEmpty String queryForSelect, boolean required, Object... whereArgs) {
        return selectSingleByQuery(queryForSelect, required, getCurrentMethod(1, whereArgs), whereArgs);
    }

    /**
     * 주어진 조건에 맞는 1개의 데이터를 제공합니다. <br>
     * 이 메소드를 호출하는 <code>DAO</code> 구현 클래스의 메소드의 파라미터는 다음과 같아야 합니다.
     * 
     * <ul>
     * <li>1 (*) : 쿼리 파라미터
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 11. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param queryForSelect
     *            데이터 조회 쿼리.
     * @param required
     *            필수 존재 여부.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param columnNames
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     *
     * @return
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<T> selectSingleByQuery(@NotEmpty String queryForSelect, boolean required, Object[] whereArgs, String... columnNames) {
        return selectSingleByQuery(queryForSelect, required, getCurrentMethod(1, whereArgs), whereArgs, columnNames);
    }

    /**
     * 주어진 조건에 맞는 데이터를 갱신합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     * 
     * @param data
     *            변경할 데이터 정보.
     * @param method
     *            사용자 정의 메소드 정보
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<Integer> updateBy(T data, @NotNull Method method, Object... whereArgs) {
        String querySet = attachSetClause(QUERY_FOR_UPDATE_HEADER);
        String query = attachWhereClause(querySet, method, whereArgs);
        Object[] params = ArrayUtils.objectArray(getUpdateParameters(data), whereArgs);

        logger.debug("Query: {}, data={}", query, Arrays.toString(params));

        return executeUpdate(query, SQLConsumer.setParameters(params));
    }

    /**
     * 주어진 조건에 맞는 데이터를 갱신합니다. <br>
     * 이 메소드({@link #updateBy(Object, Object...)})를 호출하는 메소드의 파라미터는 이 메소드의 파라미터 순서와 동일한 순서(갱신할 데이터, Where ...)이어야 합니다.
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 3.		박준홍			최초 작성
     * </pre>
     * 
     * @param data
     *            변경할 데이터 정보.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<Integer> updateBy(T data, Object... whereArgs) {
        return updateBy(data, getCurrentMethod(1, ArrayUtils.objectArray(data, whereArgs)), whereArgs);
    }

    protected static Object[] array(Object... any) {
        return any != null ? any : new Object[0];
    }

    /**
     * 주어진 JDBC Variable Binding 정보에 확인하려는 {@link WhereCompare}이 없는지 여부를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param binders
     *            JDBC Variable Biding 정보
     * @param compares
     *            확인하려는 Where Clause Compare 목록
     * @return
     *         <ul>
     *         <li>true: 없음
     *         <li>false: 있음
     *         </ul>
     *
     * @since 2021. 12. 15.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected static boolean hasNoWhereCompares(Collection<JdbcVariableBinder> binders, @NotNull WhereCompare... compares) {
        return !hasWhereCompares(binders, compares);
    }

    /**
     * 주어진 JDBC Variable Binding 정보에 확인하려는 {@link WhereCompare}이 있는지 여부를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 15.		박준홍			최초 작성
     * </pre>
     *
     * @param binders
     *            JDBC Variable Biding 정보
     * @param compares
     *            확인하려는 Where Clause Compare 목록
     * @return
     *         <ul>
     *         <li>true: 있음
     *         <li>false: 없음
     *         </ul>
     *
     * @since 2021. 12. 15.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected static boolean hasWhereCompares(Collection<JdbcVariableBinder> binders, @NotNull WhereCompare... compares) {
        List<WhereCompare> list = Arrays.asList(compares);
        return binders.stream().filter(c -> list.contains(c.operator())).findAny().isPresent();
    }
}
