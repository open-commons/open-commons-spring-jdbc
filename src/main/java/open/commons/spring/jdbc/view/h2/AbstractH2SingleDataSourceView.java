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
 * Date  : 2025. 5. 13. 오후 5:30:26
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.view.h2;

import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import open.commons.spring.jdbc.view.AbstractSingleDataSourceView;

/**
 * H2 DB View 연동.
 * 
 * @since 2025. 5. 13.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public abstract class AbstractH2SingleDataSourceView<T> extends AbstractSingleDataSourceView<T> {

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 5. 13.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     *
     * @since 2025. 5. 13.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2SingleDataSourceView(@NotNull Class<T> entityType) {
        this(entityType, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 5. 13.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     *
     * @since 2025. 5. 13.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2SingleDataSourceView(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        this(entityType, forceToPrimitive, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 5. 13.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     * @param ignoreNoDataMethod
     *            데이터를 제공하는 메소드가 없는 경우 로그 미발생 여부
     *
     * @since 2025. 5. 13.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2SingleDataSourceView(@NotNull Class<T> entityType, boolean forceToPrimitive, boolean ignoreNoDataMethod) {
        super(entityType, forceToPrimitive, ignoreNoDataMethod);
    }

    /**
     *
     * @since 2025. 5. 13.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.view.AbstractGenericView#getReservedKeywords()
     */
    @Override
    protected Set<String> getReservedKeywords() {
        return H2Commons.RESERVED_KEYWORDS;
    }

    /**
     *
     * @since 2025. 5. 13.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.view.AbstractGenericView#getReservedKeywordWrappingCharacter()
     */
    @Override
    protected CharSequence getReservedKeywordWrappingCharacter() {
        return H2Commons.RESERVED_KEYWORDS_WRAPPING_CHARACTER;
    }

    /**
     *
     * @since 2025. 5. 13.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.view.AbstractGenericView#queryForOffset(int, int)
     */
    @Override
    protected String queryForOffset(@Min(0) int offset, @Min(1) int limit) {
        return H2Commons.QUERY_FOR_OFFSET;
    }
}
