/*
 * Copyright 2019 Park Jun-Hong_(parkjunhong77@gmail.com)
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DataSourceUtils;

import open.commons.core.Result;
import open.commons.core.annotation.ColumnDef;
import open.commons.core.collection.FIFOMap;
import open.commons.core.database.ConnectionCallbackBroker;
import open.commons.core.database.ConnectionCallbackBroker2;
import open.commons.core.database.DefaultConCallbackBroker2;
import open.commons.core.database.IConnectionCallbackSetter;
import open.commons.core.function.SQLBiFunction;
import open.commons.core.function.SQLConsumer;
import open.commons.core.function.SQLFunction;
import open.commons.core.function.SQLTripleFunction;
import open.commons.core.test.StopWatch;
import open.commons.core.text.NamedTemplate;
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
 * 
 * <br>
 * 
 * <pre>
 * [개정이력]
 *      날짜      | 작성자   |	내용
 * ------------------------------------------
 * 2019. 3. 28.     박준홍     최초 작성
 * 2020. 4. 15.     박준홍     여러 개의 DataSource 지원.
 * </pre>
 * 
 * @since 2019. 3. 28.
 * @version 0.1.0
 * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
 */
@SuppressWarnings("deprecation")
public abstract class AbstractGenericDao implements IGenericDao {

    /** {@link Map} 형태로 DB 조회결과를 제공하는 DTO 타입 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static final Class<Map<String, Object>> ENTITY_DTO_MAP = (Class<Map<String, Object>>) (Class) FIFOMap.class;

    /**
     * 데이터와 {@link PreparedStatement}를 연결하는 Setter를 제공합니다.
     * 
     * @params 파라미터 배열. NotNull
     * 
     * @return 데이터 Setter
     * 
     * @deprecated Use {@link SQLConsumer#setParameters(Object[])}
     */
    protected static final Function<Object[], SQLConsumer<PreparedStatement>> PSSetter = params -> stmt -> {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    };

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
     * @param c
     *            {@link Connection}
     * @param t
     *            {@link JdbcTemplate}
     */
    private final BiFunction<Connection, JdbcTemplate, Connection> CONN_CREATOR = (c, t) -> {
        return (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class<?>[] { ConnectionProxy.class }, new CloseSuppressingInvocationHandler(c, t));
    };

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    public AbstractGenericDao() {
    }

    /**
     * 쿼리 템플릿에 에 IN Clause(<code>"IN ( ?, ?, ...)")를 추가합니다. <br>
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 28.		박준홍			최초 작성
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
     * @author parkjunhong77@gmail.com
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
     * 2019. 6. 12.     박준홍         최초 작성
     * </pre>
     *
     * @param sqlBuffer
     *            쿼리 버퍼
     * @param inParamCount
     *            IN ( ... )에 사용될 파라미터 개수
     *
     * @since 2019. 6. 12.
     * @version
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
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
     * 2019. 6. 12.		박준홍			최초 작성
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    protected void addQueryForInClause(StringBuffer sqlBuffer, String concatenator, String columnName, int inParamCount) {

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

    private <E> ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>> createBroker(@NotNull @NotEmpty List<E> data,
            @NotNull Function<List<E>, SQLConsumer<PreparedStatement>> psSetterProvider, @NotNull String headerQuery, @NotNull String valueQuery, String concatForVQ,
            String tailQuery) {

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
     * 2020. 7. 21.		박준홍			최초 작성
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    protected final <E> ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] createConnectionCallbackBrokers(@NotNull List<E> data,
            @NotNull Function<List<E>, SQLConsumer<PreparedStatement>> psSetterProvider, @Min(1) int partitionSize, @NotNull String headerQuery, @NotNull String valueQuery,
            String concatForVQ, String tailQuery) {

        if (data == null || data.size() < 1) {
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
     * 2020. 7. 21.		박준홍			최초 작성
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    protected final <E> ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] createConnectionCallbackBrokers(@NotNull List<E> data,
            @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize, @NotNull String headerQuery, @NotNull String valueQuery,
            String concatForVQ, String tailQuery) {

        Function<List<E>, SQLConsumer<PreparedStatement>> psSetterProvider = params -> {
            SQLConsumer<PreparedStatement> con = stmt -> {
                int i = 0;
                for (E param : params) {
                    i = dataSetter.apply(stmt, i, param);
                }

                // begin - PATCH [2020. 8. 12.]: 데이터 바인딩 직후 Collection 해제 | Park_Jun_Hong_(parkjunhong77@gmail.com)
                params.clear();
                // end - Park_Jun_Hong_(parkjunhong77@gmail.com), 2020. 8. 12.
            };

            return con;
        };

        return createConnectionCallbackBrokers(data, psSetterProvider, partitionSize, headerQuery, valueQuery, concatForVQ, tailQuery);
    }

    private <E> List<E> createObject(@NotNull ResultSet rs, @NotNull Class<E> entity, String... columns) throws SQLException {

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
     * 2022. 3. 28.     박준홍         최초 작성
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
     * @author parkjunhong77@gmail.com
     */
    protected String createQueryForInClause(String queryName, int inParamCount) {

        // #1. 쿼리 버퍼
        StringBuffer queryBuffer = new StringBuffer(getQuery(queryName));

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
     * 2022. 3. 28.		박준홍			최초 작성
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
     * @author parkjunhong77@gmail.com
     */
    protected String createQueryForInClause(String queryName, String inClauseName, int inParamCount) {

        // #1. 쿼리 템플릿
        NamedTemplate queryTpl = new NamedTemplate(getQuery(queryName));

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
     * 2019. 3. 28.		박준홍			최초 작성
     * 2019. 6. 5.		박준홍			작업용 Connection 객체 생성 로직 수직
     * </pre>
     *
     * @param act
     *            {@link Connection}을 전달받아 요청쿼리를 처리하는 객체
     * @return 쿼리처리 결과.
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    protected abstract <R> R execute(@NotNull SQLFunction<Connection, R> act) throws SQLException;

    /**
     * 조회된 데이터 개수를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 28.		박준홍			최초 작성
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
     * @author parkjunhong77@gmail.com
     * 
     * @see CountDTO
     */
    protected Result<Integer> executeCountOf(@NotNull String countQuery, Object... params) {
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
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param broker
     *            요청쿼리와 쿼리 파라미터를 처리하는 객체
     * @param entity
     *            요청쿼리 처리 결과 데이타 모델
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과.
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @throws SQLException
     * 
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * @see {@link ColumnDef}
     */
    private <E> List<E> executeQuery(@NotNull ConnectionCallbackBroker broker, @NotNull Class<E> entity, String... columns) throws SQLException {

        List<E> data = null;
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            data = execute(con -> {
                PreparedStatement pstmt = con.prepareStatement(broker.getQuery());

                IConnectionCallbackSetter setter = broker.getSetter();
                if (setter != null) {
                    setter.set(pstmt);
                }

                ResultSet rs = pstmt.executeQuery();

                String label = "execute-query";
                watch.record(label);
                logger.trace("Elapsed.execute-query={}", watch.getAsPretty(label));

                try {
                    return createObject(rs, entity, columns);
                } finally {
                    label = "create-objects";
                    watch.record(label);
                    logger.trace("Elapsed.create-objects={}", watch.getAsPretty(label));
                }
            });
            return data;
        } finally {
            watch.stop();
            logger.trace("Data.count: {}, Elapsed.total: {}", data != null ? NumberUtils.INT_TO_STR.apply(data.size()) : 0, watch.getAsPretty());
        }
    }

    /**
     * 요청쿼리를 실행하고 결과를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 28.     박준홍         최초 작성
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
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과.
     * @throws SQLException
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    private <S, E> List<E> executeQuery(@NotNull ConnectionCallbackBroker2<S> broker, @NotNull Class<E> entity, String... columns) throws SQLException {
        return executeQuery(broker, rs -> createObject(rs, entity, columns));
    }

    /**
     * 요청쿼리를 실행하고 결과를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 4. 23.		박준홍			최초 작성
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    private <S, E> List<E> executeQuery(@NotNull ConnectionCallbackBroker2<S> broker, SQLFunction<ResultSet, List<E>> creator) throws SQLException {
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
            logger.trace("Data.count: {}, Elapsed.total: {}", data != null ? NumberUtils.INT_TO_STR.apply(data.size()) : 0, watch.getAsPretty());
        }
    }

    /**
     * 단일/다중 (Insert/Update/Delete) 쿼리 요청을 처리합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param brokers
     *            요청쿼리 처리 객체
     * @return 쿼리 처리결과.
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @deprecated Use {@link #executeUpdate(ConnectionCallbackBroker2...)}. Not support any more.
     */
    public Result<Integer> executeUpdate(ConnectionCallbackBroker... brokers) {
        return executeUpdate(Arrays.asList(brokers));
    }

    /**
     * 단일/다중 (Insert/Update/Delete) 쿼리 요청을 처리합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param brokers
     *            요청쿼리 처리 객체
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    @SuppressWarnings("unchecked")
    public <E> Result<Integer> executeUpdate(ConnectionCallbackBroker2<E>... brokers) {

        Result<Integer> result = new Result<>();

        StopWatch watch = new StopWatch();
        watch.start();

        Integer updated = 0;
        try {
            updated = execute(con -> {
                int pos = 0;
                DefaultConnectionCallback2<E> action = null;
                int total = 0;
                int inserted = 0;
                try {
                    for (ConnectionCallbackBroker2<E> broker : brokers) {
                        action = new DefaultConnectionCallback2<E>(broker);
                        inserted = action.doInConnection(con);
                        total += inserted;

                        watch.record("inserted");
                        logger.trace("Data.count: {}, Elapsed.{}: {}", inserted, inserted, watch.getAsPretty("inserted"));

                        pos++;
                    }
                } catch (Exception e) {
                    logger.error("data.pos={}, cause={}", pos, e.getMessage(), e);
                    throw e;
                }

                return total;
            });

            result.andTrue().setData(updated);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result.setMessage(e.getMessage());
        } finally {
            watch.stop();
            logger.trace("Data.count: {}, Elapsed.total: {}", updated, watch.getAsPretty());
        }

        return result;
    }

    /**
     * 다중 (Insert/Update/Delete) 쿼리 요청을 처리합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param brokers
     *            요청쿼리 처리 객체
     * @return 쿼리 처리결과
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @deprecated Use {@link #executeUpdate(ConnectionCallbackBroker2...)}. Not support any more.
     */
    public Result<Integer> executeUpdate(@NotNull List<ConnectionCallbackBroker> brokers) {

        Result<Integer> result = new Result<>();

        StopWatch watch = new StopWatch();
        watch.start();

        Integer updated = 0;
        try {
            updated = execute(con -> {
                DefaultConnectionCallback action = null;
                int inserted = 0;
                for (ConnectionCallbackBroker broker : brokers) {
                    action = new DefaultConnectionCallback(broker);
                    inserted += action.doInConnection(con);
                }
                return inserted;
            });

            result.andTrue().setData(updated);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result.setMessage(e.getMessage());
        } finally {
            watch.stop();
            logger.trace("Data.count: {}, Elapsed.total: {}", updated, watch.getAsPretty());
        }

        return result;
    }

    /**
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 21.		박준홍			최초 작성
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
     * @param tailQuery
     *            추가 쿼리
     * @return
     *
     * @since 2020. 1. 20.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @deprecated Not use any more.
     */
    @SuppressWarnings("unused")
    private final <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull Function<List<E>, SQLConsumer<PreparedStatement>> psSetterProvider, @Min(1) int partitionSize,
            @NotNull String headerQuery, @NotNull String valueQuery, String tailQuery) {
        return executeUpdate(data, psSetterProvider, partitionSize, headerQuery, valueQuery, "", tailQuery);
    }

    /**
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 6. 15.		박준홍			최초 작성
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
     * @since 2020. 6. 15.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @deprecated Not use any more.
     */
    private final <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull Function<List<E>, SQLConsumer<PreparedStatement>> psSetterProvider, @Min(1) int partitionSize,
            @NotNull String headerQuery, @NotNull String valueQuery, String concatForVQ, String tailQuery) {

        if (data == null || data.size() < 1) {
            return new Result<>(0, true);
        }

        // #1. 데이터 추가 다중 실행 정보 생성.
        ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] brokers = createConnectionCallbackBrokers(data, psSetterProvider, partitionSize, headerQuery, valueQuery,
                concatForVQ, tailQuery);
        // #2. 데이터 추가 실행.
        return executeUpdate(brokers);
    }

    /**
     * 다수 개의 데이터를 설정된 크기로 나누어 추가합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 17.     박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param dataSetter
     *            다수 개의 객체형 파라미터를 {@link PreparedStatement}에 설정하는 기능.<br>
     *            참조: {@link SQLTripleFunction#setParameters(String...)}
     * @param partitionSize
     *            데이터 분할 크기
     * @param valueQuery
     *            데이터 바인딩 쿼리
     * @return
     *
     * @since 2020. 1. 17.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @see SQLTripleFunction#setParameters(String...)
     */
    public <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String valueQuery) {
        // !!! 세부 기능을 구현해야 합니다. !!!
        throw new UnsupportedOperationException("세부 기능을 구현해야 합니다.");
    }

    /**
     * 다수 개의 데이터를 설정된 크기로 나누어 추가합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 11.		박준홍			최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param dataSetter
     *            다수 개의 객체형 파라미터를 {@link PreparedStatement}에 설정하는 기능.<br>
     *            참조: {@link SQLTripleFunction#setParameters(String...)}
     * @param partitionSize
     *            데이터 분할 크기
     * @param headerQuery
     *            데이터 추가 헤더 쿼리. (테이블 및 컬럼 정보가 기재됨)
     * @param valueQuery
     *            데이터 바인딩 쿼리
     * @return
     *
     * @since 2021. 11. 11.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String headerQuery, @NotNull String valueQuery) {
        // !!! 세부 기능을 구현해야 합니다. !!!
        throw new UnsupportedOperationException("세부 기능을 구현해야 합니다.");
    }

    /**
     * 다수 개의 데이터를 설정된 크기로 나누어 추가합니다. <br>
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 20.		박준홍			최초 작성
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
     * @param tailQuery
     *            쿼리 마지막
     * @return
     *
     * @since 2020. 1. 20.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @see SQLTripleFunction#setParameters(String...)
     */
    public final <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String headerQuery, @NotNull String valueQuery, String tailQuery) {
        return executeUpdate(data, dataSetter, partitionSize, headerQuery, valueQuery, "", tailQuery);
    }

    /**
     * 다수 개의 데이터를 설정된 크기로 나누어 추가합니다. <br>
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 6. 15.		박준홍			최초 작성
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
     * @since 2020. 6. 15.
     * @version 0.2.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @see SQLTripleFunction#setParameters(String...)
     */
    public final <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String headerQuery, @NotNull String valueQuery, String concatForVQ, String tailQuery) {
        ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] brokers = createConnectionCallbackBrokers(data, dataSetter, partitionSize, headerQuery, valueQuery, concatForVQ,
                tailQuery);
        return executeUpdate(brokers);
    }

    /**
     * 단일 요청쿼리를 처리합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @return 쿼리 처리결과
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @deprecated Not support any more.
     */
    public Result<Integer> executeUpdate(@NotNull String query, IConnectionCallbackSetter setter) {
        return executeUpdate(new ConnectionCallbackBroker(query, setter));
    }

    /**
     * 단일 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 29.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @return 쿼리 처리결과
     *
     * @since 2019. 3. 29.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public Result<Integer> executeUpdate(@NotNull String query, SQLConsumer<PreparedStatement> setter) {
        return executeUpdate(query, setter, false);
    }

    @SuppressWarnings("unchecked")
    public Result<Integer> executeUpdate(@NotNull String query, SQLConsumer<PreparedStatement> setter, boolean forStoredProcedure) {
        return executeUpdate(new DefaultConCallbackBroker2(query, setter, forStoredProcedure));
    }

    /**
     * 데이타 타입에 맞는 객체 생성자를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * 2020. 6. 12.     박준홍         조회 결과를 java.util.Map 형태로 받는 경우 지원
     * </pre>
     *
     * @param entity
     *            쿼리처리 결과 데이타 타입
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <R> SQLBiFunction<ResultSet, Integer, R> findCreator(@NotNull Class<R> entity, String... columns) {
        Arrays.sort(columns);
        String key = String.join("-", entity.getName(), String.valueOf(Arrays.toString(columns).hashCode()));
        SQLBiFunction<ResultSet, Integer, R> creator = (SQLBiFunction<ResultSet, Integer, R>) CREATORS.get(key);

        if (creator == null) {
            // begin - PATCH [2020. 6. 12.]: 조회 결과를 java.util.Map 형태로 받는 경우 지원.| Park_Jun_Hong_(parkjunhong77@gmail.com)
            if (Map.class.isAssignableFrom(entity)) {
                // DAO Entity가 Map.class 인 경우는 Map.class 가 여러 가지의 데이터 타입을 대신하는 것이기 때문에,
                // Entity 생성 함수를 별도로 저장하지 않는다.
                creator = (rs, rowNum) -> {
                    try {
                        Map data = (Map) entity.newInstance();
                        for (String clmn : columns) {
                            data.put(clmn, rs.getObject(clmn));
                        }

                        return (R) data;
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new SQLException(String.format("%s 객체 생성시 에러가 발생하였습니다. 원인=%s", entity, e.getMessage()), e);
                    }
                };
                // end - Park_Jun_Hong_(parkjunhong77@gmail.com), 2020. 6. 12.
            } else {
                creator = (rs, rowNum) -> SQLUtils.newInstance(entity, rs, columns);
                CREATORS.put(entity.getName(), creator);
            }
        }

        return creator;
    }

    /**
     * 작업용 Connection 객체를 제공합니다.<br>
     * Springframework 5.x 부터 4.x에 존재하던 아래 메소드를 제거함에 따라 호환성 제공을 목적으로 합니다.
     * 
     * <pre>
     * public NativeJdbcExtractor getNativeJdbcExtractor() {
     *     return this.nativeJdbcExtractor;
     * }
     * </pre>
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 6. 5.		박준홍			최초 작성
     * 2020. 2. 13.		박준홍			springframework 5.1.13 >= 대응
     * 2020. 4. 15.		박준홍			private -> protected
     * </pre>
     *
     * @param con
     * @param jdbcTemplate
     * @return
     * @throws SQLException
     *
     * @since 2019. 6. 5.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    protected final Connection getConnection(@NotNull Connection con, @NotNull JdbcTemplate jdbcTemplate) throws SQLException {
        try {
            Connection targetCon = DataSourceUtils.getTargetConnection(con);

            if (targetCon != null) {
                return targetCon;
            } else {
                return CONN_CREATOR.apply(con, jdbcTemplate);
            }

        } catch (NoSuchMethodError e) {
            return CONN_CREATOR.apply(con, jdbcTemplate);
        }
    }

    /**
     * 전달된 쿼리에 대한 조회 결과 데이터 개수를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 22.		박준홍			최초 작성
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public Result<Integer> getCount(@NotNull String selectQuery, Object... params) {
        String query = wrapQueryForCount(selectQuery);
        return executeCountOf(query, params);
    }

    /**
     * 데이터 조회 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param entity
     *            결과 데이타 타입
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <E> Result<List<E>> getList(@NotNull String query, @NotNull Class<E> entity, String... columns) {
        return getList(query, (IConnectionCallbackSetter) null, entity, columns);
    }

    /**
     * 데이터 조회 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param <E>
     *            요청받을 데이타 타입
     * @param query
     *            데이터 조회 요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @param entity
     *            결과 데이타 타입
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     * 
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <E> Result<List<E>> getList(@NotNull String query, @NotNull IConnectionCallbackSetter setter, @NotNull Class<E> entity, String... columns) {

        Result<List<E>> result = new Result<>();

        try {
            List<E> list = executeQuery(new ConnectionCallbackBroker(query, setter), entity, columns);
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
     * 2020. 7. 22.		박준홍			최초 작성
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
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2020. 7. 22.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <E> Result<List<E>> getList(@NotNull String query, int size, @NotNull IConnectionCallbackSetter setter, @NotNull Class<E> entity, String... columns) {

        Result<List<E>> result = new Result<>();

        try {
            List<E> list = executeQuery(new ConnectionCallbackBroker(query, setter), entity, columns);
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
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체 <br>
     * @param entity
     *            결과 데이타 타입
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <E> Result<List<E>> getList(@NotNull String query, SQLConsumer<PreparedStatement> setter, @NotNull Class<E> entity, String... columns) {

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
     * 2021. 4. 23.		박준홍			최초 작성
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <E> Result<List<E>> getList(@NotNull String query, SQLConsumer<PreparedStatement> setter, @NotNull SQLFunction<ResultSet, List<E>> creator) {

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
     * 2021. 4. 23.		박준홍			최초 작성
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <E> Result<List<E>> getList(@NotNull String query, @NotNull SQLFunction<ResultSet, List<E>> creator) {

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
     * 2020. 7. 22.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param setter
     *            데이터 바인더
     * @param entity
     *            결과 데이터 타입.
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2020. 7. 22.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Result<List<Map<String, Object>>> getListAsMap(@NotNull String query, SQLConsumer<PreparedStatement> setter, String... columns) {
        Class<Map<String, Object>> entity = (Class<Map<String, Object>>) (Class) FIFOMap.class;
        return getList(query, setter, entity, columns);
    }

    /**
     * 데이터 조회 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 6. 12.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param entity
     *            결과 데이터 타입.
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2020. 6. 12.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public Result<List<Map<String, Object>>> getListAsMap(@NotNull String query, String... columns) {
        return getListAsMap(query, null, columns);
    }

    /**
     * 데이터 1개 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
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
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <T> Result<T> getObject(@NotNull String query, @NotNull Class<T> entity, boolean required, String... columns) {
        return getObject(query, null, entity, required, columns);
    }

    /**
     * 데이터 1개 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            조회 요청쿼리
     * @param entity
     *            결과 데이타 타입
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <T> Result<T> getObject(@NotNull String query, @NotNull Class<T> entity, String... columns) {
        return getObject(query, null, entity, false, columns);
    }

    /**
     * 데이터 1개 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
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
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <T> Result<T> getObject(@NotNull String query, SQLConsumer<PreparedStatement> setter, @NotNull Class<T> entity, boolean required, String... columns) {
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
                    // (start) [BUG-FIX]: Result#result 설정 누락 수정 / Park_Jun_Hong_(parkjunhong77@gmail.com): 2019. 6. 12.
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
     * 2019. 3. 28.		박준홍			최초 작성
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
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <T> Result<T> getObject(@NotNull String query, SQLConsumer<PreparedStatement> setter, @NotNull Class<T> entity, String... columns) {
        return getObject(query, setter, entity, false, columns);
    }

    /**
     * 데이터 1개를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            조회 쿼리
     * @param required
     *            데이터 존재 필수 여부
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     * @return
     *
     * @since 2020. 7. 30.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public Result<Map<String, Object>> getObjectAsMap(@NotNull String query, boolean required, String... columns) {
        return getObjectAsMap(query, null, required, columns);
    }

    /**
     * 데이터 1개를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		박준홍			최초 작성
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
     *
     * @since 2020. 7. 30.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public Result<Map<String, Object>> getObjectAsMap(@NotNull String query, SQLConsumer<PreparedStatement> setter, boolean required, String... columns) {
        return getObject(query, setter, ENTITY_DTO_MAP, required, columns);
    }

    /**
     * 데이터 1개를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            요청쿼리
     * @param setter
     *            요췅쿼리 파라미터 설정 객체
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     * @return
     *
     * @since 2020. 7. 30.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public Result<Map<String, Object>> getObjectAsMap(@NotNull String query, SQLConsumer<PreparedStatement> setter, String... columns) {
        return getObjectAsMap(query, setter, false, columns);
    }

    /**
     * 데이터 1개를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            요청쿼리
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     * @return
     *
     * @since 2020. 7. 30.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public Result<Map<String, Object>> getObjectAsMap(@NotNull String query, String... columns) {
        return getObjectAsMap(query, null, false, columns);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuery(java.lang.String)
     */
    @Override
    public String getQuery(@NotNull String name) {
        return this.querySource.getMessage(name, null, null);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuery(java.lang.String, java.lang.Object[], java.util.Locale)
     */
    @Override
    public String getQuery(@NotNull String name, Object[] args, Locale locale) {
        return this.querySource.getMessage(name, args, locale);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuery(java.lang.String, java.lang.Object[], java.lang.String,
     *      java.util.Locale)
     */
    @Override
    public String getQuery(@NotNull String name, Object[] args, String defaultMessage, Locale locale) {
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
     * {@link #getValue(String, SQLConsumer, boolean, String, Function)}을 사용하기 바랍니다.</b></font>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		박준홍			최초 작성
     * 2020. 8. 13.     박준홍         required == false 인 경우 Map<> 데이터 검증 추가.
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <T> Result<T> getValue(@NotNull String query, SQLConsumer<PreparedStatement> setter, boolean required, String column) {
        return getValue(query, setter, required, column, null);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 2.		박준홍			최초 작성
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
     *
     * @since 2022. 3. 2.
     * @version 1.8.0
     * @author parkjunhong77@gmail.com
     */
    @SuppressWarnings("unchecked")
    public <T> Result<T> getValue(@NotNull String query, SQLConsumer<PreparedStatement> setter, boolean required, String column, Function<Object, T> converter) {
        Result<Map<String, Object>> mapResult = getObjectAsMap(query, setter, required, column);

        if (!mapResult.getResult()) {
            return new Result<T>().setMessage(mapResult.getMessage());
        }

        Map<String, Object> mapData = mapResult.getData();
        // begin - PATCH [2020. 8. 13.]: required == false 인 경우 Map<> 데이터 검증 추가. |
        // Park_Jun_Hong_(parkjunhong77@gmail.com)
        if (mapData == null && !required) {
            return new Result<T>(null, true);
        }
        // end - Park_Jun_Hong_(parkjunhong77@gmail.com), 2020. 8. 13.

        return new Result<T>(converter != null //
                ? converter.apply(mapData.get(column))//
                : (T) mapData.get(column) //
                , true);
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
     * 2022. 3. 28.		박준홍			최초 작성
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
     * @author parkjunhong77@gmail.com
     */
    public <T> Result<T> getValue(@NotNull String query, SQLConsumer<PreparedStatement> setter, String column) {
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
     * 2020. 7. 30.		박준홍			최초 작성
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <T> Result<T> getValue(@NotNull @NotEmpty String query, @NotNull @NotEmpty String column) {
        return getValue(query, null, false, column);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * <font color="red"><b>DB 조회 결과 데이터 타입과 반환데이터 타입이 서로 일치하는 것이 확실하지 않은 경우,
     * {@link #getValue(String, String, boolean, Function)}을 사용하기 바랍니다.</b></font>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		박준홍			최초 작성
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <T> Result<T> getValue(@NotNull @NotEmpty String query, @NotNull @NotEmpty String column, boolean required) {
        return getValue(query, null, required, column);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 2.		박준홍			최초 작성
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
     * @author parkjunhong77@gmail.com
     */
    public <T> Result<T> getValue(@NotNull @NotEmpty String query, @NotNull @NotEmpty String column, boolean required, Function<Object, T> converter) {
        return getValue(query, null, required, column, converter);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 2.		박준홍			최초 작성
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
     * @author parkjunhong77@gmail.com
     */
    public <T> Result<T> getValue(@NotNull @NotEmpty String query, @NotNull @NotEmpty String column, Function<Object, T> converter) {
        return getValue(query, null, false, column, converter);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * <font color="red"><b>DB 조회 결과 데이터 타입과 반환데이터 타입이 서로 일치하는 것이 확실하지 않은 경우,
     * {@link #getValues(String, SQLConsumer, String, Function)} 를 사용하기 바랍니다.</b></font>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 30.		박준홍			최초 작성
     * 2020. 11. 21.    박준홍			내부 버그 수정. getListAsMap(String, SQLConsumer<PreparedStatement>, String...)에 SQLConsumer<PreparedStatement> 전달 누락 수정
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <T> Result<List<T>> getValues(@NotNull @NotEmpty String query, SQLConsumer<PreparedStatement> setter, @NotNull @NotEmpty String column) {
        return getValues(query, setter, column, null);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 2.		박준홍			최초 작성
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
     * @author parkjunhong77@gmail.com
     */
    @SuppressWarnings("unchecked")
    public <T> Result<List<T>> getValues(@NotNull @NotEmpty String query, SQLConsumer<PreparedStatement> setter, @NotNull @NotEmpty String column, Function<Object, T> converter) {
        Result<List<Map<String, Object>>> mapResult = getListAsMap(query, setter, column);

        if (!mapResult.getResult()) {
            return new Result<List<T>>().setMessage(mapResult.getMessage());
        }

        List<Map<String, Object>> mapData = mapResult.getData();

        List<T> data = mapData.stream() //
                .map(m -> converter != null //
                        ? converter.apply(m.get(column)) //
                        : (T) m.get(column)) //
                .collect(Collectors.toList());

        return new Result<>(data, true);
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
     * 2020. 7. 30.		박준홍			최초 작성
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
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <T> Result<List<T>> getValues(@NotNull @NotEmpty String query, @NotNull @NotEmpty String column) {
        return getValues(query, null, column);
    }

    /**
     * 특정컬럼 데이터를 조회합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2022. 3. 2.		박준홍			최초 작성
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
     * @author parkjunhong77@gmail.com
     */
    public <T> Result<List<T>> getValues(@NotNull @NotEmpty String query, @NotNull @NotEmpty String column, @NotNull Function<Object, T> converter) {
        return getValues(query, null, column, converter);
    }

    /**
     * 쿼리 파라미터를 배열로 반환합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 30.		박준홍			최초 작성
     * </pre>
     *
     * @param parameters
     *            쿼리 파라미터.
     * @return
     *
     * @since 2021. 11. 30.
     * @version 1.8.0
     * @author parkjunhong77@gmail.com
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
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param querySource
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public abstract void setQuerySource(@NotNull ReloadableResourceBundleMessageSource querySource);

    /**
     * 특정 쿼리에 대한 개수를 제공하는 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 22.		박준홍			최초 작성
     * 2022. 8. 2.      박준홍     테이블 동적 alias 생성시 오류 수정
     * </pre>
     *
     * @param query
     * @return
     *
     * @since 2020. 1. 22.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public String wrapQueryForCount(@NotNull String query) {
        StringBuffer queryBuffer = new StringBuffer("SELECT count(*) AS count FROM (");
        queryBuffer.append(' ');
        queryBuffer.append(query);
        queryBuffer.append(" ) tbl");
        queryBuffer.append(UUID.randomUUID().toString().replace("-", ""));
        queryBuffer.append("");

        return queryBuffer.toString();
    }

    /**
     * Invocation handler that suppresses close calls on JDBC Connections. Also prepares returned Statement
     * (Prepared/CallbackStatement) objects.
     * 
     * @see java.sql.Connection#close()
     */
    private class CloseSuppressingInvocationHandler implements InvocationHandler {

        private final Connection target;

        private JdbcTemplate jdbcTemplate;

        public CloseSuppressingInvocationHandler(Connection target, JdbcTemplate jdbcTemplate) {
            this.target = target;
            this.jdbcTemplate = jdbcTemplate;
        }

        private void applyStatementSettings(JdbcTemplate jdbcTemplate, Statement stmt) throws SQLException {
            int fetchSize = jdbcTemplate.getFetchSize();
            if (fetchSize > 0) {
                stmt.setFetchSize(fetchSize);
            }
            int maxRows = jdbcTemplate.getMaxRows();
            if (maxRows > 0) {
                stmt.setMaxRows(maxRows);
            }

            DataSourceUtils.applyTimeout(stmt, this.jdbcTemplate.getDataSource(), jdbcTemplate.getQueryTimeout());
        }

        @SuppressWarnings("rawtypes")
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Invocation on ConnectionProxy interface coming in...

            if (method.getName().equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0]);
            } else if (method.getName().equals("hashCode")) {
                // Use hashCode of PersistenceManager proxy.
                return System.identityHashCode(proxy);
            } else if (method.getName().equals("unwrap")) {
                if (((Class) args[0]).isInstance(proxy)) {
                    return proxy;
                }
            } else if (method.getName().equals("isWrapperFor")) {
                if (((Class) args[0]).isInstance(proxy)) {
                    return true;
                }
            } else if (method.getName().equals("close")) {
                // Handle close method: suppress, not valid.
                return null;
            } else if (method.getName().equals("isClosed")) {
                return false;
            } else if (method.getName().equals("getTargetConnection")) {
                // Handle getTargetConnection method: return underlying Connection.
                return this.target;
            }

            // Invoke method on target Connection.
            try {
                Object retVal = method.invoke(this.target, args);

                // If return value is a JDBC Statement, apply statement settings
                // (fetch size, max rows, transaction timeout).
                if (retVal instanceof Statement) {
                    applyStatementSettings(jdbcTemplate, ((Statement) retVal));
                }

                return retVal;
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}
