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
 * Date  : 2025. 5. 13. 오후 5:45:59
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.view.oracle;

import java.util.Set;

import open.commons.spring.jdbc.utils.CommonUtils;

/**
 * 
 * @since 2025. 5. 13.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class OracleCommons {

    /**
     * 예약어 목록 문자열
     * 
     * @since 2025. 4. 2.
     */
    public static final String RESERVED_KEYWORD_STRING = //
            "ACCESS, ADD, ALL, ALTER, AND, ANY, AS, ASC, AUDIT, BETWEEN, BY, CHAR, " //
                    + "CHECK, CLUSTER, COLUMN, COMMENT, COMPRESS, CONNECT, CREATE, CURRENT, " //
                    + "DATE, DECIMAL, DEFAULT, DELETE, DESC, DISTINCT, DROP, ELSE, EXCLUSIVE, " //
                    + "EXISTS, FILE, FLOAT, FOR, FROM, GRANT, GROUP, HAVING, IDENTIFIED, " //
                    + "IMMEDIATE, IN, INCREMENT, INDEX, INITIAL, INSERT, INTEGER, INTERSECT, " //
                    + "INTO, IS, LEVEL, LIKE, LOCK, LONG, MAXEXTENTS, MINUS, MLSLABEL, MODE, " //
                    + "MODIFY, NOAUDIT, NOCOMPRESS, NOT, NOWAIT, NULL, NUMBER, OF, OFFLINE, " //
                    + "ON, ONLINE, OPTION, OR, ORDER, PCTFREE, PRIOR, PUBLIC, RAW, RENAME, " //
                    + "RESOURCE, REVOKE, ROW, ROWID, ROWLABEL, ROWNUM, ROWS, SELECT, SESSION, " //
                    + "SET, SHARE, SIZE, SMALLINT, START, SUCCESSFUL, SYNONYM, SYSDATE, TABLE, " //
                    + "THEN, TO, TRIGGER, UID, UNION, UNIQUE, UPDATE, USER, VALIDATE, VALUES, " //
                    + "VARCHAR, VARCHAR2, VIEW, WHENEVER, WHERE, WITH" //
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

    public static final String QUERY_FOR_OFFSET = "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    private OracleCommons() {
    }
}
