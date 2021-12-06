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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import open.commons.Result;
import open.commons.annotation.ColumnDef;
import open.commons.annotation.ColumnValue;
import open.commons.database.ConnectionCallbackBroker2;
import open.commons.database.annotation.TableDef;
import open.commons.function.SQLConsumer;
import open.commons.function.SQLTripleFunction;
import open.commons.spring.jdbc.dao.AbstractGenericDao;
import open.commons.utils.AnnotationUtils;
import open.commons.utils.ArrayUtils;
import open.commons.utils.AssertUtils;
import open.commons.utils.ObjectUtils;
import open.commons.utils.SQLUtils;
import open.commons.utils.ThreadUtils;

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
     * @see #AbstractGenericRepository(Class, boolean)
     */
    public AbstractGenericRepository(@NotNull Class<T> entityType) {
        this(entityType, true);
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
     */
    public AbstractGenericRepository(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        this.entityType = entityType;
        this.tableName = getTableName();

        this.QUERY_FOR_INSERT = queryForInsert();
        this.QUERY_FOR_SELECT = queryForSelect();

        this.QUERY_FOR_PARTITION_HEADER = queryForPartitionHeader();
        this.QUERY_FOR_PARTITION_VALUE = queryForSelect();
        this.QUERY_FOR_PARTITION_CONCAT_VQ = queryForPartitionConcatValue();
        this.QUERY_FOR_PARTITION_TAIL = queryForPartitionTail();

        this.QUERY_FOR_DELETE_HEADER = queryForDeleteHeader();
        this.QUERY_FOR_UPDATE_HEADER = queryForUpdateHeader();

        this.forceToPrimitive = forceToPrimitive;
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
    private String attachSetClause(String queryHeader) {

        List<ColumnValue> columns = getUpdatableColumnValues();

        if (columns.size() < 1) {
            throw new IllegalArgumentException(String.format("데이터를 변경할 컬럼 정보가 존재하지 않습니다. entity={}", this.entityType));
        }

        String query = String.join(" ", queryHeader, createSetClause(columns));
        logger.debug("Query: {}", query);

        return query;
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
    private String attachWhereClause(String queryHeader, @NotNull Method method, Object... whereArgs) {

        List<ColumnValue> columns = getColumnValuesOfParameters(method);

        if (columns.size() != whereArgs.length) {
            throw new IllegalArgumentException(String.format("쿼리에 사용될 컬럼 개수(%,d)와 파라미터 개수(%,d)가 일치하지 않습니다.", columns.size(), whereArgs.length));
        }

        String query = String.join(" ", queryHeader, createWhereClause(columns));
        logger.debug("Query: {}", query);

        for (int i = 0; i < whereArgs.length; i++) {
            logger.debug("[parameter] {}={}", columns.get(i).name(), whereArgs[i]);
        }

        return query;
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
     * <br>
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
    protected final ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] createInsertBrokers(List<T> data, int partitionSize) {

        logger.debug("query.header={}, query.value={}, data.size={}", QUERY_FOR_PARTITION_HEADER, QUERY_FOR_PARTITION_VALUE, data.size());

        return createConnectionCallbackBrokers(data, SQLTripleFunction.setParameters(), partitionSize //
                , QUERY_FOR_PARTITION_HEADER //
                , QUERY_FOR_PARTITION_VALUE //
                , QUERY_FOR_PARTITION_CONCAT_VQ //
                , QUERY_FOR_PARTITION_VALUE);
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
        return executeUpdate(attachWhereClause(QUERY_FOR_DELETE_HEADER, method, whereArgs), SQLConsumer.setParameters(whereArgs));
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

        List<Method> methods = AnnotationUtils.getAnnotatedMethodsAll(this.entityType, ColumnValue.class);

        AssertUtils.assertTrue("DBMS Table에 연결된 Entity 정의가 존재하지 않습니다.", methods.size() < 1, UnsupportedOperationException.class);

        methods.sort(Comparator.comparing(m -> m.getAnnotation(ColumnValue.class).order()));

        return methods;
    }

    /**
     * 실행 중인 메소드 파라미터에 설정된 컬럼이름을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param method
     *            실행 중인 메소드
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     * 
     * @see ColumnValue
     */
    protected final String getColumnNameOfParameter(@NotNull Method method) {
        Parameter param = getParameterHasColumnValue(method);
        return param.getAnnotation(ColumnValue.class).name();
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
        return getColumnMethods().stream() //
                .map(m -> SQLUtils.getColumnName(m)) //
                .collect(Collectors.toList());
    }

    /**
     * 메소드 파라미터에에서 'Where'절에 사용될 컬럼목록을 제공합니다. <br>
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
    protected List<String> getColumnNamesOfParameters(@NotNull Method method) {
        List<Parameter> params = getParametersHasColumnValue(method);

        List<String> whereColumns = params.stream() //
                .map(p -> p.getAnnotation(ColumnValue.class).name()) //
                .collect(Collectors.toList());

        return whereColumns;
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
    protected List<ColumnValue> getColumnValues() {
        return getColumnMethods().stream() //
                .map(m -> m.getAnnotation(ColumnValue.class)) //
                .collect(Collectors.toList());
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
    private List<ColumnValue> getColumnValuesOfParameters(@NotNull Method method) {
        List<Parameter> params = getParametersHasColumnValue(method);

        List<ColumnValue> whereColumns = params.stream() //
                .map(p -> p.getAnnotation(ColumnValue.class)) //
                .collect(Collectors.toList());

        return whereColumns;
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
     * <br>
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
     * 주어진 메소드에 {@link ColumnValue} 어노테이션이 설정되어 있는 파라미터를 제공합니다. <br>
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
    private Parameter getParameterHasColumnValue(@NotNull Method method) {
        for (Parameter param : method.getParameters()) {
            if (param.isAnnotationPresent(ColumnValue.class)) {
                return param;
            }
        }

        throw new UnsupportedOperationException(String.format("%s에 컬럼정보를 제공하는 %s가/이 설정되지 않았습니다.", method, ColumnValue.class));
    }

    /**
     * 주어진 메소드 파라미터 중에 {@link ColumnValue} 어노테이션이 설정되어 있는 파라미터를 제공합니다. <br>
     * 
     * <br>
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
    private List<Parameter> getParametersHasColumnValue(@NotNull Method method) {
        List<Parameter> params = new ArrayList<>();

        for (Parameter param : method.getParameters()) {
            if (param.isAnnotationPresent(ColumnValue.class)) {
                params.add(param);
            }
        }

        if (params.size() < 1) {
            throw new UnsupportedOperationException(String.format("%s에 컬럼정보를 제공하는 %s가/이 설정되지 않았습니다.", method, ColumnValue.class));
        }

        return params;
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
    protected abstract int getPartitionSize();

    /**
     * 테이블 이름을 제공합니다. <br>
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
    protected String getTableName() {
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
        return getUpdatableColumns().stream() //
                .map(m -> m.getAnnotation(ColumnValue.class).name()) //
                .collect(Collectors.toList())//
        ;
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
        return getColumnMethods().stream() //
                .filter(m -> m.getAnnotation(ColumnValue.class).updatable()) //
                .collect(Collectors.toList()) //
        ;
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
        return getUpdatableColumns().stream() //
                .map(m -> m.getAnnotation(ColumnValue.class)) //
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
    public Result<Integer> insert(List<T> data, int partitionSize) {
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
        return executeUpdate(QUERY_FOR_INSERT, SQLConsumer.setParameters(data));
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
     */
    protected String queryForDeleteHeader() {
        return new StringBuffer() //
                .append("DELETE FROM") //
                .append(" ") //
                .append(this.tableName) //
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
     */
    protected String queryForInsert() {
        return new StringBuffer() //
                .append("INSERT INTO") //
                .append(" ") //
                .append(this.tableName) //
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
     * <br>
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
    protected abstract String queryForOffset(int offset, int limit);

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
     */
    protected String queryForSelect() {
        return new StringBuffer() //
                .append("SELECT") //
                .append(" ") //
                .append("*") //
                .append(" ") //
                .append("FROM") //
                .append(" ") //
                .append(this.tableName) //
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
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForUpdateHeader() {
        return new StringBuffer() //
                .append("UPDATE") //
                .append(" ") //
                .append(this.tableName) //
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

        Iterator<ColumnValue> itr = getColumnValues().iterator();

        final StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(itr.next().variableBinding());

        while (itr.hasNext()) {
            queryBuf.append(", ");
            queryBuf.append(itr.next().variableBinding());
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
    public Result<List<T>> selectAll(int offset, int limit) {

        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(QUERY_FOR_SELECT);
        queryBuf.append(" ");
        queryBuf.append(queryForOffset(offset, limit));

        String query = queryBuf.toString();

        logger.debug("Query: {}", query);

        return getList(query, SQLConsumer.setParameters(offset, limit), this.entityType);
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
     */
    protected Result<List<T>> selectMultiBy(@NotNull Method method, Object... whereArgs) {
        String query = attachWhereClause(QUERY_FOR_SELECT, method, whereArgs);
        return getList(query, SQLConsumer.setParameters(whereArgs), this.entityType);
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
     *            'WHERE' 절에 사용될 파라미터.@param method
     * @param columnNames
     *            컬럼 목록
     * @return
     *
     * @since 2021. 11. 30.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiBy(@NotNull Method method, Object[] whereArgs, String... columnNames) {
        String query = attachWhereClause(QUERY_FOR_SELECT, method, whereArgs);
        return getList(query, SQLConsumer.setParameters(whereArgs), this.entityType, columnNames);
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
     *            'WHERE' 절에 사용될 파라미터.@param method
     * @param columnNames
     *            컬럼 목록
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
    protected Result<List<T>> selectMultiByForPagination(@NotNull Method method, int offset, int limit, Object... whereArgs) {

        StringBuffer queryBuf = new StringBuffer();

        queryBuf.append(attachWhereClause(QUERY_FOR_SELECT, method, whereArgs));
        queryBuf.append(" ");
        queryBuf.append(queryForOffset(offset, limit));

        String query = queryBuf.toString();

        logger.debug("Query: {}", query);

        return getList(query, SQLConsumer.setParameters(ArrayUtils.add(whereArgs, offset, limit)), this.entityType);
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
     *            컬럼 목록
     * @return
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see ColumnValue
     */
    protected Result<List<T>> selectMultiByForPagination(@NotNull Method method, int offset, int limit, Object[] whereArgs, String... columnNames) {

        StringBuffer queryBuf = new StringBuffer();

        queryBuf.append(attachWhereClause(QUERY_FOR_SELECT, method, whereArgs));
        queryBuf.append(" ");
        queryBuf.append(queryForOffset(offset, limit));

        String query = queryBuf.toString();

        logger.debug("Query: {}", query);

        return getList(query, SQLConsumer.setParameters(ArrayUtils.add(whereArgs, offset, limit)), this.entityType, columnNames);
    }

    /**
     * 주어진 조건에 맞는 1개의 데이터를 제공합니다. <br>
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
        return selectSingleBy(getCurrentMethod(1, whereArgs), required, whereArgs);
    }

    /**
     * 주어진 조건에 맞는 1개의 데이터를 제공합니다. <br>
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
        return selectSingleBy(getCurrentMethod(1, whereArgs), required, whereArgs, columnNames);
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
     * @param method
     *            사용자 정의 메소드
     * @param required
     *            필수 존재 여부.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
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
    protected Result<T> selectSingleBy(@NotNull Method method, boolean required, Object... whereArgs) {
        String query = attachWhereClause(QUERY_FOR_SELECT, method, whereArgs);
        return getObject(query, SQLConsumer.setParameters(whereArgs), this.entityType, required);
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
     * @param method
     *            사용자 정의 메소드
     * @param required
     *            필수 존재 여부.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @param columnNames
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
    protected Result<T> selectSingleBy(@NotNull Method method, boolean required, Object[] whereArgs, String... columnNames) {
        String query = attachWhereClause(QUERY_FOR_SELECT, method, whereArgs);
        return getObject(query, SQLConsumer.setParameters(whereArgs), this.entityType, required, columnNames);
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
        Object[] params = ArrayUtils.add(getUpdateParameters(data), whereArgs);
        return executeUpdate(query, SQLConsumer.setParameters(params));
    }

    /**
     * 주어진 조건에 맞는 데이터를 갱신합니다. <br>
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
        return updateBy(data, getCurrentMethod(1, whereArgs), whereArgs);
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
     * </pre>
     *
     * @param buf
     *            쿼리 버퍼
     * @param concat
     *            컬럼 설정쿼리 연결 문자열
     * @param columns
     *            컬럼 정보
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    private static void createColumnAssignQueries(StringBuffer buf, String concat, List<ColumnValue> columns) {

        // variable binding
        Iterator<ColumnValue> itr = columns.iterator();
        buf.append(getAssignQuery(itr.next()));

        while (itr.hasNext()) {
            buf.append(" ");
            buf.append(concat);
            buf.append(" ");
            buf.append(getAssignQuery(itr.next()));
        }
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
    private static String createSetClause(@NotNull List<ColumnValue> columns) {

        StringBuffer buf = new StringBuffer();

        if (columns.size() > 1) {
            buf.append("SET");
            buf.append(" ");

            createColumnAssignQueries(buf, ",", columns);
        }

        return buf.toString();
    }

    /**
     * 주어진 컬럼명으로 'AND'로 연결된 Where 구문을 제공합니다. <br>
     * 패턴: <code>WHERE {column} = {variable-binding-query} ( AND {column} = {variable-binding-query} )*</code>
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
    private static String createWhereClause(@NotNull List<ColumnValue> columns) {

        StringBuffer buf = new StringBuffer();

        if (columns.size() > 0) {
            buf.append("WHERE");
            buf.append(" ");

            createColumnAssignQueries(buf, "AND", columns);
        }

        return buf.toString();
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
    private static final String getAssignQuery(ColumnValue cv) {
        return String.join(" = ", cv.name(), cv.variableBinding());
    }
}
