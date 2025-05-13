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
 * Date  : 2025. 5. 13. 오후 5:45:41
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.view.mariadb;

import java.util.Set;

import open.commons.spring.jdbc.utils.CommonUtils;

/**
 * 
 * @since 2025. 5. 13.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class MariadbCommons {

    /**
     * 예약어 목록 문자열
     * 
     * @since 2025. 4. 2.
     */
    public static final String RESERVED_KEYWORD_STRING = //
            "ACCESSIBLE, ADD, ALL, ALTER, ANALYZE, AND, AS, ASC, ASENSITIVE, " //
                    + "BEFORE, BETWEEN, BIGINT, BINARY, BLOB, BOTH, BY, CALL, CASCADE, " //
                    + "CASE, CHANGE, CHAR, CHARACTER, CHECK, COLLATE, COLUMN, CONDITION, " //
                    + "CONSTRAINT, CONTINUE, CONVERT, CREATE, CROSS, CURRENT_DATE, " //
                    + "CURRENT_TIME, CURRENT_TIMESTAMP, CURRENT_USER, CURSOR, DATABASE, " //
                    + "DATABASES, DAY_HOUR, DAY_MICROSECOND, DAY_MINUTE, DAY_SECOND, " //
                    + "DEC, DECIMAL, DECLARE, DEFAULT, DELAYED, DELETE, DESC, DESCRIBE, " //
                    + "DETERMINISTIC, DISTINCT, DISTINCTROW, DIV, DOUBLE, DROP, DUAL, " //
                    + "EACH, ELSE, ELSEIF, ENCLOSED, ESCAPED, EXISTS, EXIT, EXPLAIN, " //
                    + "FALSE, FETCH, FLOAT, FLOAT4, FLOAT8, FOR, FORCE, FOREIGN, FROM, " //
                    + "FULLTEXT, GENERATED, GET, GRANT, GROUP, HAVING, HIGH_PRIORITY, " //
                    + "HOUR_MICROSECOND, HOUR_MINUTE, HOUR_SECOND, IF, IGNORE, IN, " //
                    + "INDEX, INFILE, INNER, INOUT, INSENSITIVE, INSERT, INT, INT1, " //
                    + "INT2, INT3, INT4, INT8, INTEGER, INTERVAL, INTO, IO_AFTER_GTIDS, " //
                    + "IO_BEFORE_GTIDS, IS, ITERATE, JOIN, KEY, KEYS, KILL, LEADING, " //
                    + "LEAVE, LEFT, LIKE, LIMIT, LINEAR, LINES, LOAD, LOCALTIME, LOCALTIMESTAMP, " //
                    + "LOCK, LONG, LONGBLOB, LONGTEXT, LOOP, LOW_PRIORITY, MASTER_BIND, " //
                    + "MASTER_SSL_VERIFY_SERVER_CERT, MATCH, MAXVALUE, MEDIUMBLOB, MEDIUMINT, " //
                    + "MEDIUMTEXT, MIDDLEINT, MINUTE_MICROSECOND, MINUTE_SECOND, MOD, MODIFIES, " //
                    + "NATURAL, NOT, NO_WRITE_TO_BINLOG, NULL, NUMERIC, ON, OPTIMIZE, OPTION, " //
                    + "OPTIONALLY, OR, ORDER, OUT, OUTER, OUTFILE, PARTITION, PRECISION, PRIMARY, " //
                    + "PROCEDURE, PURGE, RANGE, READ, READS, READ_WRITE, REAL, REFERENCES, REGEXP, " //
                    + "RELEASE, RENAME, REPEAT, REPLACE, REQUIRE, RESIGNAL, RESTRICT, RETURN, " //
                    + "REVOKE, RIGHT, RLIKE, SCHEMA, SCHEMAS, SECOND_MICROSECOND, SELECT, SENSITIVE, " //
                    + "SEPARATOR, SET, SHOW, SIGNAL, SMALLINT, SPATIAL, SPECIFIC, SQL, SQLEXCEPTION, " //
                    + "SQLSTATE, SQLWARNING, SQL_BIG_RESULT, SQL_CALC_FOUND_ROWS, SQL_SMALL_RESULT, " //
                    + "SSL, STARTING, STORED, STRAIGHT_JOIN, TABLE, TERMINATED, THEN, TINYBLOB, " //
                    + "TINYINT, TINYTEXT, TO, TRAILING, TRIGGER, TRUE, UNDO, UNION, UNIQUE, UNLOCK, " //
                    + "UNSIGNED, UPDATE, USAGE, USE, USING, UTC_DATE, UTC_TIME, UTC_TIMESTAMP, " //
                    + "VALUES, VARBINARY, VARCHAR, VARCHARACTER, VARYING, VIRTUAL, WHEN, WHERE, " //
                    + "WHILE, WITH, WRITE, XOR, YEAR_MONTH, ZEROFILL" //
    ;
    /**
     * 예약어 목록
     * 
     * @since 2025. 4. 2.
     */
    public static final Set<String> RESERVED_KEYWORDS = CommonUtils.loadReservedKeywords(RESERVED_KEYWORD_STRING);
    /**
     * 예약어 감싸는 문자
     * 
     * @since 2025. 4. 2.
     */
    public static final CharSequence RESERVED_KEYWORDS_WRAPPING_CHARACTER = "\"";

    public static final String QUERY_FOR_OFFSET = "LIMIT ?, ?";

    private MariadbCommons() {
    }

}
