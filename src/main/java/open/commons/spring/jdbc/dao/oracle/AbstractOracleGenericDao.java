/*
 * Copyright 2019 Park Jun-Hong_(parkjunhong77/google/com)
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
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.spring.jdbc.dao.oracle;

import java.sql.PreparedStatement;
import java.util.List;

import open.commons.Result;
import open.commons.function.SQLTripleFunction;
import open.commons.spring.jdbc.dao.AbstractGenericDao;

/**
 * Oracle DBMS 연동 기능이 추가된 클래스.
 * 
 * @since 2020. 1. 17.
 * @author Park_Jun_Hong_(fafanmama_at_naver_com)
 * @version 0.0.6
 */
public abstract class AbstractOracleGenericDao extends AbstractGenericDao {

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
     * 다수 개의 데이터를 설정된 크기로 나누어 데이터를 추가한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 1. 21.		박준홍			최초 작성
     * </pre>
     *
     * @param <T>
     *            데이타 타입.
     * @param data
     *            데이타
     * @param dataSetter
     *            객체 데이터를 {@link PreparedStatement}에 추가하는 주체.<br>
     *            참조: {@link SQLTripleFunction#setParameters(String...)}
     * @param partitionSize
     *            데이터 분할 크기
     * @param valueQuery
     * @return 여러개 데이터 바인딩용 쿼리. (?, ?, ...) 이 포함된 구문이 설정됨.
     *
     * @since 2020. 1. 21.
     * @version 0,.0.6
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     *
     * @see SQLTripleFunction#setParameters(String...)
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#executeUpdate(java.util.List,
     *      open.commons.function.SQLTripleFunction, int, java.lang.String)
     */
    public <E> Result<Integer> executeUpdate(List<E> data, SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, int partitionSize, String valueQuery) {
        return super.executeUpdate(data, dataSetter, partitionSize, "INSERT ALL ", valueQuery, " SELECT 1 FROM DUAL");
    }
}
