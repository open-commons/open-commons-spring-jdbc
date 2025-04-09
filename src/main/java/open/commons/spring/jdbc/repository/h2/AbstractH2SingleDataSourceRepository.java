/*
 * Copyright 2025 Park Jun-Hong_(parkjunhong77@gmail.com)
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
 * Date  : 2025. 4. 1. 오후 4:24:34
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository.h2;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import open.commons.core.text.NamedTemplate;
import open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository;

/**
 * H2 DB 연동을 위한 클래스.
 * 
 * @since 2025. 4. 1.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public abstract class AbstractH2SingleDataSourceRepository<T> extends AbstractSingleDataSourceRepository<T> {

    /**
     * 예약어 목록 문자열
     * 
     * @since 2025. 4. 2.
     */
    protected static final String RESERVED_KEYWORD_STRING = //
            "ALL, ALTER, AND, ANY, ARRAY, AS, ASYMMETRIC, AUTHORIZATION, BETWEEN, BIGINT," //
                    + "BINARY, BOOLEAN, BOTH, BY, CALL, CALLED, CASE, CAST, CHAR, CHARACTER," //
                    + "CHECK, CLOB, CLOSE, COLLATE, COLUMN, COMMIT, CONDITION, CONNECT, CONSTRAINT," //
                    + "CONTINUE, CONVERT, CORRESPONDING, CREATE, CROSS, CUBE, CURRENT, CURRENT_DATE," //
                    + "CURRENT_PATH, CURRENT_ROLE, CURRENT_SCHEMA, CURRENT_TIME, CURRENT_TIMESTAMP," //
                    + "CURRENT_USER, CURSOR, CYCLE, DATE, DAY, DEALLOCATE, DEC, DECIMAL, DECLARE," //
                    + "DEFAULT, DELETE, DENSE_RANK, DEREF, DESCRIBE, DETERMINISTIC, DISTINCT, DOUBLE," //
                    + "DROP, DYNAMIC, ELSE, END, ESCAPE, EXCEPT, EXEC, EXECUTE, EXISTS, EXTERNAL," //
                    + "EXTRACT, FALSE, FETCH, FIRST, FLOAT, FOR, FOREIGN, FREE, FROM, FULL, FUNCTION," //
                    + "GET, GRANT, GROUP, GROUPING, HAVING, HOLD, HOUR, IDENTITY, IF, IMMEDIATE," //
                    + "IN, INDICATOR, INNER, INOUT, INSENSITIVE, INSERT, INT, INTEGER, INTERSECT," //
                    + "INTERVAL, INTO, IS, JOIN, LANGUAGE, LARGE, LATERAL, LEADING, LEFT, LIKE," //
                    + "LOCAL, LOCALTIME, LOCALTIMESTAMP, MATCH, MEMBER, MERGE, METHOD, MINUTE," //
                    + "MODIFIES, MODULE, MONTH, MULTISET, NATIONAL, NATURAL, NCHAR, NCLOB, NEW," //
                    + "NO, NONE, NOT, NULL, NULLIF, NUMERIC, OF, OLD, ON, ONLY, OPEN, OR, ORDER," //
                    + "OUT, OUTER, OVER, OVERLAPS, PARAMETER, PARTITION, PRECISION, PREPARE," //
                    + "PRIMARY, PROCEDURE, RANGE, RANK, READS, REAL, RECURSIVE, REF, REFERENCES," //
                    + "REFERENCING, REGR_AVGX, REGR_AVGY, REGR_COUNT, REGR_INTERCEPT, REGR_R2," //
                    + "REGR_SLOPE, REGR_SXX, REGR_SXY, REGR_SYY, RELEASE, RESULT, RETURN," //
                    + "RETURNS, REVOKE, RIGHT, ROLLBACK, ROLLUP, ROW, ROW_NUMBER, ROWS, SAVEPOINT," //
                    + "SCOPE, SCROLL, SEARCH, SECOND, SELECT, SENSITIVE, SESSION_USER, SET, SIMILAR," //
                    + "SMALLINT, SOME, SPECIFIC, SPECIFICTYPE, SQL, SQLEXCEPTION, SQLSTATE," //
                    + "SQLWARNING, START, STATIC, SUBMULTISET, SYMMETRIC, SYSTEM, SYSTEM_USER," //
                    + "TABLE, TABLESAMPLE, THEN, TIME, TIMESTAMP, TIMEZONE_HOUR, TIMEZONE_MINUTE," //
                    + "TO, TRAILING, TRANSLATION, TREAT, TRIGGER, TRUE, UESCAPE, UNION, UNIQUE," //
                    + "UNKNOWN, UNNEST, UPDATE, USER, USING, VALUE, VALUES, VARCHAR, VARYING," //
                    + "WHEN, WHENEVER, WHERE, WHILE, WINDOW, WITH, WITHIN, WITHOUT, YEAR" //
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
    protected static final String QUERY_FOR_OFFSET = "LIMIT ?, ?";

    /** 테이블 이름 */
    protected static final String TN_TABLE_NAME = "TABLE_NAME";
    /** 'Select' 데이터 할당(binding) 쿼리 */
    protected static final String TN_DATA_BINDING_QUERY = "DATA_BINDING_QUERY";
    /** PK 또는 Unique Key 비교 구문 */
    protected static final String TN_USING_ON_COMPARE_CLAUSE = "USING_ON_COMPARE_CLAUSE";
    /** 데이터 갱신 구문 */
    protected static final String TN_UPDATE_SET_CLAUSE = "UPDATE_SET_CLAUSE";
    /** 컬럼 설정 구문 */
    protected static final String TN_INSERT_COLUMN_CLAUSE = "INSERT_COLUMN_CLAUSE";
    /** 'Insert' 데이터 할당(binding) 쿼리 */
    protected static final String TN_INSERT_VALUE_BINDING_CLAUSE = "INSERT_VALUE_BINDING_CLAUSE";
    /** 테이블명 alias */
    protected static final String TN_TABLE_ALIAS = "TABLE_ALIAS";
    /** 신규 데이터 가상 테이블 alais */
    protected static final String TN_DATA_ALIAS = "DATA_ALIAS";
    /**
     * 아래 지시자를 동적으로 생성해야 함.
     * 
     * <pre>
     * - {@link #TN_TABLE_NAME}: {@value #TN_TABLE_NAME}
     * - {@link #TN_TABLE_ALIAS}: {@value #TN_TABLE_ALIAS}
     * - {@link #TN_DATA_BINDING_QUERY}: {@value #TN_DATA_BINDING_QUERY}
     * - {@link #TN_DATA_ALIAS}: {@value #TN_DATA_ALIAS}
     * - {@link #TN_USING_ON_COMPARE_CLAUSE}: {@value #TN_USING_ON_COMPARE_CLAUSE}
     * - {@link #TN_INSERT_COLUMN_CLAUSE}: {@value #TN_INSERT_COLUMN_CLAUSE}
     * - {@link #TN_INSERT_VALUE_BINDING_CLAUSE}: {@value #TN_INSERT_VALUE_BINDING_CLAUSE}
     * </pre>
     */
    protected static final String QUERY_TPL_INSERT_OR_NOTHING = new StringBuilder() //
            .append("MERGE INTO {").append(TN_TABLE_NAME).append("} AS {").append(TN_TABLE_ALIAS).append("} ") //
            .append("USING ( ") //
            .append("  SELECT {").append(TN_DATA_BINDING_QUERY).append("} ") //
            .append(") AS {").append(TN_DATA_ALIAS).append("} ") //
            .append("ON ( {").append(TN_USING_ON_COMPARE_CLAUSE).append("} ) ") //
            .append("WHEN NOT MATCHED THEN") //
            .append("  INSERT ( {").append(TN_INSERT_COLUMN_CLAUSE).append("} ) ") //
            .append("  VALUES ( {").append(TN_INSERT_VALUE_BINDING_CLAUSE).append("} ) ") //
            .toString();
    /**
     * 아래 지시자를 동적으로 생성해야 함.
     * 
     * <pre>
     * - {@link #TN_TABLE_NAME}: {@value #TN_TABLE_NAME}
     * - {@link #TN_TABLE_ALIAS}: {@value #TN_TABLE_ALIAS}
     * - {@link #TN_DATA_BINDING_QUERY}: {@value #TN_DATA_BINDING_QUERY}
     * - {@link #TN_DATA_ALIAS}: {@value #TN_DATA_ALIAS}
     * - {@link #TN_USING_ON_COMPARE_CLAUSE}: {@value #TN_USING_ON_COMPARE_CLAUSE}
     * - {@link #TN_UPDATE_SET_CLAUSE}: {@value #TN_UPDATE_SET_CLAUSE}
     * - {@link #TN_INSERT_COLUMN_CLAUSE}: {@value #TN_INSERT_COLUMN_CLAUSE}
     * - {@link #TN_INSERT_VALUE_BINDING_CLAUSE}: {@value #TN_INSERT_VALUE_BINDING_CLAUSE}
     * </pre>
     */
    protected static final String QUERY_TPL_INSERT_OR_UPDATE = new StringBuilder() //
            .append("MERGE INTO {").append(TN_TABLE_NAME).append("} AS {").append(TN_TABLE_ALIAS).append("} ") //
            .append("USING ( ") //
            .append("  SELECT {").append(TN_DATA_BINDING_QUERY).append("} ") //
            .append(") AS {").append(TN_DATA_ALIAS).append("} ") //
            .append("ON ( {").append(TN_USING_ON_COMPARE_CLAUSE).append("} ) ") //
            .append("WHEN MATCHED THEN") //
            .append("  UPDATE SET {").append(TN_UPDATE_SET_CLAUSE).append("} ") //
            .append("WHEN NOT MATCHED THEN") //
            .append("  INSERT ( {").append(TN_INSERT_COLUMN_CLAUSE).append("} ) ") //
            .append("  VALUES ( {").append(TN_INSERT_VALUE_BINDING_CLAUSE).append("} ) ") //
            .toString();

    protected final String QUERY_FOR_INSERT_OR_NOTHING;
    protected final String QUERY_FOR_INSERT_OR_UPDATE;

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 1.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2SingleDataSourceRepository(@NotNull Class<T> entityType) {
        this(entityType, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 1.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper Class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2SingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        this(entityType, forceToPrimitive, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 1.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     * @param ignoreNoDataMethod
     *            데이터를 제공하는 메소드가 없는 경우 로그 미발생 여부
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2SingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive, boolean ignoreNoDataMethod) {
        super(entityType, forceToPrimitive, ignoreNoDataMethod);

        this.QUERY_FOR_INSERT_OR_NOTHING = createQueryForInsertOrNothing(null, null, (Object[]) null);
        this.QUERY_FOR_INSERT_OR_UPDATE = createQueryForInsertOrUpdate(null, null, (Object[]) null);
    }

    /**
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createParametersForInsertOrNothing(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected Object createParametersForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs) {
        // #1. 'SELECT' 파라미터
        List<String> clmnSelect = getColumnNames();
        return getColumnValues(data, clmnSelect);
    }

    /**
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createParametersForInsertOrUpdate(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected Object createParametersForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs) {
        return super.createParametersForInsertOrUpdate(data, method, whereArgs);
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createQueryForInsertOrNothing(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected String createQueryForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs) {
        if (this.QUERY_FOR_INSERT_OR_NOTHING != null) {
            return this.QUERY_FOR_INSERT_OR_NOTHING;
        } else {
            // Primary Key 컬럼 확인
            List<String> pkColumns = validateColumnNames(getPrimaryKeyColumns());
            if (pkColumns == null || pkColumns.size() < 1) {
                logger.warn("'{}' 테이블에 Primary Key가 설정되지 않았습니다.", this.tableName);
                return queryForInsert();
            } else {
                /**
                 * <pre>
                 * - TN_TABLE_NAME: "TABLE_NAME"
                 * - TN_TABLE_ALIAS: "TABLE_ALIAS"
                 * - TN_DATA_BINDING_QUERY: "DATA_BINDING_QUERY"
                 * - TN_DATA_ALIAS: "DATA_ALIAS"
                 * - TN_USING_ON_COMPARE_CLAUSE: "USING_ON_COMPARE_CLAUSE"
                 * - TN_UPDATE_SET_CLAUSE: "UPDATE_SET_CLAUSE"
                 * - TN_INSERT_COLUMN_CLAUSE: "INSERT_COLUMN_CLAUSE"
                 * - TN_INSERT_VALUE_BINDING_CLAUSE: "INSERT_VALUE_BINDING_CLAUSE"
                 * </pre>
                 */
                final String ALIAS_TABLE = "tbl";
                final String ALIAS_DATA = "data";

                // #1. 쿼리 구문 선언
                NamedTemplate queryTpl = new NamedTemplate(QUERY_TPL_INSERT_OR_NOTHING);
                // #2. 구문 설정
                queryTpl.addValue(TN_TABLE_NAME, getTableName());
                queryTpl.addValue(TN_TABLE_ALIAS, ALIAS_TABLE);
                queryTpl.addValue(TN_DATA_BINDING_QUERY, queryForVariableBindingAliasingColumnName(true));
                queryTpl.addValue(TN_DATA_ALIAS, ALIAS_DATA);
                queryTpl.addValue(TN_USING_ON_COMPARE_CLAUSE, createMergeUsingOnClause(pkColumns, ALIAS_TABLE, ALIAS_DATA));
                queryTpl.addValue(TN_INSERT_COLUMN_CLAUSE, queryForColumnNames());
                queryTpl.addValue(TN_INSERT_VALUE_BINDING_CLAUSE, queryForColumnNames(ALIAS_DATA));

                return queryTpl.format();
            }
        }
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createQueryForInsertOrUpdate(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected String createQueryForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs) {
        if (this.QUERY_FOR_INSERT_OR_UPDATE != null) {
            return this.QUERY_FOR_INSERT_OR_UPDATE;
        } else {
            // Primary Key 컬럼 확인
            List<String> pkColumns = validateColumnNames(getPrimaryKeyColumns());
            if (pkColumns == null || pkColumns.size() < 1) {
                logger.warn("'{}' 테이블에 Primary Key가 설정되지 않았습니다.", this.tableName);
                return queryForInsert();
            } else {
                /**
                 * <pre>
                 * - TN_TABLE_NAME: "TABLE_NAME"
                 * - TN_TABLE_ALIAS: "tbl"
                 * - TN_DATA_BINDING_QUERY: "DATA_BINDING_QUERY"
                 * - TN_DATA_ALIAS: "src"
                 * - TN_USING_ON_COMPARE_CLAUSE: "USING_ON_COMPARE_CLAUSE"
                 * - TN_UPDATE_SET_CLAUSE}: TN_UPDATE_SET_CLAUSE}
                 * - TN_INSERT_COLUMN_CLAUSE: "INSERT_COLUMN_CLAUSE"
                 * - TN_INSERT_VALUE_BINDING_CLAUSE: "INSERT_VALUE_BINDING_CLAUSE"
                 * </pre>
                 */
                final String ALIAS_TABLE = "tbl";
                final String ALIAS_DATA = "data";

                // #1. 쿼리 구문 선언
                NamedTemplate queryTpl = new NamedTemplate(QUERY_TPL_INSERT_OR_UPDATE);
                // #2. 구문 설정
                queryTpl.addValue(TN_TABLE_NAME, getTableName());
                queryTpl.addValue(TN_TABLE_ALIAS, ALIAS_TABLE);
                queryTpl.addValue(TN_DATA_BINDING_QUERY, queryForVariableBindingAliasingColumnName(true));
                queryTpl.addValue(TN_DATA_ALIAS, ALIAS_DATA);
                queryTpl.addValue(TN_USING_ON_COMPARE_CLAUSE, createMergeUsingOnClause(pkColumns, ALIAS_TABLE, ALIAS_DATA));
                queryTpl.addValue(TN_UPDATE_SET_CLAUSE, createMergeUpdateSetClause(validateColumnNames(getUpdatableColumnNames()), ALIAS_TABLE, ALIAS_DATA));
                queryTpl.addValue(TN_INSERT_COLUMN_CLAUSE, queryForColumnNames());
                queryTpl.addValue(TN_INSERT_VALUE_BINDING_CLAUSE, queryForColumnNames(ALIAS_DATA));

                return queryTpl.format();
            }
        }
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
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForOffset(int, int)
     */
    @Override
    protected String queryForOffset(@Min(0) int offset, @Min(1) int limit) {
        return QUERY_FOR_OFFSET;
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
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
     * @since 2025. 4. 1.
     * @version 0.5.0
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
     * @since 2025. 4. 1.
     * @version 0.5.0
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
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionValue()
     */
    @Override
    protected String queryForPartitionValue() {
        return new StringBuilder().append("( ").append(queryForVariableBinding()).append(" )").toString();
    }
}
