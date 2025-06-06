/*
 * Copyright 2020 Park Jun-Hong_(parkjunhong77@gmail.com)
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
 * Date  : 2020. 7. 28. 오후 7:17:07
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.spring.jdbc.dao.postgresql;

import java.util.List;

import open.commons.core.Result;
import open.commons.core.function.SQLConsumer;
import open.commons.core.utils.ArrayUtils;
import open.commons.spring.jdbc.dao.AbstractSingleDataSourceDao;

/**
 * PostgreSQL 연동 DAO 상위 클래스.
 * 
 * @since 2020. 7. 28.
 * @version
 * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
 */
public abstract class AbstractPostgreSingleDataSourceDao extends AbstractSingleDataSourceDao {

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 28.		박준홍			최초 작성
     * </pre>
     *
     * @since 2020. 7. 28.
     */
    public AbstractPostgreSingleDataSourceDao() {
    }

    /**
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 28.		박준홍			최초 작성
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
     * @since 2020. 7. 28.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public <E> Result<List<E>> getList(String query, Object[] parameters, int begin, int count, Class<E> entity, String... columns) {
        String partQuery = wrapQueryForPartition(query);
        Object[] newParams = ArrayUtils.add(parameters, begin, count);

        return getList(partQuery, SQLConsumer.setParameters(newParams), entity, columns);
    }

    /**
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 7. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            조회 쿼리 또는 테이블명
     * @return
     *
     * @since 2020. 7. 28.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    protected final String wrapQueryForPartition(String query) {
        StringBuffer queryBuffer = new StringBuffer("SELECT * FROM ( ");
        queryBuffer.append(query);
        queryBuffer.append(" ) tbl OFFSET ? LIMIT ?");

        return queryBuffer.toString();
    }

}
