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
 * Date  : 2021. 11. 26. 오후 5:02:26
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository.postgresql;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import open.commons.core.annotation.ColumnValue;
import open.commons.core.utils.AnnotationUtils;
import open.commons.core.utils.SQLUtils;
import open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository;

/**
 * PostgreSQL 연동을 위한 클래스.
 * 
 * @since 2021. 11. 26.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
public abstract class AbstractPostgreSingleDataSourceRepository<T> extends AbstractSingleDataSourceRepository<T> {

    /**
     * 예약어 목록 문자열
     * 
     * @since 2025. 4. 2.
     */
    protected static final String RESERVED_KEYWORD_STRING = //
            "ALL, ANALYSE, ANALYZE, AND, ANY, ARRAY, AS, ASC, ASYMMETRIC, AUTHORIZATION, " //
                    + "BINARY, BOTH, CASE, CAST, CHECK, COLLATE, COLUMN, CONSTRAINT, CREATE, " //
                    + "CURRENT_CATALOG, CURRENT_DATE, CURRENT_ROLE, CURRENT_TIME, " //
                    + "CURRENT_TIMESTAMP, CURRENT_USER, DEFAULT, DEFERRABLE, DESC, DISTINCT, DO, " //
                    + "ELSE, END, EXCEPT, FALSE, FETCH, FOR, FOREIGN, FROM, GRANT, GROUP, HAVING, " //
                    + "IN, INITIALLY, INTERSECT, INTO, LATERAL, LEADING, LIMIT, LOCALTIME, " //
                    + "LOCALTIMESTAMP, NOT, NULL, OFFSET, ON, ONLY, OR, ORDER, PLACING, PRIMARY, " //
                    + "REFERENCES, RETURNING, SELECT, SESSION_USER, SOME, SYMMETRIC, TABLE, THEN, " //
                    + "TO, TRAILING, TRUE, UNION, UNIQUE, USER, USING, VARIADIC, WHEN, WHERE, WINDOW, WITH" //
    ;
    /**
     * 예약어 목록
     * 
     * @since 2025. 4. 2.
     */
    protected static final Set<String> RESERVED_KEYWORDS = loadReservedKeywords(RESERVED_KEYWORD_STRING);
    /**
     * 예약어 감싸는 문자
     * 
     * @since 2025. 4. 2.
     */
    protected static final CharSequence RESERVED_KEYWORDS_WRAPPING_CHARACTER = "\"";

    protected static final String QUERY_FOR_OFFSET = "OFFSET ? LIMIT ?";

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
     *
     * @since 2021. 12. 6.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #AbstractPostgreSingleDataSourceRepository(Class, boolean)
     */
    public AbstractPostgreSingleDataSourceRepository(@NotNull Class<T> entityType) {
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
     *            Wrapper Class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractPostgreSingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        this(entityType, forceToPrimitive, true);
    }

    /**
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2023. 8. 28.     박준홍         최초 작성
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
    public AbstractPostgreSingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive, boolean ignoreNoDataMethod) {
        super(entityType, forceToPrimitive, ignoreNoDataMethod);
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 아무런 동작을 하지 않습니다.<br>
     * 
     * 참고: https://www.postgresql.org/docs/&lt;versoin&gt;/sql-insert.html<br>
     * Supported Versions: 15 / 14 / 13 / 12 / 11 / 10
     * 
     * <pre>
     * [ WITH [ RECURSIVE ] with_query [, ...] ]
     * INSERT INTO table_name [ AS alias ] [ ( column_name [, ...] ) ]
     *     [ OVERRIDING { SYSTEM | USER } VALUE ]
     *     { DEFAULT VALUES | VALUES ( { expression | DEFAULT } [, ...] ) [, ...] | query }
     *     [ ON CONFLICT [ conflict_target ] conflict_action ]
     *     [ RETURNING * | output_expression [ [ AS ] output_name ] [, ...] ]
     * </pre>
     * 
     * where conflict_target can be one of:
     * 
     * <pre>
     *     ( { index_column_name | ( index_expression ) } [ COLLATE collation ] [ opclass ] [, ...] ) [ WHERE index_predicate ]
     *     ON CONSTRAINT constraint_name
     * </pre>
     * 
     * and conflict_action is one of:
     * 
     * <pre>
     *     DO NOTHING
     *     DO UPDATE SET { column_name = { expression | DEFAULT } |
     *                     ( column_name [, ...] ) = [ ROW ] ( { expression | DEFAULT } [, ...] ) |
     *                     ( column_name [, ...] ) = ( sub-SELECT )
     *                   } [, ...]
     *               [ WHERE condition ]
     * </pre>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 2.     박준홍         최초 작성
     * 2022. 11. 29.    박준홍         메소드 이관.
     * </pre>
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
        // #1. 데이터 변경 쿼리 생성
        List<String> updateClmns = getUpdatableColumnNames().stream() // 업데이트 가능한 컬럼 도출
                .collect(Collectors.toList()) //
        ;

        StringBuilder queryBuf = new StringBuilder();
        queryBuf.append(QUERY_FOR_INSERT);

        if (!updateClmns.isEmpty()) {
            // 'CONFLICT' 여부 확인
            List<String> primaryKeys = AnnotationUtils.getAnnotatedMethodsAllAsStream(data.getClass(), ColumnValue.class) //
                    .filter(m -> m.getAnnotation(ColumnValue.class).primaryKey()) //
                    .map(m -> SQLUtils.getColumnName(m)) //
                    .map(cn -> validateColumnName(cn)) //
                    .collect(Collectors.toList()) //
            ;

            if (!primaryKeys.isEmpty()) {
                queryBuf.append(" ");
                queryBuf.append("ON CONFLICT (");
                queryBuf.append(String.join(",", primaryKeys));
                queryBuf.append(") ");
                queryBuf.append("DO NOTHING");
            }
        }

        return queryBuf.toString();
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 설정된 데이터를 갱신합니다. <br>
     * 
     * 참고: https://www.postgresql.org/docs/&lt;versoin&gt;/sql-insert.html<br>
     * Supported Versions: 15 / 14 / 13 / 12 / 11 / 10
     * 
     * <pre>
     * [ WITH [ RECURSIVE ] with_query [, ...] ]
     * INSERT INTO table_name [ AS alias ] [ ( column_name [, ...] ) ]
     *     [ OVERRIDING { SYSTEM | USER } VALUE ]
     *     { DEFAULT VALUES | VALUES ( { expression | DEFAULT } [, ...] ) [, ...] | query }
     *     [ ON CONFLICT [ conflict_target ] conflict_action ]
     *     [ RETURNING * | output_expression [ [ AS ] output_name ] [, ...] ]
     * </pre>
     * 
     * where conflict_target can be one of:
     * 
     * <pre>
     *     ( { index_column_name | ( index_expression ) } [ COLLATE collation ] [ opclass ] [, ...] ) [ WHERE index_predicate ]
     *     ON CONSTRAINT constraint_name
     * </pre>
     * 
     * and conflict_action is one of:
     * 
     * <pre>
     *     DO NOTHING
     *     DO UPDATE SET { column_name = { expression | DEFAULT } |
     *                     ( column_name [, ...] ) = [ ROW ] ( { expression | DEFAULT } [, ...] ) |
     *                     ( column_name [, ...] ) = ( sub-SELECT )
     *                   } [, ...]
     *               [ WHERE condition ]
     * </pre>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 7. 14.     박준홍         최초 작성
     * 2022. 11. 1.     박준홍         실제 구현
     * 2022. 11. 29.    박준홍         메소드 이관
     * </pre>
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
        List<String> updateClmns = getUpdatableColumnNames().stream() // 업데이트 가능한 컬럼 도출
                .collect(Collectors.toList()) //
        ;

        StringBuilder queryBuf = new StringBuilder();
        queryBuf.append(QUERY_FOR_INSERT);

        if (!updateClmns.isEmpty()) {
            // 'CONFLICT' 여부 확인
            List<String> primaryKeys = AnnotationUtils.getAnnotatedMethodsAllAsStream(data.getClass(), ColumnValue.class) //
                    .filter(m -> m.getAnnotation(ColumnValue.class).primaryKey()) //
                    .map(m -> SQLUtils.getColumnName(m)) //
                    .map(cn -> validateColumnName(cn)) //
                    .collect(Collectors.toList()) //
            ;

            if (!primaryKeys.isEmpty()) {
                queryBuf.append(" ");
                queryBuf.append("ON CONFLICT (");
                queryBuf.append(String.join(",", primaryKeys));
                queryBuf.append(") ");
                queryBuf.append("DO UPDATE SET (");
                queryBuf.append(String.join(",", updateClmns));
                queryBuf.append(") = ROW(");
                queryBuf.append(String.join(",", updateClmns.stream().map(clmn -> String.join(".", "excluded", clmn)).collect(Collectors.toList())));
                queryBuf.append(")");
            }
        }

        return queryBuf.toString();
    }

    /**
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#getReservedKeywords()
     */
    @Override
    protected Set<String> getReservedKeywords() {
        return RESERVED_KEYWORDS;
    }

    /**
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#getReservedKeywordWrappingCharacter()
     */
    @Override
    protected CharSequence getReservedKeywordWrappingCharacter() {
        return RESERVED_KEYWORDS_WRAPPING_CHARACTER;
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForOffset(int, int)
     */
    @Override
    protected String queryForOffset(int offset, int limit) {
        return QUERY_FOR_OFFSET;
    }

    /**
     *
     * @since 2021. 11. 26.
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
     * @since 2021. 11. 26.
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
     * @since 2021. 11. 26.
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
     * @since 2021. 11. 26.
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
