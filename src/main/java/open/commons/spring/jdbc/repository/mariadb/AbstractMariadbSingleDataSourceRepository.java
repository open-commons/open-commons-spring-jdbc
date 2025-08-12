/*
 * Copyright 2021 Park Jun-Hong (parkjunhong77@gmail.com)
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
 * Date  : 2021. 12. 24. 오후 1:10:57
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository.mariadb;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository;
import open.commons.spring.jdbc.view.mariadb.MariadbCommons;

/**
 * Mariadb 연동을 위한 클래스.
 * 
 * @since 2021. 12. 24.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
public abstract class AbstractMariadbSingleDataSourceRepository<T> extends AbstractSingleDataSourceRepository<T> {

    /**
     * 'INSERT IGNORE' 기본 쿼리
     * 
     * @version 0.4.0
     * @since 2022. 11. 2.
     */
    protected final String QUERY_FOR_INSERT_IGNORE;

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 24.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     *
     * @since 2021. 12. 24.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractMariadbSingleDataSourceRepository(@NotNull Class<T> entityType) {
        this(entityType, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 24.    박준홍     최초 작성
     * 2022. 11. 2.     박준홍     'INSERT IGNORE' 추가.
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper Class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     *
     * @since 2021. 12. 24.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractMariadbSingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        this(entityType, forceToPrimitive, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2023. 8. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     * @param ignoreNoDataMethod
     *            데이터를 제공하는 메소드가 없는 경우 로그 미발생 여부
     *
     * @since 2023. 8. 28.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractMariadbSingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive, boolean ignoreNoDataMethod) {
        super(entityType, forceToPrimitive, ignoreNoDataMethod);

        this.QUERY_FOR_INSERT_IGNORE = new StringBuffer() //
                .append("INSERT IGNORE INTO") //
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
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createQueryForInsertOrNothing(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected String createQueryForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs) {
        return this.QUERY_FOR_INSERT_IGNORE;
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 설정된 데이터를 갱신합니다. <br>
     * 
     * <pre>
     * INSERT [LOW_PRIORITY | HIGH_PRIORITY] [IGNORE]
     *     [INTO] tbl_name [PARTITION (partition_list)] [(col,...)]
     *     SELECT ...
     *     [ ON DUPLICATE KEY UPDATE
     *       col=expr
     *         [, col=expr] ... ]
     * </pre>
     * 
     * <pre>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 7. 14.     박준홍         최초 작성
     * 2022. 11. 29.    박준홍     메소드 이관.
     * </pre>
     * 
     * 
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createQueryForInsertOrUpdate(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected String createQueryForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs) {
        // #1. 데이터 변경 쿼리 생성
        String updatePart = String.join(",", //
                getUpdatableColumnNames().stream() // 업데이트 가능한 컬럼 도출
                        .map(clmn -> String.format("%s = VALUES(%s)", clmn, clmn)) // 컬럼별 갱신 쿼리
                        .collect(Collectors.toList()) //
        );

        StringBuilder queryBuf = new StringBuilder();
        queryBuf.append(QUERY_FOR_INSERT);

        if (!updatePart.isEmpty()) {
            queryBuf.append(" ");
            queryBuf.append("ON DUPLICATE KEY UPDATE");
            queryBuf.append(" ");
            queryBuf.append(updatePart);
        }

        logger.debug("query={}, data={}", queryBuf.toString(), data);

        return queryBuf.toString();
    }

    /**
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.view.AbstractGenericView#getReservedKeywords()
     */
    @Override
    protected Set<String> getReservedKeywords() {
        return MariadbCommons.RESERVED_KEYWORDS;
    }

    /**
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.view.AbstractGenericView#getReservedKeywordWrappingCharacter()
     */
    @Override
    protected CharSequence getReservedKeywordWrappingCharacter() {
        return MariadbCommons.RESERVED_KEYWORDS_WRAPPING_CHARACTER;
    }

    /**
     *
     * @since 2021. 12. 24.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.view.AbstractGenericView#queryForOffset(int, int)
     */
    @Override
    protected String queryForOffset(@Min(0) int offset, @Min(1) int limit) {
        return MariadbCommons.QUERY_FOR_OFFSET;
    }

    /**
     *
     * @since 2021. 12. 24.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionConcatValue()
     */
    @Override
    protected String queryForPartitionConcatValue() {
        return ",";
    }

    /**
     *
     * @since 2021. 12. 24.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionHeader()
     */
    @Override
    protected String queryForPartitionHeader() {
        List<String> columns = validateColumnNames(getColumnNames());

        return new StringBuffer() //
                .append("INSERT INTO") //
                .append(" ") //
                .append(this.tableName) //
                .append(" (")//
                .append(String.join(", ", columns)) //
                .append(") ") //
                .append("VALUES") //
                .toString();
    }

    /**
     *
     * @since 2021. 12. 24.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionTail()
     */
    @Override
    protected String queryForPartitionTail() {
        return "";
    }

    /**
     *
     * @since 2021. 12. 24.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionValue()
     */
    @Override
    protected String queryForPartitionValue() {
        return new StringBuilder().append("( ").append(queryForVariableBinding()).append(" )").toString();
    }
}
