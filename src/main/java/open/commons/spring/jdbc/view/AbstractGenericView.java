/*
 * Copyright 2025 Park Jun-Hong_(parkjunhong77@gmail.com)
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
 * Date  : 2025. 5. 13. 오후 4:08:17
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
import open.commons.core.database.annotation.TableDef;
import open.commons.core.function.SQLConsumer;
import open.commons.core.function.TripleFunction;
import open.commons.core.util.ArrayItr;
import open.commons.core.utils.ArrayUtils;
import open.commons.core.utils.AssertUtils;
import open.commons.core.utils.CollectionUtils;
import open.commons.core.utils.ExceptionUtils;
import open.commons.core.utils.ObjectUtils;
import open.commons.core.utils.SQLUtils;
import open.commons.core.utils.StringUtils;
import open.commons.core.utils.ThreadUtils;
import open.commons.spring.jdbc.dao.AbstractGenericRetrieve;
import open.commons.spring.jdbc.repository.AbstractGenericRepository;
import open.commons.spring.jdbc.repository.IGenericRetrieve;
import open.commons.spring.jdbc.repository.annotation.JdbcVariableBinder;
import open.commons.spring.jdbc.repository.annotation.JdbcVariableBinder.WhereCompare;
import open.commons.spring.jdbc.repository.exceptions.UnsupportedVariableBindingException;

/**
 * {@link AbstractGenericRepository}에서 "SELECT" 관련 기능을 이관하여 정의한 클래스.
 * 
 * @param <T>
 *            DBMS Table에 연결된 데이터 타입.
 * 
 * @since 2025. 5. 13.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 * 
 * @see TableDef
 * @see ColumnDef
 * @see ColumnValue
 */
public abstract class AbstractGenericView<T> extends AbstractGenericRetrieve implements IGenericRetrieve<T> {

    /**
     * {@link Parameter}에 설정된 {@link JdbcVariableBinder}를 제공합니다.<br>
     * 
     * @param p
     *            메소드 파라미터
     * @return
     */
    private static final Function<Parameter, JdbcVariableBinder> PARAMETER_JDBC_VARIABLE_BINDER = p -> {
        return p.getAnnotation(JdbcVariableBinder.class);
    };

    /**
     * {@link Parameter}에 설정된 {@link JdbcVariableBinder#name()} 값을 제공합니다.<br>
     * 단, {@link JdbcVariableBinder#name()}값이 빈 문자열인 경우, {@link Parameter#getName()}값을 제공합니다.
     * 
     * @param p
     *            메소드 파라미터
     * @return
     */
    private static final Function<Parameter, String> PARAMETER_COLUMN_NAME = p -> {
        JdbcVariableBinder b = PARAMETER_JDBC_VARIABLE_BINDER.apply(p);
        return SQLUtils.getColumnName(b.name(), b.columnNameType(), p.getName());
    };

    /**
     * 'SELECT' 구문에서 사용되는 DATA BINDING 구문을 생성합니다.
     * 
     * @param clmnName
     *            컬럼이름
     * @param clmnValue
     *            컬럼 정보
     * 
     * @param castByClmnRealType
     *            데이터를 컬럼 타입으로 CAST 여부
     * 
     * @since 2025. 4. 8.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    private static final TripleFunction<String, ColumnValue, Boolean, String> VARIABLE_BINDER_ON_SELECT = (clmnName, clmnValue, castByClmnRealType) -> {
        StringBuilder buf = new StringBuilder();

        String clmnRealType = clmnValue.columnType() != null ? clmnValue.columnType().trim() : null;
        castByClmnRealType = castByClmnRealType && clmnRealType != null;

        if (castByClmnRealType) {
            buf.append(" CAST (");
        }

        buf.append(clmnValue.variableBinding());

        if (castByClmnRealType) {
            buf.append(" AS ");
            buf.append(clmnRealType);
            buf.append(") ");
        }

        buf.append(" AS ");
        buf.append(clmnName);

        return buf.toString();
    };

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
     * 전체 데이터를 조회하는 쿼리.<br>
     * 패턴: <code>SEELCT * FROM {table-name}</code>
     * 
     * @see #queryForSelect()
     */
    protected final String QUERY_FOR_SELECT;
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 6.     박준홍         최초 작성
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
    public AbstractGenericView(@NotNull Class<T> entityType) {
        this(entityType, true, true);
    }

    /**
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
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
     * @see #AbstractGenericView(Class, boolean, boolean)
     */
    public AbstractGenericView(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        this(entityType, forceToPrimitive, true);
    }

    /**
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2023. 8. 24.     박준홍         최초 작성
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
    public AbstractGenericView(@NotNull Class<T> entityType, boolean forceToPrimitive, boolean ignoreNoDataMethod) {

        this.ignoreNoDataMethod = ignoreNoDataMethod;

        this.entityType = entityType;
        this.tableName = getTableName();

        this.QUERY_FOR_SELECT = queryForSelect();

        this.forceToPrimitive = forceToPrimitive;

        this.QUERY_FOR_COUNT = String.join(" ", "SELECT count(*) AS count FROM", getTableName());

    }

    /**
     * 주어진 조건에 맞는 데이터를 조회하는 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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
     * 주어진 파라미터를 이용하여 생성한 데이터 조회 쿼리를 제공합니다. <br>
     * 패턴: <code>{query} WHERE {column} = {variable-binding-query} (AND {column} = {variable-binding-query})*</code>
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

        List<Parameter> parameters = getVariableBindingParameters(method);

        queryBuf.append(" ");
        queryBuf.append(createWhereClause(parameters, "AND", whereArgs));
    }

    /**
     * 주어진 조건에 맞는 데이터를 조회하는 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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
     * 주어진 파라미터를 이용하여 생성한 데이터 조회 쿼리를 제공합니다. <br>
     * 패턴: <code>{query} WHERE {column} = {variable-binding-query} (AND {column} = {variable-binding-query})*</code>
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

        List<Parameter> parameters = getVariableBindingParameters(method);

        return String.join(" ", queryHeader, createWhereClause(parameters, "AND", whereArgs));
    }

    /**
     * 주어진 파라미터에 <code>null</code>이 포함되어 있는지 여부를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#countAll()
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#countBy(java.util.Map)
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 28.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 28.        박준홍         최초 작성
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
     * 정렬을 위한 Order By 구분을 생성하여 제공합니다. <br>
     * 패턴: <code>ORDER BY {column} {direction}(, {column} {direction})*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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

    protected Object[] createParametersForSelectBy(Map<String, Object> clmnParams, Object... added) {
        return clmnParams.size() > 0 //
                ? ArrayUtils.add(clmnParams.values().toArray(new Object[0]), added) //
                : added;
    }

    /**
     * 주어진 조건에 해당하는 데이터 개수를 제공합니다.
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 28.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 2. 10.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 2. 11.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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
     * 주어진 컬럼명으로 '{컬럼 접속자}'로 연결된 Where 구문을 제공합니다. <br>
     * 패턴:
     * <code>WHERE {column} {concatenator} {variable-binding-query} ( AND {column} {concatenator} {variable-binding-query} )*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * 2023. 10. 19.        박준홍     파라미터에서 
     *                                  - {@link JdbcVariableBinder} 제거.
     *                                  - {@link Parameter} 추가
     *                                 이에 따라 {@link Parameter}을 이용하여 내부 로직 변경.
     * </pre>
     * 
     * @param parameters
     *            {@link JdbcVariableBinder}가 설정된 {@link Parameter} 목록
     * @param concatenator
     *            Where 구문 컬럼 접속자
     * @param whereArgs
     *            파라미터 개수
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String createWhereClause(List<Parameter> parameters, String concatenator, Object... whereArgs) {

        if (parameters.size() < 1) {
            return "";
        }

        StringBuffer buf = new StringBuffer();

        buf.append("WHERE");
        buf.append(" ");

        Iterator<Parameter> itrParams = parameters.iterator();

        int paramCount = whereArgs.length;
        int posParam = 0;

        Parameter paramNow = itrParams.next();

        buf.append(validateColumnName(PARAMETER_COLUMN_NAME.apply(paramNow)));
        buf.append(" ");

        buf.append(getAssignQuery(PARAMETER_JDBC_VARIABLE_BINDER.apply(paramNow), posParam, whereArgs));
        paramCount--;

        boolean hasNext = true;
        while (hasNext) {
            posParam++;

            Parameter paramLatest = paramNow;
            switch (PARAMETER_JDBC_VARIABLE_BINDER.apply(paramLatest).operator()) {
                case IN:
                case NOT_IN:
                    if (itrParams.hasNext()) {
                        throw new UnsupportedVariableBindingException(String.format("'IN' 구문 이후에 다른 연산자가 오는 경우는 지원하지 않습니다. 연산자=%s",
                                parameters.stream().map(p -> p.getAnnotation(JdbcVariableBinder.class)).map(c -> c.operator().get()).collect(Collectors.toList())));
                    }
                    for (int i = 0; i < paramCount; i++) {
                        buf.append(", ");
                        buf.append(" ?");
                    }
                    buf.append(")");
                    hasNext = false;
                    break;
                default:
                    if (!itrParams.hasNext()) {
                        hasNext = false;
                        break;
                    }

                    paramNow = itrParams.next();

                    buf.append(" ");
                    buf.append(concatenator);
                    buf.append(" ");

                    buf.append(validateColumnName(PARAMETER_COLUMN_NAME.apply(paramNow)));
                    buf.append(" ");

                    buf.append(getAssignQuery(PARAMETER_JDBC_VARIABLE_BINDER.apply(paramNow), posParam, whereArgs));
                    paramCount--;
                    break;
            }
        }

        return buf.toString();
    }

    /**
     * 컬럼에 값을 설정하는 쿼리를 제공합니다. <br>
     * 패턴: <code>{column-name} = {variable-binding-query}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 1.     박준홍         최초 작성
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

        // 호출하는 함수로 이관
        // buf.append(vb.name());
        // buf.append(" ");

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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final List<Method> getColumnMethods() {

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
     * 컬럼명을 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * 2025. 4. 2           박준홍         DBMS Reserved Keyword 검증 적용
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 7. 14.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 7. 14.     박준홍         최초 작성
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
    protected Object[] getColumnValues(T data, String... clmns) {
        return getColumnValues(data, clmns != null ? Arrays.asList(clmns) : null);
    }

    /**
     * 이 메소드({@link #getCurrentMethod(Class...)})를 호출하는 메소드 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 1.     박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected List<ColumnValue> getEntityColumnValues() {
        return getColumnsAsStream() //
                .map(m -> m.getAnnotation(ColumnValue.class)) //
                .collect(Collectors.toList());
    }

    /**
     *
     * @since 2021. 12. 6.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#getEntityType()
     */
    @Override
    public Class<T> getEntityType() {
        return this.entityType;
    }

    /**
     * 테이블에서 Primary Key로 사용 중인 컬럼 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 1.      박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    protected final List<String> getPrimaryKeyColumns() {

        List<Method> columns = getColumnMethods();
        if (columns.size() < 1) {
            return null;
        }

        // Method: ColumnValue.primaryKey() 값이 true 경우
        return columns.stream().filter(m -> {
            ColumnValue annoCv = m.getAnnotation(ColumnValue.class);
            return annoCv != null && annoCv.primaryKey();
        }).map(m -> SQLUtils.getColumnName(m))//
                .collect(Collectors.toList()) //
        ;
    }

    /**
     * DBMS에서 예약어로 사용되는 단어 목록를 모두 대문자로 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 2.      박준홍         최초 작성
     * </pre>
     *
     * @return 예약어로 사용되는 단어 목록.
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract Set<String> getReservedKeywords();

    /**
     * 예약어로 사용되는 단어를 감싸는 문자를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 2.      박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract CharSequence getReservedKeywordWrappingCharacter();

    /**
     *
     * @since 2021. 12. 7.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#getTableName()
     */
    @Override
    public String getTableName() {
        TableDef tblAnno = this.entityType.getAnnotation(TableDef.class);
        AssertUtils.assertNull("DBMS Table에 연결된 Entity 정의가 존재하지 않습니다.", tblAnno, UnsupportedOperationException.class);
        return tblAnno.table();
    }

    /**
     * 메소드 파라미터에에서 'Where'절에 사용될 컬럼정보을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 1.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * 2023. 10. 19.        박준홍     {@link JdbcVariableBinder#name()} 값이 빈 문자열일 경우 {@link Parameter#getName()} 값을 제공.
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
                .map(PARAMETER_COLUMN_NAME::apply) //
                .collect(Collectors.toList());

        return columnNames;
    }

    /**
     * 주어진 메소드 파라미터 중에 {@link JdbcVariableBinder} 어노테이션이 설정되어 있는 파라미터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 14.        박준홍         최초 작성
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
     * 컬럼이름을 콤마(,)로 연결시킨 문자열을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
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
        return String.join(", ", validateColumnNames(getColumnNames()));
    }

    /**
     * 컬럼이름을 콤마(,)로 연결시킨 문자열을 제공합니다. <br>
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 2.      박준홍         최초 작성
     * </pre>
     *
     * @param tblAlias
     *            테이블명 alias
     * @return
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForColumnNames(String tblAlias) {
        if (tblAlias == null || tblAlias.trim().isEmpty()) {
            return queryForColumnNames();
        } else {
            final String trimTblAlias = tblAlias.trim();
            return validateColumnNames(getColumnNames()).stream() //
                    .map(clmn -> new StringBuilder(trimTblAlias).append(".").append(clmn))//
                    .collect(Collectors.joining(", "));
        }
    }

    /**
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
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
     * 전체 데이터를 조회하는 쿼리를 제공합니다. <br>
     * 패턴: <code>SEELCT * FROM {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     * 컬럼 개수에 맞는 '?'로만 구성된 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
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
     * 'Select' 구문에서 사용되는 데이터 바인딩 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 2.      박준홍         최초 작성
     * 2025. 4. 8.      박준홍         clmnUppserCase 항목 추가. H2 DB에서 "? AS {column}" 구문파싱시 {column}을 타입으로 처리하는 오류에 대응.
     * </pre>
     * 
     * @param castByClmnRealType
     *            컬럼타입으로 CAST 여부
     * 
     * @return
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForVariableBindingAliasingColumnName(boolean castByClmnRealType) {

        Iterator<String> clmnNames = getColumnNames().iterator();
        Iterator<ColumnValue> clmnValues = getEntityColumnValues().iterator();

        final StringBuffer queryBuf = new StringBuffer();

        if (clmnValues.hasNext()) {
            queryBuf.append(VARIABLE_BINDER_ON_SELECT.apply(clmnNames.next(), clmnValues.next(), castByClmnRealType));
            while (clmnValues.hasNext()) {
                queryBuf.append(", ");
                queryBuf.append(VARIABLE_BINDER_ON_SELECT.apply(clmnNames.next(), clmnValues.next(), castByClmnRealType));
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectAll()
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectAll(int, int)
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectAll(int, int, java.lang.String[])
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectAll(java.lang.String[])
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectAllByQuery(java.lang.String)
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectAllByQuery(java.lang.String, int, int)
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectAllByQuery(java.lang.String, int, int,
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectAllByQuery(java.lang.String, java.lang.String[])
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectBy(java.util.Map, int, int, java.lang.String[])
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectBy(java.util.Map, java.lang.String[])
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectByQuery(java.lang.String, java.util.Map, int,
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
     * @see open.commons.spring.jdbc.repository.IGenericRetrieve#selectByQuery(java.lang.String, java.util.Map,
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 30.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 9.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 2. 10.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 2. 10.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 30.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
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
     * 컬럼이름이 DBMS Reserved Keyword인 경우 DBMS에서 정한 문자로 감싼 문자열을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 2.      박준홍         최초 작성
     * </pre>
     *
     * @param clmnName
     *            컬럼이름.
     * @return
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    protected String validateColumnName(@NotEmpty String clmnName) {
        Set<String> rkw = getReservedKeywords();
        CharSequence kwrc = getReservedKeywordWrappingCharacter();
        if (rkw == null || rkw.isEmpty() || !rkw.contains(clmnName.trim().toUpperCase())) {
            return clmnName;
        } else if (kwrc == null) {
            throw ExceptionUtils.newException(IllegalStateException.class, "예약어가 설정되었으나, 예약어를 감싸는 문자가 설정되지 않았습니다. 컬럼명=%s, 예약어=%s", clmnName, CollectionUtils.toString(rkw));
        } else {
            return String.join("", kwrc, clmnName, kwrc);
        }
    }

    /**
     * 컬럼이름이 DBMS Reserved Keyword인 경우 DBMS에서 정한 문자로 감싼 문자열을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 2.      박준홍         최초 작성
     * </pre>
     *
     * @param clmns
     *            컬럼이름 목록
     * @return
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    protected List<String> validateColumnNames(@NotNull List<String> clmns) {
        return clmns.stream().map(clmn -> validateColumnName(clmn)).collect(Collectors.toList());
    }

    protected static Object[] array(Object... any) {
        return any != null ? any : new Object[0];
    }

    /**
     * 주어진 JDBC Variable Binding 정보에 확인하려는 {@link WhereCompare}이 없는지 여부를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 15.        박준홍         최초 작성
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
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 15.        박준홍         최초 작성
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

    /**
     * 콤마(,)로 구분된 예약어 문자열을 {@link Set} 객체로 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 2.      박준홍         최초 작성
     * </pre>
     *
     * @param reservedKeywordString
     *            콤마(,)로 구분된 예약어 문자열
     * @return 중복없는 예약어(대문자) 목록
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    protected static final Set<String> loadReservedKeywords(@NotNull String reservedKeywordString) {
        return Collections.unmodifiableSet(StringUtils.splitAsSet(reservedKeywordString, ",", kw -> kw != null ? kw.trim().toUpperCase() : null));
    }
}
