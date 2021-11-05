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
 *
 * This file is generated under this project, "open-commons-spring-jdbc".
 *
 * Date  : 2020. 1. 22. 오전 12:50:06
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.spring.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.validation.constraints.NotNull;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import open.commons.Result;
import open.commons.function.HexaFunction;

/**
 * {@link ThreadPoolTaskExecutor}를 기반으로
 * 
 * @since 2020. 1. 22.
 * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
 * @version 0.0.6
 */
@Validated
public interface IAsyncSupportable {

    final Supplier<Object[]> SUPPLIER_OBJECT_ARR = () -> new Object[0];
    final Supplier<String[]> SUPPLIER_STRING_ARR = () -> new String[0];

    /**
     * 비동기 지원 메소드를 호출한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 21.     박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     *            조회 결과 데이터 타입.
     * @param <P>
     *            조회 파라미터 데이터 타입.
     * @param m
     *            {@link Repository} Bean이 제공하는 메소드
     * @param query
     *            데이터 조회 쿼리
     * @param totalCount
     *            전체 데이터 개수
     * @param partitionSize
     *            조회 데이터 최대 개수
     * @param type
     *            조회 데이터 타입 {@link Class}.
     * @param parameters
     *            조회 파라미터
     * @return
     *
     * @since 2020. 1. 21.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    default <E> List<Future<Result<List<E>>>> callAsync(@NotNull HexaFunction<String, Integer, Integer, Class<E>, Supplier<Object[]>, Supplier<String[]>, Result<List<E>>> m //
            , @NotNull String query, int totalCount, int partitionSize, @NotNull Class<E> type, @NotNull Supplier<Object[]> params, @NotNull Supplier<String[]> columns) {

        ThreadPoolTaskExecutor threadPool = getThreadPoolExecutor();

        int q = totalCount / partitionSize;
        double r = (double) totalCount / (double) partitionSize;

        List<Future<Result<List<E>>>> futures = new ArrayList<>();
        int pos = 0;

        while (pos < q) {
            futures.add(threadPool.submit(new AsyncSelectorBy<E>(m, query, pos * partitionSize, partitionSize, type, params, columns)));
            pos++;
        }

        if (r > 0) {
            futures.add(threadPool.submit(new AsyncSelectorBy<E>(m, query, pos * partitionSize, partitionSize, type, params, columns)));
        }

        return futures;
    }

    /**
     * 데이터 조회를 설정된 크기로 나누어서 실행한 후 취합한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 30.		박준홍			최초 작성
     * </pre>
     *
     * @param <E>
     *            조회 결과 데이터 모델
     * @param query
     *            데이터 조회 쿼리
     * @param totalCount
     *            전체 데이터 개수
     * @param partitionSize
     *            조회 데이터 최대 개수
     * @param type
     *            조회 데이터 타입 {@link Class}.
     * @param parameters
     *            조회 파라미터
     * @param columns
     *            읽을 컬럼목록.
     * @return
     *
     * @since 2020. 1. 30.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    default <E> Result<List<E>> executeParallel(@NotNull String query, int totalCount, int partitionSize, @NotNull Class<E> type, @NotNull Supplier<Object[]> params,
            @NotNull Supplier<String[]> columns) {

        List<E> data = new ArrayList<>();
        Consumer<Result<List<E>>> dataCollector = getCollector(data);
        Predicate<Result<List<E>>> failureFilter = t -> !t.getResult() || t.getData() == null;

        // 비동기 호출 결과
        List<Future<Result<List<E>>>> futures = callAsync(this::getList, query, totalCount, partitionSize, type, params, columns);
        // 실패하는 작업이 발생하는 경우 나머지 작업을 취소하기 위한.
        List<Future<Result<List<E>>>> clones = new ArrayList<>(futures);

        // #1. 데이터 수집
        Optional<Future<Result<List<E>>>> failure = futures.stream() //
                .filter(future -> {
                    try {
                        Result<List<E>> fresult = future.get();

                        if (failureFilter.test(fresult)) {
                            return true;
                        } else {
                            dataCollector.accept(fresult);
                            return false;
                        }
                    } catch (CancellationException | InterruptedException | ExecutionException e) {
                        return true;
                    }
                }).findAny();

        // #2. 병렬 처리 작업이 실패가 발생한 경우, 남아 있는 다른 작업을 취소.
        // Future.cancel(boolean) 함수가 호출되는 경우 I/O 관련 메소드에서 에러메시지가 발생할 수도 있음.
        clones.stream().forEach(f -> f.cancel(true));

        // #3. 데이터 확인
        Result<List<E>> result = new Result<>();
        if (failure.isPresent()) {
            try {
                result.setMessage(failure.get().get().getMessage());
            } catch (CancellationException | InterruptedException | ExecutionException e) {
                result.setMessage(e.getMessage());
            }
        } else {
            result.setData(data).andTrue();
        }

        return result;
    }

    /**
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 21.     박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     *            데이터 타입.
     * @param collector
     * @return
     *
     * @since 2020. 1. 21.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    default <E> Consumer<Result<List<E>>> getCollector(@NotNull final List<E> collector) {
        return t -> collector.addAll(t.getData());
    }

    /**
     * 전달된 쿼리에 대한 조회 결과 데이터 개수를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 22.     박준홍         최초 작성
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
    public Result<Integer> getCount(@NotNull String selectQuery, Object... params);

    /**
     * 데이터 개수를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 21.     박준홍         최초 작성
     * </pre>
     *
     * @param <P>
     *            파라미터 타입.
     * @param m
     *            데이터 개수 제공 {@link Repository} Bean 메소드.
     * @param parameters
     *            메소드 파라미터.
     * @return
     *
     * @since 2020. 1. 21.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    default <P> Result<Integer> getCount(@NotNull Supplier<Result<Integer>> m, @NotNull P parameters) {
        return m.get();
    }

    /**
     * 데이터 개수를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 21.     박준홍         최초 작성
     * </pre>
     *
     * @param <P>
     *            파라미터 타입.
     * @param m
     *            데이터 개수 제공 {@link Repository} Bean 메소드.
     * @param parameters
     *            메소드 파라미터.
     * @return
     *
     * @since 2020. 1. 21.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    default <P> Result<Integer> getCountBy(@NotNull Function<P, Result<Integer>> m, @NotNull P parameters) {
        return m.apply(parameters);
    }

    /**
     * 분할 조회를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 22.		박준홍			최초 작성
     * </pre>
     *
     * @param <E>
     *            조회 데이터 타입.
     * @param <P>
     *            라마티러 데이터 타입.
     * @param query
     *            조회 쿼리
     * @param partitionSize
     *            분할 크기
     * @param type
     *            조회 데이터 타입.
     * @param params
     *            조회 파라미터.
     * @return
     *
     * @since 2020. 1. 22.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    default <E, P> Result<List<E>> getList(@NotNull String query, int partitionSize, @NotNull Class<E> type, Object... params) {
        return getList(query, partitionSize, type, () -> params, SUPPLIER_STRING_ARR);
    }

    /**
     * 분할 조회를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 30.		박준홍			최초 작성
     * </pre>
     *
     * @param <E>
     *            조회 데이터 타입.
     * @param <P>
     *            라마티러 데이터 타입.
     * @param query
     *            조회 쿼리
     * @param partitionSize
     *            분할 크기
     * @param type
     *            조회 데이터 타입.
     * @param columns
     *            읽을 컬럼 목록.
     * @return
     *
     * @since 2020. 1. 30.
     * @version _._._
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    default <E, P> Result<List<E>> getList(@NotNull String query, int partitionSize, @NotNull Class<E> type, String... columns) {
        return getList(query, partitionSize, type, SUPPLIER_OBJECT_ARR, () -> columns);
    }

    /**
     * 분할 조회를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 30.		박준홍			최초 작성
     * </pre>
     *
     * @param <E>
     *            조회 데이터 타입.
     * @param <P>
     *            라마티러 데이터 타입.
     * @param query
     *            조회 쿼리
     * @param partitionSize
     *            분할 크기
     * @param type
     *            조회 데이터 타입.
     * @param params
     *            조회 파라미터.
     * @param columns
     *            읽을 컬럼 목록.
     * @return
     *
     * @since 2020. 1. 30.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    default <E, P> Result<List<E>> getList(@NotNull String query, int partitionSize, @NotNull Class<E> type, @NotNull Supplier<Object[]> params,
            @NotNull Supplier<String[]> columns) {
        // #1. 조회 데이터 개수
        Result<Integer> resultCount = getCount(query, params.get());
        if (!resultCount.getResult()) {
            return new Result<List<E>>().setMessage(resultCount.getMessage());
        }

        // #2. 분할 조회
        return executeParallel(query, resultCount.getData(), partitionSize, type, params, columns);
    }

    /**
     * * 설정된 위치부터 최대 정해진 개수만큼의 데이터를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 30.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 쿼리
     * @param offset
     *            읽어올 데이터 직전 위치. 데이터는 offset + 1 부터 읽는다.
     * @param fetch
     *            읽어올 데이터 개수.
     * @param dataType
     *            읽어올 데이터 모델.
     * @param params
     *            데이터 조회 쿼리 파라미터.
     * @param columns
     *            읽을 컬럼 목록.
     * @return
     * @throws NullPointerException
     * @throws IllegalArgumentException
     *
     * @since 2020. 1. 30.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <E> Result<List<E>> getList(@NotNull String query, int offset, int fetch, @NotNull Class<E> dataType, @NotNull Supplier<Object[]> params,
            @NotNull Supplier<String[]> columns) throws NullPointerException, IllegalArgumentException;

    /**
     * {@link ThreadPoolTaskExecutor} 를 제공한다.
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 22.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2020. 1. 22.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public @NotNull ThreadPoolTaskExecutor getThreadPoolExecutor();

    @Validated
    public static class AsyncSelectorBy<E> implements Callable<Result<List<E>>> {

        /** 데이터 개수 제공 {@link Repository} Bean 메소드. */
        private final HexaFunction<String, Integer, Integer, Class<E>, Supplier<Object[]>, Supplier<String[]>, Result<List<E>>> m;
        /** 쿼리 */
        private String query;
        /** 데이터 조회 커서 위치. (exclusive) */
        private final Integer offset;
        /** 데이터 조회 최대 개수. */
        private final Integer count;
        /** 조회 데이터 타입. */
        private Class<E> type;
        /** {@link Repository} Bean 메소드 파라미터 */
        private final Supplier<Object[]> params;
        /** 읽을 컬럼 목록 */
        private final Supplier<String[]> columns;

        /**
         * <br>
         * 
         * <pre>
         * [개정이력]
         *      날짜      | 작성자   |   내용
         * ------------------------------------------
         * 2020. 1. 21.     박준홍         최초 작성
         * </pre>
         * 
         * @param m
         *            {@link Repository} Bean이 제공하는 메소드
         * @param offset
         *            데이터 조회 커서 위치. (exclusive)
         * @param count
         *            데이터 조회 최대 개수.
         * @param columns
         *            TODO
         * @param p
         *            해당 함수 파라미터.
         * @since 2020. 1. 21.
         * @version
         */
        public AsyncSelectorBy(@NotNull HexaFunction<String, Integer, Integer, Class<E>, Supplier<Object[]>, Supplier<String[]>, Result<List<E>>> m //
                , String query, Integer offset, Integer count, @NotNull Class<E> type, Supplier<Object[]> params, Supplier<String[]> columns) {
            this.m = m;
            this.query = query;
            this.offset = offset;
            this.count = count;
            this.type = type;
            this.params = params;
            this.columns = columns;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Result<List<E>> call() throws Exception {
            return m.apply(query, offset, count, type, params, columns);
        }
    }

}
