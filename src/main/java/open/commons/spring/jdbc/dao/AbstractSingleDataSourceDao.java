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
 * Date  : 2020. 4. 15. 오후 12:42:20
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.spring.jdbc.dao;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import open.commons.core.utils.AssertUtils2;

/**
 * 단일 DBMS와 연동하는 기능을 지원한다.
 * 
 * @since 2020. 4. 15.
 * @version
 * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
 */
public abstract class AbstractSingleDataSourceDao extends AbstractGenericDao {

    protected DataSource dataSource;
    protected JdbcTemplate jdbcTemplate;

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 4. 15.		박준홍			최초 작성
     * </pre>
     *
     * @since 2020. 4. 15.
     * @version
     */
    public AbstractSingleDataSourceDao() {
    }

    /**
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        AssertUtils2.assertNotNull("DataSource MUST NOT BE null.", this.dataSource);
    }

    /**
     * {@link DataSource}를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 28.     박준홍         최초 작성
     * 2020. 4. 15.		박준홍        클래스 분리로 메소드 이동
     * 2025. 6. 11.     박준홍         {@link Transactional}을 이용하여 @Repository 계층 클래스의 메소드를 관리하기 위해서 DataSource를 TransactionAwareDataSourceProxy로 감싸는 걸 적용.
     * </pre>
     *
     * @return
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @see open.commons.spring.jdbc.dao.IGenericDao#getDataSource()
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public <E> E getDataSource() {
        return (E) getDataSource0(this.dataSource);
    }

    /**
     * {@link JdbcTemplate}를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 28.     박준홍         최초 작성
     * 2020. 4. 15.		박준홍        클래스 분리로 메소드 이동
     * </pre>
     *
     * @return
     *
     * @since 2019. 3. 28.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * @version 0.1.0
     */
    public JdbcTemplate getJdbcTemplate() {
        if (this.jdbcTemplate == null) {
            this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        }

        return this.jdbcTemplate;
    }

    /**
     * {@link DataSource} 객체를 설정한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 28.     박준홍         최초 작성
     * 2020. 4. 15.     박준홍        클래스 분리로 메소드 이동
     * </pre>
     *
     * @param dataSource
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public abstract void setDataSource(@NotNull DataSource dataSource);
}
