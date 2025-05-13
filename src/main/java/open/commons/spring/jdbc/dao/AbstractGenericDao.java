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

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.jdbc.core.JdbcTemplate;

import open.commons.core.Result;
import open.commons.core.database.ConnectionCallbackBroker;
import open.commons.core.database.ConnectionCallbackBroker2;
import open.commons.core.database.DefaultConCallbackBroker2;
import open.commons.core.database.IConnectionCallbackSetter;
import open.commons.core.function.SQLConsumer;
import open.commons.core.function.SQLTripleFunction;
import open.commons.core.test.StopWatch;

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
public abstract class AbstractGenericDao extends AbstractGenericRetrieve {

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
    public <E> Result<Integer> executeUpdate(@NotNull ConnectionCallbackBroker2<E>... brokers) throws NullPointerException {

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
}
