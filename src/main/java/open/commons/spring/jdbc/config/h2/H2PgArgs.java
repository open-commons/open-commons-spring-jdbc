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
 * Date  : 2025. 4. 3. 오전 11:17:46
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config.h2;

/**
 * PG 방식 설정 클래스.
 * 
 * @since 2025. 4. 3.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class H2PgArgs extends AbstractH2ServerTypeArgs {

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 3.		박준홍			최초 작성
     * </pre>
     *
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public H2PgArgs() {
        super(H2ServerType.PG, 5435 /* 기본값 */);
    }

    /**
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("H2PgArgs [type=");
        builder.append(type);
        builder.append(", port=");
        builder.append(port);
        builder.append(", allowOthers=");
        builder.append(allowOthers);
        builder.append(", daemon=");
        builder.append(daemon);
        builder.append("]");
        return builder.toString();
    }
}
