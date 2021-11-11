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
 * Date  : 2020. 1. 17. 오전 12:59:31
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.spring.jdbc.dao.oracle;

import java.sql.PreparedStatement;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import open.commons.Result;
import open.commons.function.SQLTripleFunction;
import open.commons.spring.jdbc.dao.AbstractSingleDataSourceDao;

/**
 * Oracle DBMS 연동 기능이 추가된 클래스.
 * 
 * @since 2020. 1. 17.
 * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
 * @version 0.0.6
 */
public abstract class AbstractOracleGenericDao extends AbstractSingleDataSourceDao {

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 17.		박준홍			최초 작성
     * </pre>
     *
     * @since 2020. 1. 17.
     * @version 0.0.6
     */
    public AbstractOracleGenericDao() {
    }

    /**
     *
     * @see SQLTripleFunction#setParameters(String...)
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#executeUpdate(java.util.List,
     *      open.commons.function.SQLTripleFunction, int, java.lang.String)
     */
    @Override
    public <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String valueQuery) {
        return super.executeUpdate(data, dataSetter, partitionSize, "INSERT ALL ", valueQuery, " SELECT 1 FROM DUAL");
    }
}
