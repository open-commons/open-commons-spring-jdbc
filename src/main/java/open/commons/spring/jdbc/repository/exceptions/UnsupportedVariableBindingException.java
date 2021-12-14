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
 * Date  : 2021. 12. 14. 오전 11:23:36
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository.exceptions;

import open.commons.spring.jdbc.repository.annotation.JdbcVariableBinder;

/**
 * 지원하지 않는 형태의 {@link JdbcVariableBinder}를 설정한 경우에 발생하는 예외상황 클래스.
 * 
 * @since 2021. 12. 14.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
public class UnsupportedVariableBindingException extends RuntimeException {

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 14.		박준홍			최초 작성
     * </pre>
     *
     *
     * @since 2021. 12. 14.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public UnsupportedVariableBindingException() {
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 14.		박준홍			최초 작성
     * </pre>
     *
     * @param message
     *
     * @since 2021. 12. 14.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public UnsupportedVariableBindingException(String message) {
        super(message);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 14.		박준홍			최초 작성
     * </pre>
     *
     * @param message
     * @param cause
     *
     * @since 2021. 12. 14.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public UnsupportedVariableBindingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 14.		박준홍			최초 작성
     * </pre>
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     *
     * @since 2021. 12. 14.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public UnsupportedVariableBindingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 14.		박준홍			최초 작성
     * </pre>
     *
     * @param cause
     *
     * @since 2021. 12. 14.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public UnsupportedVariableBindingException(Throwable cause) {
        super(cause);
    }

}
