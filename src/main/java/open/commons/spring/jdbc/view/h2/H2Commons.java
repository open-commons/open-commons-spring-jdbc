/*
 * Copyright 2025 Park Jun-Hong (parkjunhong77@gmail.com)
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
 * Date  : 2025. 5. 13. 오후 5:35:09
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.view.h2;

import java.util.Set;

import open.commons.spring.jdbc.utils.CommonUtils;

/**
 * H2 DBMS 공통 데이터 모음.
 * 
 * @since 2025. 4. 2.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class H2Commons {

    /**
     * 예약어 목록 문자열
     * 
     * @since 2025. 4. 2.
     */
    public static final String RESERVED_KEYWORD_STRING = //
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

    public static final Set<String> RESERVED_KEYWORDS = CommonUtils.loadReservedKeywords(H2Commons.RESERVED_KEYWORD_STRING);

    /**
     * 예약어 감싸는 문자
     * 
     * @since 2025. 4. 2.
     */
    public static final CharSequence RESERVED_KEYWORDS_WRAPPING_CHARACTER = "\"";
    public static final String QUERY_FOR_OFFSET = "LIMIT ?, ?";

    private H2Commons() {
    }
}
