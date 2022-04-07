/*
 * Copyright 2019 Park Jun-Hong_(parkjunhong77@gmail.com)
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
 * Date  : 2020. 1. 22. 오전 12:57:08
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.spring.jdbc.dao.oracle;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javax.validation.constraints.NotNull;

import open.commons.core.Result;
import open.commons.core.function.SQLConsumer;
import open.commons.core.test.StopWatch;
import open.commons.core.utils.ArrayUtils;
import open.commons.spring.jdbc.dao.IAsyncSupportable;

/**
 * 
 * @since 2020. 1. 22.
 * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
 * @version 0.0.6
 */
public abstract class AbstractAsyncOralceGenericDao extends AbstractOracleGenericDao implements IAsyncSupportable {

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 22.		박준홍			최초 작성
     * </pre>
     *
     * @since 2020. 1. 22.
     * @version 0.0.6
     */
    public AbstractAsyncOralceGenericDao() {
    }

    /**
     * @see open.commons.spring.jdbc.dao.IAsyncSupportable#getList(java.lang.String, int, int, java.lang.Class,
     *      java.util.function.Supplier, java.util.function.Supplier)
     */
    @Override
    public <E> Result<List<E>> getList(@NotNull String query, int offset, int fetch, @NotNull Class<E> dataType, @NotNull Supplier<Object[]> params,
            @NotNull Supplier<String[]> columns) throws NullPointerException, IllegalArgumentException {
        StringBuffer extendedQuery = new StringBuffer(query);
        extendedQuery.append(' ');
        extendedQuery.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        Object[] parameters = ArrayUtils.add(params.get(), offset, fetch);

        logger.debug("Query: {}, offset: {}, count: {}, latestTime: {}", query, offset, fetch, Arrays.toString(parameters));

        // begin - 성능 측정
        StopWatch watch = new StopWatch();
        watch.start();

        Result<List<E>> result = getList(extendedQuery.toString(), SQLConsumer.setParameters(parameters), dataType, columns.get());

        watch.stop();
        // end - 성능 측정

        if (result.getResult()) {
            logger.debug("[데이터 조회] 성공, 데이터 개수: {}, 경과시간: {}", result.getData().size(), watch.getAsPretty());
        } else {
            logger.debug("[데이터 조회] 실패, 원인: {}, 경과시간: {}", result.getMessage(), watch.getAsPretty());
        }

        return result;
    }

}
