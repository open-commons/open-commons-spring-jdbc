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
import java.sql.PreparedStatement;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

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
     * <code>INSERT INTO {table-name} ( {comma-separated-column-names} ) VALUES ( {comma-separated-question-marks} )</code>
     * 
     * @see #queryForInsert()
     */
    protected final String QUERY_FOR_INSERT;

    /**
     * 전체 데이터를 조회하는 쿼리.<br>
     * <code>SEELCT * FROM {table-name}</code>
     * 
     * @see #queryForSelectAll()
     */
    protected final String QUERY_FOR_SELECT_ALL;

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
        this.QUERY_FOR_SELECT_ALL = queryForSelectAll();

        this.QUERY_FOR_PARTITION_HEADER = queryForPartitionHeader();
        this.QUERY_FOR_PARTITION_VALUE = queryForSelectAll();
        this.QUERY_FOR_PARTITION_CONCAT_VQ = queryForPartitionConcatValue();
        this.QUERY_FOR_PARTITION_TAIL = queryForPartitionTail();
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

        return methods;
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
        List<Method> methods = getColumnMethods();
        // #1. ColumnValue#order() 오름차순으로 정렬.
        methods.sort(Comparator.comparing(m -> m.getAnnotation(ColumnValue.class).order()));
        // #2. ColumnValue#name() 추출
        return methods.stream() //
                .map(m -> SQLUtils.getColumnName(m)) //
                .collect(Collectors.toList());
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
     * @see #getColumnNames();
     */
    protected String queryForColumnNames() {
        return String.join(", ", getColumnNames().toArray(new String[0]));
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
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final String queryForInsert() {
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
    protected String queryForSelectAll() {
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

        logger.debug("Query: {}", QUERY_FOR_SELECT_ALL);

        return getList(QUERY_FOR_SELECT_ALL, SQLConsumer.setParameters(), this.entityType);
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
        queryBuf.append(QUERY_FOR_SELECT_ALL);
        queryBuf.append(" ");
        queryBuf.append(queryForOffset(offset, limit));

        String query = queryBuf.toString();

        logger.debug("Query: {}", query);

        return getList(query, SQLConsumer.setParameters(offset, limit), this.entityType);
    }
}
