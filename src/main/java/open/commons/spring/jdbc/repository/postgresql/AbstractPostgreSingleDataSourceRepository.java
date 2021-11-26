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
 * Date  : 2021. 11. 26. 오후 5:02:26
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository.postgresql;

import java.util.List;

import javax.validation.constraints.NotNull;

import open.commons.Result;
import open.commons.function.SQLConsumer;
import open.commons.spring.jdbc.dao.postgresql.AbstractPostgreSingleDataSourceDao;
import open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository;
import open.commons.utils.ArrayUtils;

/**
 * PostgreSQL 연동을 위한 클래스.
 * 
 * @since 2021. 11. 26.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
public abstract class AbstractPostgreSingleDataSourceRepository<T> extends AbstractSingleDataSourceRepository<T> {

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractPostgreSingleDataSourceRepository(@NotNull Class<T> entityType) {
        super(entityType);
    }

    /**
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @param <E>
     * @param query
     *            조회 쿼리 또는 테이블명
     * @param parameters
     *            파라미터
     * @param begin
     *            데이터 조회 시작 위치
     * @param count
     *            데이터 조회 최대 개수
     * @param entity
     *            데이터 타입
     * @param columns
     *            조회할 컬럼
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see AbstractPostgreSingleDataSourceDao#getList(String, Object[], int, int, Class, String...)
     */
    public <E> Result<List<E>> getList(String query, Object[] parameters, int begin, int count, Class<E> entity, String... columns) {
        String partQuery = wrapQueryForPartition(query);
        Object[] newParams = ArrayUtils.add(parameters, begin, count);

        return getList(partQuery, SQLConsumer.setParameters(newParams), entity, columns);
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository#queryForOffset(int, int)
     */
    @Override
    protected String queryForOffset(int offset, int limit) {
        return "OFFSET ? LIMIT ?";
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository#queryForPartitionConcatValue()
     */
    @Override
    protected String queryForPartitionConcatValue() {
        return ",";
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository#queryForPartitionHeader()
     */
    @Override
    protected String queryForPartitionHeader() {
        List<String> columns = getColumnNames();

        return new StringBuffer() //
                .append("INSERT INTO") //
                .append(" ") //
                .append(this.tableName) //
                .append(" (")//
                .append(String.join(", ", columns.toArray(new String[0]))) //
                .append(") ") //
                .append("VALUES") //
                .toString();
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository#queryForPartitionTail()
     */
    @Override
    protected String queryForPartitionTail() {
        return "";
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository#queryForPartitionValue()
     */
    @Override
    protected String queryForPartitionValue() {
        return String.join(" ", "(", queryForVariableBinding(), ")");
    }

    protected final String wrapQueryForPartition(String query) {
        StringBuffer queryBuffer = new StringBuffer("SELECT * FROM ( ");
        queryBuffer.append(query);
        queryBuffer.append(" ) tbl OFFSET ? LIMIT ?");

        return queryBuffer.toString();
    }
}
