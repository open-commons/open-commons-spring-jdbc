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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.PreparedStatement;
import java.util.ArrayList;
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
import open.commons.spring.jdbc.dao.AbstractSingleDataSourceDao;
import open.commons.utils.AnnotationUtils;
import open.commons.utils.AssertUtils;
import open.commons.utils.SQLUtils;

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
public abstract class AbstractSingleDataSourceRepository<T> extends AbstractSingleDataSourceDao implements IGenericRepository<T> {

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

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     * 
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractSingleDataSourceRepository(@NotNull Class<T> entityType) {
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
    }

    /**
     * 주어진 파라미터를 이용하여 생성한 데이터 변경 쿼리를 제공합니다. <br>
     * 패턴: <code>{query} SET {column} = ? (, {column} = ?)*</code>
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
     * @param args
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    private String attachSetClause(String queryHeader) {

        List<String> columns = getUpdatableColumnNames();

        if (columns.size() < 1) {
            throw new IllegalArgumentException(String.format("데이터를 변경할 컬럼 정보가 존재하지 않습니다. entity={}", this.entityType));
        }

        String query = String.join(" ", queryHeader, createSetClause(columns));
        logger.debug("Query: {}", query);

        return query;
    }

    /**
     * 주어진 파라미터를 이용하여 생성한 데이터 조회 쿼리를 제공합니다. <br>
     * 패턴: <code>{query} WHERE {column} = ? (AND {column} = ?)*</code>
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
     * @param args
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    private String attachWhereClause(String queryHeader, Method method, Object... args) {

        List<String> columns = getColumnNamesOfParameters(method);

        if (columns.size() != args.length) {
            throw new IllegalArgumentException(String.format("쿼리에 사용될 컬럼 개수(%,d)와 파라미터 개수(%,d)가 일치하지 않습니다.", columns.size(), args.length));
        }

        String query = String.join(" ", queryHeader, createWhereClause(columns));
        logger.debug("Query: {}", query);

        for (int i = 0; i < args.length; i++) {
            logger.debug("param >> {}={}", columns.get(i), args[i]);
        }

        return query;
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
     * @param pk
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> deleteBy(Method method, Object... args) {
        return executeUpdate(attachWhereClause(QUERY_FOR_DELETE_HEADER, method, args), SQLConsumer.setParameters(args));
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
    protected final String getColumnNameOfParameter(Method method) {
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
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    private List<String> getColumnNamesOfParameters(Method method) {
        List<Parameter> params = getParametersHasColumnValue(method);
        return params.stream() //
                .map(p -> p.getAnnotation(ColumnValue.class).name()) //
                .collect(Collectors.toList());
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
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    private Parameter getParameterHasColumnValue(Method method) {
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
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    private List<Parameter> getParametersHasColumnValue(Method method) {
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
    protected final List<Method> getUpdatableColumn() {
        return getColumnMethods().stream() //
                .filter(m -> m.getAnnotation(ColumnValue.class).updatable()) //
                .collect(Collectors.toList()) //
        ;
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
        return getUpdatableColumn().stream() //
                .map(m -> m.getAnnotation(ColumnValue.class).name()) //
                .collect(Collectors.toList())//
        ;
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
        int columnCount = getColumnMethods().size();

        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append("?");
        for (int i = 1; i < columnCount; i++) {
            queryBuf.append(", ?");
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
     *            조회 메소드
     * @param whereArgs
     *            파라미터.
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<List<T>> selectMultiBy(Method method, Object... whereArgs) {
        return getList(attachWhereClause(QUERY_FOR_SELECT, method, whereArgs), SQLConsumer.setParameters(whereArgs), this.entityType);
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
     *            조회 메소드
     * @param required
     *            필수 존재 여부.
     * @param whereArgs
     *            파라미터
     * @return
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<T> selectSingleBy(Method method, boolean required, Object... whereArgs) {
        return getObject(attachWhereClause(QUERY_FOR_SELECT, method, whereArgs), SQLConsumer.setParameters(whereArgs), this.entityType, required);
    }

    /**
     * 주어진 조건에 맞는 데이터를 갱신합니다. <br>
     * 패턴: <code>UPDATE {table-name} SET {column} = ? (, {column} = ?)*  WHERE {column} = ? ( AND {column} = ? )*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 29.		박준홍			최초 작성
     * </pre>
     * 
     * @param data
     *            TODO
     * @param whereArgs
     * @param m
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> updateBy(T data, Method method, Object... whereArgs) {

        String querySet = attachSetClause(QUERY_FOR_UPDATE_HEADER);

        return executeUpdate(attachWhereClause(querySet, method, whereArgs), SQLConsumer.setParameters(whereArgs));
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
    private static String createSetClause(@NotEmpty List<String> columns) {

        StringBuffer buf = new StringBuffer();

        buf.append("SET");
        buf.append(" ");

        // variable binding
        Iterator<String> itr = columns.iterator();
        buf.append(itr.next());
        buf.append(" = ?");

        while (itr.hasNext()) {
            buf.append(", ");
            buf.append(itr.next());
            buf.append(" = ?");
        }

        return buf.toString();
    }

    /**
     * 주어진 컬럼명으로 'AND'로 연결된 Where 구문을 제공합니다. <br>
     * 패턴: <code>WHERE {column} = ? ( AND {column} = ? )*</code>
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
    private static String createWhereClause(@NotEmpty List<String> columns) {

        StringBuffer buf = new StringBuffer();

        buf.append("WHERE");
        buf.append(" ");

        // variable binding
        Iterator<String> itr = columns.iterator();
        buf.append(itr.next());
        buf.append(" = ?");

        while (itr.hasNext()) {
            buf.append(" AND ");
            buf.append(itr.next());
            buf.append(" = ?");
        }

        return buf.toString();
    }
}
