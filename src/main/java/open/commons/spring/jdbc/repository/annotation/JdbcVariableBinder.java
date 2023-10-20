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
 * Date  : 2021. 12. 13. 오후 8:38:22
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import open.commons.core.annotation.ColumnDef.ColumnNameType;
import open.commons.core.annotation.ColumnValue;

/**
 * Where 구문에 적용되는 Variable Binding 에 사용되는 기능 정의.
 * 
 * @since 2021. 12. 13.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface JdbcVariableBinder {

    /**
     * 데이터가 사용되는 위치 <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2023. 1. 11.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2023. 1. 11.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    ColumnLocation at() default ColumnLocation.WHERE;

    /**
     * 컬럼명을 변환하는 방식.<br>
     * 프로그래밍 언어와 DBMS 간 명명규칙이 상이하기 때문에 필요. <br>
     * <b>기본값: {@link ColumnNameType#NAME}</b>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2023. 10. 19.     박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2020. 1. 16.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @see ColumnNameType
     */
    ColumnNameType columnNameType() default ColumnNameType.NAME;

    /**
     * 컬럼명을 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 13.      박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 12. 13.
     * @version 0.3.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @see ColumnValue#name()
     */
    String name() default "";

    /**
     * Where 구문에서 컬럼값을 비교하기 위한 비교연산자를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 13.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 12. 13.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    WhereCompare operator() default WhereCompare.EQ;

    /**
     * JDBC Variable Binding 에 사용될 문자열을 제공합니다. <br>
     * 일반적으로 물음표(?)를 사용하지만, 연동하는 DBMS에서 제공하는 함수나 프로시저와 같은 정보를 사용하는 경우 지원합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 13.     박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 12. 13.
     * @version 0.3.0
     * @author Park Jun-Hong (parkjunhong77@gmail.com)
     * 
     * @see ColumnValue#variableBinding()
     */
    String variableBinding() default "?";

    /**
     * 컬럼이 적용되는 위치.
     * 
     * @since 2023. 1. 11.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    public static enum ColumnLocation {
        /**
         * 'UPDATE' 쿼리의 'SET' 컬럼 설정 부분<br>
         * 
         * <pre>
         * UPDATE &lt;table&gt; SET &lt;column&gt; = ...
         * </pre>
         */
        UPDATE_SET, //
        /**
         * 'WHEREH' 컬럼 설정 부분<br>
         * 
         * <pre>
         * ... WHERE <column> LOGICAl_OP ...
         * </pre>
         */
        WHERE, //
        /**
         * 'INSERT' 컬럼 설정 부분<br>
         * 
         * <pre>
         * INSERT INTO &lt;table&gt; ( &lt;column&gt; ...) VALUES ( ...
         * </pre>
         */
        INSERT, //
    }

    /**
     * 'WHERE' 구문에 사용되는 논리연산자.
     * 
     * @since 2021. 12. 13.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public static enum WhereCompare {
        EQ("="), //
        GE(">="), //
        GT(">"), //
        LE("<="), //
        LT("<"), //
        NOT("!="), //
        //
        /** format: IN ( ... ) */
        IN("IN"), //
        /** format: NOT IN ( ... ) */
        NOT_IN("NOT IN"), //
        //
        /** format: {variable}LIKE %?% */
        LIKE("LIKE"), //
        /** format: LIKE %? */
        LIKE_PRE("LIKE"), //
        /** format: LIKE ?% */
        LIKE_POST("LIKE"), //
        //
        /** format: NOT LIKE %?% */
        NOT_LIKE("NOT LIKE"), //
        /** format: NOT LIKE %? */
        NOT_LIKE_PRE("NOT LIKE"), //
        /** format: NOT LIKE ?% */
        NOT_LIKE_POST("NOT LIKE"), //
        /** format: */
        IS_NOT_NULL("IS NOT NULL"), //
        /** 지원하지 않는 위치 */
        UNSUPPORTED("UNSUPPORTED"), //
        ;

        private final String op;

        private WhereCompare(String op) {
            this.op = op;
        }

        public String get() {
            return op;
        }
    }
}
