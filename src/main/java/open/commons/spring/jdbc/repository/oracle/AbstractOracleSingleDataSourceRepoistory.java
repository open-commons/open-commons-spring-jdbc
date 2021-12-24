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
 * Date  : 2021. 12. 16. 오후 4:47:21
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository.oracle;

import java.sql.PreparedStatement;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import open.commons.Result;
import open.commons.function.SQLTripleFunction;
import open.commons.spring.jdbc.repository.AbstractGenericRepository;
import open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository;

/**
 * Oracle DBMS 연동을 위한 클래스.
 * 
 * @since 2021. 12. 16.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
public abstract class AbstractOracleSingleDataSourceRepoistory<T> extends AbstractSingleDataSourceRepository<T> {

    protected final String QUERY_FOR_OFFSET = "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 16.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractOracleSingleDataSourceRepoistory(@NotNull Class<T> entityType) {
        this(entityType, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 16.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     * @param forceToPrimitive
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractOracleSingleDataSourceRepoistory(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        super(entityType, forceToPrimitive);
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version _._._
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#executeUpdate(java.util.List,
     *      open.commons.function.SQLTripleFunction, int, java.lang.String)
     * 
     * @deprecated Use {@link AbstractGenericRepository#insert(List, int)}
     */
    @Override
    public <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String valueQuery) {
        throw new UnsupportedOperationException("#insert(List<T>, int) 를 사용하세요.");
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForOffset(int, int)
     */
    @Override
    protected String queryForOffset(@Min(0) int offset, @Min(1) int limit) {
        return QUERY_FOR_OFFSET;
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionConcatValue()
     */
    @Override
    protected String queryForPartitionConcatValue() {
        return "";
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionHeader()
     */
    @Override
    protected String queryForPartitionHeader() {
        return "INSERT ALL";
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionTail()
     */
    @Override
    protected String queryForPartitionTail() {
        return "SELECT 1 FROM DUAL";
    }

    /**
     *
     * @since 2021. 12. 16.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionValue()
     */
    @Override
    protected String queryForPartitionValue() {

        List<String> columns = getColumnNames();

        return new StringBuffer()//
                .append("INTO ") //
                .append(getTableName()) //
                .append(" ( ") //
                .append(String.join(", ", columns.toArray(new String[0]))) //
                .append(" ) ") //
                .append("VALUES") //
                .append(" ( ") //
                .append(queryForVariableBinding()) //
                .append(" )") //
                .toString();
    }
}
