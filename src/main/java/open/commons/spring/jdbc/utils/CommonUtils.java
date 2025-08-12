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
 * Date  : 2025. 5. 13. 오후 5:43:10
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.utils;

import java.util.Collections;
import java.util.Set;

import javax.validation.constraints.NotNull;

import open.commons.core.utils.StringUtils;

/**
 * 
 * @since 2025. 5. 13.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class CommonUtils {
    private CommonUtils() {
    }

    /**
     * 콤마(,)로 구분된 예약어 문자열을 {@link Set} 객체로 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 2.      박준홍         최초 작성
     * </pre>
     *
     * @param reservedKeywordString
     *            콤마(,)로 구분된 예약어 문자열
     * @return 중복없는 예약어(대문자) 목록
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public static final Set<String> loadReservedKeywords(@NotNull String reservedKeywordString) {
        return Collections.unmodifiableSet(StringUtils.splitAsSet(reservedKeywordString, ",", kw -> kw != null ? kw.trim().toUpperCase() : null));
    }

}
