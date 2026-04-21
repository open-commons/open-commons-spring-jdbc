/*
 * Copyright 2019 Park Jun-Hong (parkjunhong77@gmail.com)
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
 * This file is generated under this project, "open-commons-springframework4".
 *
 * Date  : 2019. 3. 28. 오후 3:42:37
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.spring.jdbc.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import open.commons.core.Result;
import open.commons.core.annotation.ColumnDef;
import open.commons.core.collection.concurrent.ConcurrentLinkedHashMap;
import open.commons.core.database.ConnectionCallbackBroker2;
import open.commons.core.database.DefaultConCallbackBroker2;
import open.commons.core.function.SQLBiFunction;
import open.commons.core.function.SQLConsumer;
import open.commons.core.function.SQLFunction;
import open.commons.core.function.SQLTripleFunction;
import open.commons.core.test.StopWatch;
import open.commons.core.text.NamedTemplate;
import open.commons.core.utils.AssertUtils2;
import open.commons.core.utils.NumberUtils;
import open.commons.core.utils.SQLUtils;
import open.commons.spring.jdbc.dao.dto.CountDTO;

/**
 * DAO 공통 기능 제공 클래스.<br>
 * 
 * Springframework {@link JdbcTemplate}에서 사용하는 저레벨 수준의 코드를 이용하여 customize 하였음.
 * 
 * <hr>
 * {@link SQLConsumer}를 이용한 파라미터 설정 객체 생성 예제 <br>
 * 
 * <pre>
 * static final Function&lt;QueryParamObj, SQLConsumer&lt;PreparedStatement>> PROVIDER = param -> pstmt -> {
 *     pstmt.setString(1, param.getName());
 *     pstmt.setString(1, param.getCost());
 *     pstmt.setString(1, param.getDate());
 * };
 * 
 * static SQLConsumer&lt;PreparedStatement> create(QueryParamObj param) {
 *     return pstmt -> {
 *         pstmt.setString(1, param.getName());
 *         pstmt.setString(1, param.getCost());
 *         pstmt.setString(1, param.getDate());
 *     };
 * }
 * 
 * public static void main(String[] args) {
 *     // 쿼리 파라미터.
 *     QueryParamObj param = new QueryObj();
 * 
 *     // #1. java.util.function.Function 을 이용하는 법
 *     SQLConsumer<PreparedStatement> setter = PROVIDER.apply(param);
 *     // #2. 메소드를 이용하는 법
 *     setter = create(param);
 * 
 *     AbstractGenericDao dao = null; // ...
 * 
 *     // insert/update/delete
 *     String query = "INSERT ...";
 *     Result&lt;Integer> executeUpdate = dao.executeUpdate(query, setter);
 * 
 *     // select
 *     query = "SELECT ...";
 *     Result&lt;List&lt;EntityType>> getList = dao.getList(query, setter, EntityType.class);
 * 
 *     // select
 *     query = "SELECT ...";
 *     Result&lt;EntityType> getObject = dao.getObject(query, setter, EntityType.class);
 * }
 * </pre>
 * 
 * <br>
 * 
 * <pre>
 * [개정이력]
 *      날짜      | 작성자   |	내용
 * ------------------------------------------
 * 2019. 3. 28.     parkjunhong77@gmail.com     최초 작성
 * 2020. 4. 15.     parkjunhong77@gmail.com     여러 개의 DataSource 지원.
 * </pre>
 * 
 * @since 2019. 3. 28.
 * @version 0.1.0
 * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
 */
public abstract class AbstractGenericRetrieve implements IGenericDao {

    /** {@link Map} 형태로 DB 조회결과를 제공하는 DTO 타입 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static final Class<Map<String, Object>> ENTITY_DTO_MAP = (Class<Map<String, Object>>) (Class) ConcurrentLinkedHashMap.class;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected ReloadableResourceBundleMessageSource querySource;

    // protected DataSource dataSource;
    // protected JdbcTemplate jdbcTemplate;

    /**
     * 타입별 객체 생성기<br>
     * <ul>
     * <li>key: 클래스 이름 {@link Class#getName()}
     * <li>value: 객체 생성 함수
     * </ul>
     */
    private final ConcurrentSkipListMap<String, SQLBiFunction<ResultSet, Integer, ?>> CREATORS = new ConcurrentSkipListMap<>();

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    public AbstractGenericRetrieve() {
    }

    /**
     * 쿼리 템플릿에 에 IN Clause(<code>"IN ( ?, ?, ...)")를 추가합니다. <br>
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param queryTpl
     *            쿼리 템플릿
     * 
     * @param inClauseName
     *            'IN' 항목 이름
     * @param inParamCount
     *            'IN' 파라미터 개수
     * @return
     *
     * @since 2022. 3. 28.
     * @version 0.3.0
     */
    protected void addQueryForInClause(NamedTemplate queryTpl, String inClauseName, int inParamCount) {

        // #1. 파라미터 개수만큼 (?, ?, ...) 생성
        StringBuffer tapIdIn = new StringBuffer();
        addQueryForInClause(tapIdIn, inParamCount);

        // #2. 쿼리 템플릿에 반영
        queryTpl.addValue(inClauseName, tapIdIn.toString());
    }

    /**
     * 기존 쿼리에 IN Clause(<code>"IN ( ?, ?, ...)")를 추가합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 6. 12.     parkjunhong77@gmail.com         최초 작성
     * </pre>
     *
     * @param sqlBuffer
     *            쿼리 버퍼
     * @param inParamCount
     *            IN ( ... )에 사용될 파라미터 개수
     *
     * @since 2019. 6. 12.
     * @version
     */
    protected void addQueryForInClause(StringBuffer sqlBuffer, int inParamCount) {

        if (inParamCount < 1) {
            throw new IllegalArgumentException("Parameter count MUST BE LARGER than 0.");
        }

        sqlBuffer.append(" ( ?");

        for (int i = 1; i < inParamCount; i++) {
            sqlBuffer.append(", ?");
        }

        sqlBuffer.append(" )");
    }

    /**
     * 기존 쿼리 WHERE Clause에 IN Clause (<code>"IN ( ?, ?, ...)")를 추가합니다.. <br>
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 6. 12.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param sqlBuffer
     *            쿼리 버퍼
     * @param concatenator
     *            WHERE 조건 병합 연산자 [ AND | OR ]
     * @param columnName
     *            비교 컬럼명
     * @param inParamCount
     *            IN ( ... )에 사용될 파라미터 개수
     *
     * @since 2019. 6. 12.
     * @version 0.0.6
     */
    protected void addQueryForInClause(StringBuffer sqlBuffer, String concatenator, String columnName,
            int inParamCount) {
        AssertUtils2.notNulls(sqlBuffer, concatenator, columnName);

        if (inParamCount < 1) {
            throw new IllegalArgumentException("Parameter count MUST BE LARGER than 0.");
        }

        sqlBuffer.append(" ");
        sqlBuffer.append(concatenator);
        sqlBuffer.append(" ");
        sqlBuffer.append(columnName);
        sqlBuffer.append(" IN");

        addQueryForInClause(sqlBuffer, inParamCount);
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
    }

    private <E> ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>> createBroker(@NotEmpty List<E> data,
            Function<List<E>, SQLConsumer<PreparedStatement>> psSetterProvider, String headerQuery, String valueQuery,
            String concatForVQ, String tailQuery) {

        StringBuffer query = new StringBuffer();

        query.append(headerQuery);
        query.append(" ");
        query.append(valueQuery);
        query.append(" ");
        for (int i = 1; i < data.size(); i++) {
            query.append(concatForVQ);
            query.append(" ");
            query.append(valueQuery);
        }

        if (tailQuery != null) {
            query.append(tailQuery);
        }

        // #2 데이터 Setter 생성 및 PreparedStatement 브로커 생성.
        return new DefaultConCallbackBroker2(query.toString(), psSetterProvider.apply(data));
    }

    /**
     * 하나의 {@link Connection}에서 실행되는 다중 실행 정보를 생성합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 21.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            저장할 데이터
     * @param psSetterProvider
     *            PreparedStatement 데이터 설정
     * @param partitionSize
     *            분할 크기
     * @param headerQuery
     *            다중 데이터 추가를 위한 쿼리 헤더
     * @param valueQuery
     *            데이터 바인딩 쿼리
     * @param concatForVQ
     *            데이터 바인딩 쿼리 연결자
     * @param tailQuery
     *            추가 쿼리
     * @return
     *
     * @since 2020. 7. 21.
     */
    protected final <E> ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] createConnectionCallbackBrokers(
            List<E> data, Function<List<E>, SQLConsumer<PreparedStatement>> psSetterProvider, @Min(1) int partitionSize,
            String headerQuery, String valueQuery, String concatForVQ, String tailQuery) {
        AssertUtils2.notNulls(data, psSetterProvider, headerQuery, valueQuery, concatForVQ, tailQuery);

        if (data.size() < 1) {
            return new DefaultConCallbackBroker2[0];
        }

        // #1. 분할된 데이터를 추가하기 위한 쿼리와 데이터 Setter 생성
        List<ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>> brokers = new ArrayList<>();

        List<E> part = new ArrayList<>();
        for (E datum : data) {
            part.add(datum);

            // #1-1. 나누어진 데이터의 크기가 설정된 크기인지 확인
            if (part.size() % partitionSize == 0) {
                brokers.add(createBroker(part, psSetterProvider, headerQuery, valueQuery, concatForVQ, tailQuery));
                part = new ArrayList<>();
            }
        }

        // #2. 남은 데이터 추가
        if (part.size() > 0) {
            brokers.add(createBroker(part, psSetterProvider, headerQuery, valueQuery, concatForVQ, tailQuery));
        }

        return brokers.toArray(new DefaultConCallbackBroker2[0]);
    }

    /**
     * 다중 데이터를 추가하는 실행정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 21.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <E>
     *            데이터 타입.
     * @param data
     *            추가하려는 데이타.
     * @param dataSetter
     *            객체 데이터를 {@link PreparedStatement}에 추가하는 주체.<br>
     *            참조: {@link SQLTripleFunction#setParameters(String...)}
     * @param partitionSize
     *            데이터 분할 크기
     * @param headerQuery
     *            여러개 데이터 추가용 쿼리 헤더, INSERT ... 이 포함된 구문이 설정됨.
     * @param valueQuery
     *            여러개 데이터 바인딩용 쿼리. (?, ?, ...) 이 포함된 구문이 설정됨.
     * @param concatForVQ
     *            바인딩용 쿼리 연결자
     * @param tailQuery
     *            쿼리 마지막
     * @return
     *
     * @since 2020. 7. 21.
     */
    protected final <E> ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] createConnectionCallbackBrokers( //
            List<E> data, SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter //
            , @Min(1) int partitionSize, String headerQuery, String valueQuery, String concatForVQ, String tailQuery) {
        AssertUtils2.notNulls(data);
        AssertUtils2.notNulls(headerQuery, valueQuery, concatForVQ, tailQuery);

        Function<List<E>, SQLConsumer<PreparedStatement>> psSetterProvider = params -> {
            SQLConsumer<PreparedStatement> con = stmt -> {
                int i = 0;
                for (E param : params) {
                    i = dataSetter.apply(stmt, i, param);
                }

                // begin - PATCH [2020. 8. 12.]: 데이터 바인딩 직후 Collection 해제 |
                // Park_Jun_Hong_(parkjunhong77@gmail.com)
                params.clear();
                // end - Park_Jun_Hong_(parkjunhong77@gmail.com), 2020. 8. 12.
            };

            return con;
        };

        return createConnectionCallbackBrokers(data, psSetterProvider, partitionSize, headerQuery, valueQuery,
                concatForVQ, tailQuery);
    }

    private <E> List<E> createObject(ResultSet rs, Class<E> entity, String... columns) throws SQLException {

        SQLBiFunction<ResultSet, Integer, E> creator = findCreator(entity, columns);

        List<E> l = new ArrayList<>();
        int i = 1;
        while (rs.next()) {
            l.add(creator.apply(rs, i++));
        }
        return l;
    }

    /**
     * 주어진 이름에 해당하는 쿼리에 'IN' 구문을 추가하여 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 3. 28.     parkjunhong77@gmail.com         최초 작성
     * </pre>
     *
     * @param queryName
     *            쿼리 이름
     * @param inParamCount
     *            'IN' 파라미터 개수
     * @return
     *
     * @since 2022. 3. 28.
     * @version 0.3.0
     */
    protected String createQueryForInClause(String queryName, int inParamCount) {
        AssertUtils2.notNull(queryName);

        String query = Objects.requireNonNull(getQuery(queryName));

        // #1. 쿼리 버퍼
        StringBuffer queryBuffer = new StringBuffer(query);

        // #2. 파라미터 개수만큼 (?, ?, ...) 생성
        addQueryForInClause(queryBuffer, inParamCount);

        // #3. 최종 쿼리 생성.
        return queryBuffer.toString();
    }

    /**
     * 주어진 이름에 해당하는 쿼리에 'IN' 구문을 추가하여 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param queryName
     *            쿼리 이름
     * @param inClauseName
     *            'IN' 항목 이름
     * @param inParamCount
     *            'IN' 파라미터 개수
     * @return
     *
     * @since 2022. 3. 28.
     * @version 0.3.0
     */
    protected String createQueryForInClause(String queryName, String inClauseName, int inParamCount) {
        AssertUtils2.notNulls(queryName, inClauseName);

        String query = Objects.requireNonNull(getQuery(queryName));

        // #1. 쿼리 템플릿
        NamedTemplate queryTpl = new NamedTemplate(query);

        // #2. 파라미터 개수만큼 (?, ?, ...) 생성
        StringBuffer tapIdIn = new StringBuffer();
        addQueryForInClause(tapIdIn, inParamCount);

        // #3. 쿼리 템플릿에 반영
        queryTpl.addValue(inClauseName, tapIdIn.toString());

        // #4. 최종 쿼리 생성.
        return queryTpl.format();
    }

    /**
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
    }

    /**
     * 쿼리 요청을 처리하고 결과를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * 2019. 6. 5.		parkjunhong77@gmail.com			작업용 Connection 객체 생성 로직 수직
     * 2025. 6. 11.     parkjunhong77@gmail.com         {@link Transactional}을 이용하여 {@link Repository} 메소드를 관리하기 위해서 통합.
     * 
     * </pre>
     *
     * @param act
     *            {@link Connection}을 전달받아 요청쿼리를 처리하는 객체
     * @return 쿼리처리 결과.
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    protected <R> R execute(SQLFunction<Connection, R> act) throws SQLException {
        AssertUtils2.notNull(act);

        Connection con = null;
        DataSource dataSource = null;
        try {
            dataSource = getDataSource();
            con = DataSourceUtils.getConnection(dataSource);
            return act.apply(con);
        } catch (SQLException e) {
            logger.warn("Fail to execute query. con={}", con.toString(), e);
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    /**
     * 조회된 데이터 개수를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param countQuery
     *            데이터 개수 제공 쿼리.
     * @param params
     *            조회 파라미터.
     * @return
     *
     * @since 2021. 12. 28.
     * @version 0.3.0
     * 
     * @see CountDTO
     */
    protected Result<Integer> executeCountOf(String countQuery, Object... params) {
        AssertUtils2.notNulls(countQuery, params);

        Result<CountDTO> result = getObject(countQuery, SQLConsumer.setParameters(params), CountDTO.class);

        if (!result.getResult()) {
            return new Result<Integer>().setMessage(result.getMessage());
        } else if (result.getData() == null) {
            return new Result<Integer>().setMessage("count is null !!!");
        } else {
            return new Result<Integer>(result.getData().getCount(), true);
        }
    }

    /**
     * 요청쿼리를 실행하고 결과를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 28.     parkjunhong77@gmail.com         최초 작성
     * </pre>
     *
     * @param <S>
     * @param <E>
     *            요청받을 데이터
     * @param broker
     *            요청쿼리와 쿼리 파라미터를 처리하는 객체
     * @param entity
     *            요청쿼리 처리 결과 데이타 모델
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *            {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과.
     * @throws SQLException
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    private <S, E> List<E> executeQuery(ConnectionCallbackBroker2<S> broker, Class<E> entity, String... columns)
            throws SQLException {
        return executeQuery(broker, rs -> createObject(rs, entity, columns));
    }

    /**
     * 요청쿼리를 실행하고 결과를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 4. 23.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <S>
     * @param <E>
     *            요청받을 데이터 타입
     * @param broker
     *            쿼리와 쿼리 파라미터를 처리하는 객체
     * @param creator
     *            데이터를 생성하는 객체
     * @return
     * @throws SQLException
     *
     * @since 2021. 4. 23.
     * @version 0.3.0
     */
    private <S, E> List<E> executeQuery(ConnectionCallbackBroker2<S> broker, SQLFunction<ResultSet, List<E>> creator)
            throws SQLException {
        List<E> data = null;
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            data = execute(con -> {
                PreparedStatement pstmt = con.prepareStatement(broker.getQuery());
                broker.set(pstmt);

                ResultSet rs = pstmt.executeQuery();

                final String label = "execute-query";
                final String nextlabel = "create-objects";
                watch.record(label);
                logger.trace("Elapsed.{}={}, next='{}'", label, watch.getAsPretty(label), nextlabel);

                try {
                    return creator.apply(rs);
                } finally {
                    watch.record(nextlabel);
                    logger.trace("Elapsed.{}={}", nextlabel, watch.getAsPretty(nextlabel));
                }
            });
            return data;
        } finally {
            watch.stop();
            logger.trace("Data.count: {}, Elapsed.total: {}",
                    data != null ? NumberUtils.INT_TO_STR.apply(data.size()) : 0, watch.getAsPretty());
        }
    }

    /**
     * 데이타 타입에 맞는 객체 생성자를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * 2020. 6. 12.     parkjunhong77@gmail.com         조회 결과를 java.util.Map 형태로 받는 경우 지원
     * </pre>
     *
     * @param entity
     *            쿼리처리 결과 데이타 타입
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *            {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.0.6
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <R> SQLBiFunction<ResultSet, Integer, R> findCreator(Class<R> entity, String... columns) {
        Arrays.sort(columns);
        String key = String.join("-", entity.getName(), String.valueOf(Arrays.toString(columns).hashCode()));
        SQLBiFunction<ResultSet, Integer, R> creator = (SQLBiFunction<ResultSet, Integer, R>) CREATORS.get(key);

        if (creator == null) {
            // begin - PATCH [2020. 6. 12.]: 조회 결과를 java.util.Map 형태로 받는 경우 지원.|
            // Park_Jun_Hong_(parkjunhong77@gmail.com)
            if (Map.class.isAssignableFrom(entity)) {
                // DAO Entity가 Map.class 인 경우는 Map.class 가 여러 가지의 데이터 타입을 대신하는
                // 것이기 때문에,
                // Entity 생성 함수를 별도로 저장하지 않는다.
                creator = (rs, _) -> {
                    try {
                        Map data = (Map) entity.getDeclaredConstructor().newInstance();
                        for (String clmn : columns) {
                            data.put(clmn, rs.getObject(clmn));
                        }

                        return (R) data;
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException | NoSuchMethodException e) {
                        throw new SQLException(String.format("%s 객체 생성시 에러가 발생하였습니다. 원인=%s", entity, e.getMessage()),
                                e);
                    }
                };
                // end - Park_Jun_Hong_(parkjunhong77@gmail.com), 2020. 6. 12.
            } else {
                creator = (rs, _) -> SQLUtils.newInstance(entity, rs, columns);
                CREATORS.put(entity.getName(), creator);
            }
        }

        return creator;
    }

    /**
     * 전달된 쿼리에 대한 조회 결과 데이터 개수를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 22.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param selectQuery
     *            데이터 조회 쿼리.
     * @param params
     *            조회 파라미터.
     * @return
     *
     * @since 2020. 1. 22.
     * @version 0.0.6
     */
    public Result<Integer> getCount(String selectQuery, Object... params) {
        AssertUtils2.notNulls(selectQuery, params);

        String query = wrapQueryForCount(selectQuery);
        return executeCountOf(query, params);
    }

    /**
     * {@link DataSource}를 {@link TransactionAwareDataSourceProxy}로 감싸서 제공합니다.
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 6. 11.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param dataSource
     * @return
     *
     * @since 2025. 6. 11.
     * @version 0.5.0
     */
    protected final DataSource getDataSource0(DataSource dataSource) {
        Assert.notNull(dataSource, "datasource는 절대 null 일 수 없습니다.");

        if (dataSource instanceof TransactionAwareDataSourceProxy) {
            return dataSource;
        } else {
            return new TransactionAwareDataSourceProxy(dataSource);
        }
    }

    /**
     * 데이터 조회 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param entity
     *            결과 데이타 타입
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *          {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    public <E> Result<List<E>> getList(String query, Class<E> entity, String... columns) {
        AssertUtils2.notNulls(query, entity, columns);

        return getList(query, SQLConsumer.DO_NOTHING, entity, columns);
    }

    /**
     * 데이터 조회 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 22.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <E>
     *            요청받을 데이타 타입
     * @param query
     *            데이터 조회 요청쿼리
     * @param size
     *            한번에 불러올 데이터 개수
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @param entity
     *            결과 데이타 타입
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *          {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2020. 7. 22.
     */
    public <E> Result<List<E>> getList(String query, int size, SQLConsumer<PreparedStatement> setter, Class<E> entity,
            String... columns) {

        Result<List<E>> result = new Result<>();

        try {
            List<E> list = executeQuery(new DefaultConCallbackBroker2(query, setter), entity, columns);
            result.andTrue().setData(list);
        } catch (SQLException e) {
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 데이터 조회 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체 <br>
     * @param entity
     *            결과 데이타 타입
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *          {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    public <E> Result<List<E>> getList(String query, SQLConsumer<PreparedStatement> setter, Class<E> entity,
            String... columns) {

        Result<List<E>> result = new Result<>();

        try {
            List<E> list = executeQuery(new DefaultConCallbackBroker2(query, setter), entity, columns);
            result.andTrue().setData(list);
        } catch (SQLException e) {
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 데이터 조회 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 4. 23.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체 <br>
     * @param creator
     *            데이터 생성 함수
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2032. 4. 23.
     * @version 0.3.0
     */
    public <E> Result<List<E>> getList(String query, SQLConsumer<PreparedStatement> setter,
            SQLFunction<ResultSet, List<E>> creator) {

        Result<List<E>> result = new Result<>();

        try {
            List<E> list = executeQuery(new DefaultConCallbackBroker2(query, setter), creator);
            result.andTrue().setData(list);
        } catch (SQLException e) {
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 데이터 조회 요청쿼리를 처리합니다. <br>
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 4. 23.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <E>
     * @param query
     *            데이터 조회 요청쿼리
     * @param creator
     *            데이터 생성 함수
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2021. 4. 23.
     * @version 0.3.0
     */
    public <E> Result<List<E>> getList(String query, SQLFunction<ResultSet, List<E>> creator) {

        Result<List<E>> result = new Result<>();

        try {
            List<E> list = executeQuery(new DefaultConCallbackBroker2(query, null), creator);
            result.andTrue().setData(list);
        } catch (SQLException e) {
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 데이터 조회 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 22.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param setter
     *            데이터 바인더
     * @param entity
     *            결과 데이터 타입.
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *          {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2020. 7. 22.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Result<List<Map<String, Object>>> getListAsMap(String query, SQLConsumer<PreparedStatement> setter,
            String... columns) {
        Class<Map<String, Object>> entity = (Class<Map<String, Object>>) (Class) ConcurrentLinkedHashMap.class;
        return getList(query, setter, entity, columns);
    }

    /**
     * 데이터 조회 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 6. 12.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param entity
     *            결과 데이터 타입.
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *          {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2020. 6. 12.
     */
    public Result<List<Map<String, Object>>> getListAsMap(String query, String... columns) {
        return getListAsMap(query, SQLConsumer.DO_NOTHING, columns);
    }

    /**
     * 데이터 1개 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            조회 요청쿼리
     * @param entity
     *            결과 데이타 타입
     * @param required
     *            필수여부
     * @param columns
     * 
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *            {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    public <T> Result<T> getObject(String query, Class<T> entity, boolean required, String... columns)
            throws EmptyResultDataAccessException, IncorrectResultSizeDataAccessException {
        return getObject(query, SQLConsumer.DO_NOTHING, entity, required, columns);
    }

    /**
     * 데이터 1개 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            조회 요청쿼리
     * @param entity
     *            결과 데이타 타입
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *            {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    public <T> Result<T> getObject(String query, Class<T> entity, String... columns)
            throws EmptyResultDataAccessException, IncorrectResultSizeDataAccessException {
        return getObject(query, SQLConsumer.DO_NOTHING, entity, false, columns);
    }

    /**
     * 데이터 1개 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            조회 요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @param entity
     *            결과 데이타 타입
     * @param required
     *            필수 여부
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *            {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    public <T> Result<T> getObject(String query, SQLConsumer<PreparedStatement> setter, Class<T> entity,
            boolean required, String... columns)
            throws EmptyResultDataAccessException, IncorrectResultSizeDataAccessException {
        AssertUtils2.notNulls(query, setter, entity, columns);

        Result<T> result = new Result<>();

        try {
            List<T> list = executeQuery(new DefaultConCallbackBroker2(query, setter), entity, columns);

            switch (list.size()) {
                case 0:
                    if (required) {
                        throw new EmptyResultDataAccessException(1);
                    }
                    result.andTrue();
                    break;
                case 1:
                    // (start) [BUG-FIX]: Result#result 설정 누락 수정 /
                    // Park_Jun_Hong_(parkjunhong77@gmail.com): 2019. 6. 12.
                    // 오후 4:15:28
                    result.andTrue().setData(list.get(0));
                    // (end): 2019. 6. 12. 오후 4:15:28
                    break;
                default:
                    throw new IncorrectResultSizeDataAccessException(1, list.size());
            }
        } catch (SQLException e) {
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 데이터 1개 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            조회 요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @param entity
     *            결과 데이타 타입.
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된
     *            {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    public <T> Result<T> getObject(String query, SQLConsumer<PreparedStatement> setter, Class<T> entity,
            String... columns) throws EmptyResultDataAccessException, IncorrectResultSizeDataAccessException {
        return getObject(query, setter, entity, false, columns);
    }

    /**
     * 데이터 1개를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            조회 쿼리
     * @param required
     *            데이터 존재 필수 여부
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     * @return
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2020. 7. 30.
     */
    public Result<Map<String, Object>> getObjectAsMap(String query, boolean required, String... columns) {
        return getObjectAsMap(query, SQLConsumer.DO_NOTHING, required, columns);
    }

    /**
     * 데이터 1개를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            요청쿼리
     * @param setter
     *            요췅쿼리 파라미터 설정 객체
     * @param required
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     * @return
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2020. 7. 30.
     */
    public Result<Map<String, Object>> getObjectAsMap(String query, SQLConsumer<PreparedStatement> setter,
            boolean required, String... columns)
            throws EmptyResultDataAccessException, IncorrectResultSizeDataAccessException {
        return getObject(query, setter, ENTITY_DTO_MAP, required, columns);
    }

    /**
     * 데이터 1개를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            요청쿼리
     * @param setter
     *            요췅쿼리 파라미터 설정 객체
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     * @return
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2020. 7. 30.
     */
    public Result<Map<String, Object>> getObjectAsMap(String query, SQLConsumer<PreparedStatement> setter,
            String... columns) {
        return getObjectAsMap(query, setter, false, columns);
    }

    /**
     * 데이터 1개를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param query
     *            요청쿼리
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     * @return
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2020. 7. 30.
     */
    public Result<Map<String, Object>> getObjectAsMap(String query, String... columns) {
        return getObjectAsMap(query, SQLConsumer.DO_NOTHING, false, columns);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuery(java.lang.String)
     */
    @Override
    public String getQuery(String name) {
        return this.querySource.getMessage(name, null, null);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuery(java.lang.String,
     *      java.lang.Object[], java.util.Locale)
     */
    @Override
    public String getQuery(String name, Object[] args, Locale locale) {
        return this.querySource.getMessage(name, args, locale);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuery(java.lang.String,
     *      java.lang.Object[], java.lang.String, java.util.Locale)
     */
    @Override
    public String getQuery(String name, Object[] args, String defaultMessage, Locale locale) {
        return this.querySource.getMessage(name, args, defaultMessage, locale);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuerySource()
     */
    @Override
    public ReloadableResourceBundleMessageSource getQuerySource() {
        return this.querySource;
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * <font color="red"><b>DB 조회 결과 데이터 타입과 반환데이터 타입이 서로 일치하는 것이 확실하지 않은 경우,
     * {@link #getValue(String, SQLConsumer, boolean, String, Function)}을 사용하기
     * 바랍니다.</b></font>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		parkjunhong77@gmail.com			최초 작성
     * 2020. 8. 13.     parkjunhong77@gmail.com         required == false 인 경우 Map<> 데이터 검증 추가.
     * </pre>
     *
     * @param <T>
     * @param query
     *            조회 쿼리
     * @param setter
     *            쿼리 파라미터 설정 객체
     * @param required
     *            데이터 필수 반환 여부
     * @param column
     *            컬럼명
     * @return
     *
     * @since 2020. 7. 30.
     */
    public <T> Result<T> getValue(String query, SQLConsumer<PreparedStatement> setter, boolean required,
            String column) {
        return getValue(query, setter, required, column, null);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 2.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <T>
     * @param query
     *            조회 쿼리
     * @param setter
     *            쿼리 파라미터 설정 객체
     * @param required
     *            데이터 필수 반환 여부
     * @param column
     *            컬럼명
     * @param converter
     *            컬럼에 해당하는 데이터를 받고자 하는 데이터 타입으로 변환하는 함수
     * @return
     * @throws EmptyResultDataAccessException
     *             required 값이 <code>true</code>인 경우 조회 결과가 없는 경우
     * @throws IncorrectResultSizeDataAccessException
     *             조회 결과 데이터 개수가 2개 이상인 경우
     *
     * @since 2022. 3. 2.
     * @version 1.8.0
     */
    @SuppressWarnings("unchecked")
    public <T> Result<T> getValue(String query, SQLConsumer<PreparedStatement> setter, boolean required, String column,
            Function<Object, T> converter) {
        Result<Map<String, Object>> mapResult = getObjectAsMap(query, setter, required, column);

        if (!mapResult.getResult()) {
            return new Result<T>().setMessage(mapResult.getMessage());
        }

        Map<String, Object> mapData = mapResult.getData();
        Object o = null;
        if (mapData == null || (o = mapData.get(column)) == null) {
            if (required) {
                throw new EmptyResultDataAccessException(1);
            } else {
                return new Result<T>(null, true);
            }
        } else {
            return new Result<T>(converter != null //
                    ? converter.apply(o)//
                    : (T) o //
                    , true);
        }
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * <font color="red"><b>DB 조회 결과 데이터 타입과 반환데이터 타입이 서로 일치하는 것이 확실하지 않은 경우,
     * {@link #getValue(String, String, Function)}을 사용하기 바랍니다.</b></font>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <T>
     * @param query
     *            조회 쿼리
     * @param setter
     *            쿼리 파라미터 설정 객체
     * @param column
     *            컬럼명
     * @return
     *
     * @since 2022. 3. 28.
     * @version 0.3.0
     */
    public <T> Result<T> getValue(String query, SQLConsumer<PreparedStatement> setter, String column) {
        return getValue(query, setter, false, column, null);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * <font color="red"><b>DB 조회 결과 데이터 타입과 반환데이터 타입이 서로 일치하는 것이 확실하지 않은 경우,
     * {@link #getValue(String, String, Function)}을 사용하기 바랍니다.</b></font>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <T>
     * @param query
     *            조회쿼리
     * @param column
     *            조회할 컬럼명
     * @return
     *
     * @since 2020. 7. 30.
     */
    public <T> Result<T> getValue(@NotEmpty String query, @NotEmpty String column) {
        return getValue(query, SQLConsumer.DO_NOTHING, false, column);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * <font color="red"><b>DB 조회 결과 데이터 타입과 반환데이터 타입이 서로 일치하는 것이 확실하지 않은 경우,
     * {@link #getValue(String, String, boolean, Function)}을 사용하기
     * 바랍니다.</b></font>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <T>
     *            데이터 타입
     * @param query
     *            조회 쿼리
     * @param column
     *            조회할 컬럼명
     * @param required
     *            데이터 필수 여부
     * @return
     *
     * @since 2020. 7. 30.
     */
    public <T> Result<T> getValue(@NotEmpty String query, @NotEmpty String column, boolean required) {
        return getValue(query, SQLConsumer.DO_NOTHING, required, column);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 2.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <T>
     *            데이터 타입
     * @param query
     *            조회 쿼리
     * @param column
     *            조회할 컬럼명
     * @param required
     *            데이터 필수 여부
     * @param converter
     *            컬럼에 해당하는 데이터를 받고자 하는 데이터 타입으로 변환하는 함수
     * @return
     *
     * @since 2022. 3. 2.
     * @version 1.8.0
     */
    public <T> Result<T> getValue(@NotEmpty String query, @NotEmpty String column, boolean required,
            Function<Object, T> converter) {
        return getValue(query, SQLConsumer.DO_NOTHING, required, column, converter);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 2.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <T>
     * @param query
     *            조회쿼리
     * @param column
     *            조회할 컬럼명
     * @param converter
     *            컬럼에 해당하는 데이터를 받고자 하는 데이터 타입으로 변환하는 함수
     * @return
     *
     * @since 2022. 3. 2.
     * @version 1.8.0
     */
    public <T> Result<T> getValue(@NotEmpty String query, @NotEmpty String column, Function<Object, T> converter) {
        return getValue(query, SQLConsumer.DO_NOTHING, false, column, converter);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * <font color="red"><b>DB 조회 결과 데이터 타입과 반환데이터 타입이 서로 일치하는 것이 확실하지 않은 경우,
     * {@link #getValues(String, SQLConsumer, String, Function)} 를 사용하기
     * 바랍니다.</b></font>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		parkjunhong77@gmail.com			최초 작성
     * 2020. 11. 21.    parkjunhong77@gmail.com			내부 버그 수정. getListAsMap(String, SQLConsumer<PreparedStatement>, String...)에 SQLConsumer<PreparedStatement> 전달 누락 수정
     * </pre>
     *
     * @param <T>
     * @param query
     *            조회쿼리
     * @param setter
     *            쿼리 파라미터 설정 객체
     * @param column
     *            컬럼명
     * @return
     *
     * @since 2020. 7. 30.
     */
    public <T> Result<List<T>> getValues(@NotEmpty String query, SQLConsumer<PreparedStatement> setter,
            @NotEmpty String column) {
        return getValues(query, setter, column, null);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 2.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <T>
     * @param query
     *            조회쿼리
     * @param setter
     *            쿼리 파라미터 설정 객체
     * @param column
     *            컬럼명
     * @param converter
     *            컬럼에 해당하는 데이터를 받고자 하는 데이터 타입으로 변환하는 함수
     * @return
     *
     * @since 2022. 3. 2.
     * @version 1.8.0
     */
    @SuppressWarnings("unchecked")
    public <T> Result<List<T>> getValues(@NotEmpty String query, SQLConsumer<PreparedStatement> setter,
            @NotEmpty String column, Function<Object, T> converter) {
        Result<List<Map<String, Object>>> mapResult = getListAsMap(query, setter, column);

        if (!mapResult.getResult()) {
            return new Result<List<T>>().setMessage(mapResult.getMessage());
        }

        List<Map<String, Object>> mapData = mapResult.getData();
        if (mapData != null) {
            List<T> data = mapData.stream() //
                    .map(m -> converter != null //
                            ? converter.apply(m.get(column)) //
                            : (T) m.get(column)) //
                    .collect(Collectors.toList());

            return new Result<>(data, true);
        } else {
            return new Result<>(null, true);
        }

    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * <font color="red"><b>DB 조회 결과 데이터 타입과 반환데이터 타입이 서로 일치하는 것이 확실하지 않은 경우,
     * {@link #getValues(String, String, Function)}을 사용하기 바랍니다.</b></font>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <T>
     * @param query
     *            조회쿼리
     * @param column
     *            조회할 컬럼명
     * @return
     *
     * @since 2020. 7. 30.
     */
    public <T> Result<List<T>> getValues(@NotEmpty String query, @NotEmpty String column) {
        return getValues(query, SQLConsumer.DO_NOTHING, column);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 2.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param <T>
     * @param query
     *            조회쿼리
     * @param column
     *            컬럼명
     * @param converter
     *            컬럼에 해당하는 데이터를 받고자 하는 데이터 타입으로 변환하는 함수
     * @return
     *
     * @since 2022. 3. 2.
     * @version 1.8.0
     */
    public <T> Result<List<T>> getValues(@NotEmpty String query, @NotEmpty String column,
            Function<Object, T> converter) {
        return getValues(query, SQLConsumer.DO_NOTHING, column, converter);
    }

    /**
     * 쿼리 파라미터를 배열로 반환합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 30.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param parameters
     *            쿼리 파라미터.
     * @return
     *
     * @since 2021. 11. 30.
     * @version 1.8.0
     */
    protected final Object[] objectArray(Object... parameters) {
        return parameters == null ? new Object[0] : parameters;
    }

    /**
     * 쿼리 정보 객체를 설정합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		parkjunhong77@gmail.com			최초 작성
     * </pre>
     *
     * @param querySource
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    public abstract void setQuerySource(ReloadableResourceBundleMessageSource querySource);

    /**
     * 특정 쿼리에 대한 개수를 제공하는 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 22.		parkjunhong77@gmail.com			최초 작성
     * 2022. 8. 2.      parkjunhong77@gmail.com     테이블 동적 alias 생성시 오류 수정
     * </pre>
     *
     * @param query
     * @return
     *
     * @since 2020. 1. 22.
     * @version 0.0.6
     */
    public String wrapQueryForCount(String query) {
        AssertUtils2.notNull(query);

        StringBuffer queryBuffer = new StringBuffer("SELECT count(*) AS count FROM (");
        queryBuffer.append(' ');
        queryBuffer.append(query);
        queryBuffer.append(" ) tbl");
        queryBuffer.append(UUID.randomUUID().toString().replace("-", ""));
        queryBuffer.append("");

        return queryBuffer.toString();
    }
}
