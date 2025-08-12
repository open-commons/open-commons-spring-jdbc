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
 * Date  : 2025. 6. 11. 오후 4:26:10
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.exception;

import org.springframework.dao.DataAccessException;

/**
 * JDBC 기능 수행 도중 발생하는 예외클래스.
 * 
 * @since 2025. 6. 11.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
@SuppressWarnings("serial")
public class RuntimeDataAccessException extends DataAccessException {

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 6. 11.		박준홍			최초 작성
     * </pre>
     *
     * @param msg
     *
     * @since 2025. 6. 11.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public RuntimeDataAccessException(String msg) {
        super(msg);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 6. 11.		박준홍			최초 작성
     * </pre>
     *
     * @param msg
     * @param cause
     *
     * @since 2025. 6. 11.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public RuntimeDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
