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
 * Date  : 2021. 11. 26. 오후 12:07:30
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import open.commons.core.Result;
import open.commons.core.annotation.ColumnValue;

/**
 * DBMS Table 모델에 기반하여 단순한 CRUD 수준의 기능을 정의.
 * 
 * @param <T>
 * @since 2021. 11. 26.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
public interface IGenericRepository<T> {

    /**
     * 전체 데이터 개수를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 28.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 12. 28.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public Result<Integer> countAll();

    /**
     * 주어진 검색조건에 맞는 데이터 개수를 제공합니다. <br>
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
    public Result<Integer> countBy(@NotNull Map<String, Object> clmnParams);

    /**
     * DB Table Entity 타입을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 28.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */

    public Class<T> getEntityType();

    /**
     * 테이블 이름을 제공합니다. <br>
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
    public String getTableName();

    /**
     * 여러 개의 데이터를 추가합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @param data
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public Result<Integer> insert(List<T> data);

    /**
     * 여러 개의 데이터를 추가합니다. <br>
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
     *            한번에 저장할 데이터 개수.
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public Result<Integer> insert(List<T> data, @Min(1) int partitionSize);

    /**
     * 데이터를 추가합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @param data
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    public Result<Integer> insert(T data);

    /**
     * 모든 데이터를 제공합니다. <br>
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
    public Result<List<T>> selectAll();

    /**
     * 여러 개의 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @param offset
     *            데이터 시작 위치. ( '0'부터 시작)
     * @param limit
     *            데이터 개수.
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public Result<List<T>> selectAll(@Min(0) int offset, @Min(1) int limit);

    /**
     * 주어진 조건에 따라 정렬된 모든 데이터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 9.		박준홍			최초 작성
     * </pre>
     *
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
     * @since 2021. 12. 9.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public Result<List<T>> selectAll(@Min(0) int offset, @Min(1) int limit, String... orderByArgs);

    /**
     * 주어진 조건에 따라 정렬된 모든 데이터를 제공합니다. <br>
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
    public Result<List<T>> selectAll(String... orderByArgs);

    /**
     * 주어진 조건에 맞는 데이터를 제공합니다.<br>
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
     * @since 2022. 2. 11.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public Result<List<T>> selectBy(@NotNull Map<String, Object> clmnParams, int offset, int limit, String... orderByArgs);

    /**
     * 주어진 조건에 맞는 데이터를 제공합니다.<br>
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
     * @param orderByArgs
     *            정렬 기준.<br>
     *            <b>데이터 정의</b><br>
     *            <li>포맷: {column} {direction}<br>
     *            <li>예: name asc
     * @return
     *
     * @since 2022. 2. 11.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public Result<List<T>> selectBy(@NotNull Map<String, Object> clmnParams, String... orderByArgs);

}
