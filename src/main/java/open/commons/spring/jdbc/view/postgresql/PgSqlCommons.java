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
 * Date  : 2025. 5. 13. 오후 5:45:54
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.view.postgresql;

import java.util.Set;

import open.commons.spring.jdbc.utils.CommonUtils;

/**
 * 
 * @since 2025. 5. 13.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class PgSqlCommons {

    /**
     * 예약어 목록 문자열
     * 
     * @since 2025. 4. 2.
     */
    public static final String RESERVED_KEYWORD_STRING = //
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
    public static final Set<String> RESERVED_KEYWORDS = CommonUtils.loadReservedKeywords(RESERVED_KEYWORD_STRING);
    /**
     * 예약어 감싸는 문자
     * 
     * @since 2025. 4. 2.
     */
    public static final CharSequence RESERVED_KEYWORDS_WRAPPING_CHARACTER = "\"";

    public static final String QUERY_FOR_OFFSET = "OFFSET ? LIMIT ?";

    private PgSqlCommons() {
    }

}
