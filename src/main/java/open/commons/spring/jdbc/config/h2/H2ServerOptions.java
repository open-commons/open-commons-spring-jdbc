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
 * Date  : 2025. 4. 3. 오전 11:15:55
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config.h2;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
/**
 * H2 서버 구동 옵션용.
 * 
 * @since 2025. 4. 3.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public @interface H2ServerOptions {

    /**
     * H2 옵션 이름에 서버 유형 포함 여부. <br>
     * '서버 유형' 정보는 'H2 옵션 이름' 앞쪽에 위치함. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 3.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public boolean addType() default true;

    /**
     * H2 서버 유형 정보가 제외된 옵션 이름. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 3.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public String name();
}
