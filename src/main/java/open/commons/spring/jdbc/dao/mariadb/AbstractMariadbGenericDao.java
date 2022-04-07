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
 * Date  : 2021. 11. 11. 오후 5:05:39
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.dao.mariadb;

import java.sql.PreparedStatement;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import open.commons.core.Result;
import open.commons.core.function.SQLTripleFunction;
import open.commons.spring.jdbc.dao.AbstractSingleDataSourceDao;

/**
 * 하나의 Mariadb DBMS와 연동하는 객체.
 * 
 * @since 2021. 11. 11.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
public abstract class AbstractMariadbGenericDao extends AbstractSingleDataSourceDao {

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 11.		박준홍			최초 작성
     * </pre>
     *
     *
     * @since 2021. 11. 11.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractMariadbGenericDao() {
    }

    /**
     *
     * @since 2021. 11. 11.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#executeUpdate(java.util.List,
     *      open.commons.function.SQLTripleFunction, int, java.lang.String, java.lang.String)
     */
    @Override
    public <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String headerQuery, @NotNull String valueQuery) {
        return executeUpdate(data, dataSetter, partitionSize, headerQuery, valueQuery, ",", "");
    }
}
