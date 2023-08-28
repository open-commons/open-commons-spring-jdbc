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
 * Date  : 2021. 11. 30. 오후 3:36:43
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import open.commons.core.function.SQLFunction;
import open.commons.core.utils.AssertUtils;
import open.commons.spring.jdbc.dao.AbstractSingleDataSourceDao;

/**
 * 단일 DBMS와 연동하는 기능을 지원한다.
 * 
 * @since 2021. 11. 30.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 * 
 * @see AbstractSingleDataSourceDao
 */
public abstract class AbstractSingleDataSourceRepository<T> extends AbstractGenericRepository<T> {

    protected DataSource dataSource;
    protected JdbcTemplate jdbcTemplate;

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 12. 6.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     *
     * @since 2021. 12. 6.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #AbstractSingleDataSourceRepository(Class, boolean)
     */
    public AbstractSingleDataSourceRepository(@NotNull Class<T> entityType) {
        this(entityType, true, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 30.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper Class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     *
     * @since 2021. 11. 30.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractSingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        this(entityType, forceToPrimitive, true);
    }

    /**
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2023. 8. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     * @param ignoreNoDataMethod
     *            데이터를 제공하는 메소드가 없는 경우 로그 미발생 여부
     *
     * @since 2023. 8. 28.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractSingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive, boolean ignoreNoDataMethod) {
        super(entityType, forceToPrimitive, ignoreNoDataMethod);
    }

    /**
     *
     * @since 2021. 11. 30.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        AssertUtils.assertNull("DataSource MUST NOT BE null.", this.dataSource);
    }

    /**
     * 쿼리 요청을 처리하고 결과를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 28.     박준홍     최초 작성
     * 2019. 6. 5.      박준홍     작업용 Connection 객체 생성 로직 수직
     * 2020. 4. 15.     박준홍     클래스를 분리하여 이동.
     * 2021. 11. 30.    박준홍     코드 복제. (open.commons.spring.jdbc.dao.AbstractSingleDataSourceDao)
     * </pre>
     *
     * @param act
     *            {@link Connection}을 전달받아 요청쿼리를 처리하는 객체
     * @return 쿼리처리 결과.
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    protected <R> R execute(@NotNull SQLFunction<Connection, R> act) throws SQLException {

        Connection con = DataSourceUtils.getConnection(getDataSource());
        Connection conToWork = null;

        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        try {
            con.setAutoCommit(false);
            // (start) [BUG-FIX]: spring 5.x 부터 4.x에 존재하던 public NativeJdbcExtractor getNativeJdbcExtractor()
            // 를 제거함에 따라 호환성 지원 / Park_Jun_Hong_(parkjunhong77@gmail.com): 2019. 6. 5. 오후 5:14:17
            conToWork = getConnection(con, jdbcTemplate);
            // (end): 2019. 6. 5. 오후 5:14:17

            conToWork.setAutoCommit(false);
            R r = act.apply(conToWork);

            return r;

        } catch (SQLException e) {
            logger.warn("Fail to execute query.", e);

            try {
                con.rollback();
            } catch (SQLException ignored) {
            }

            StringBuffer msg = new StringBuffer();
            msg.append("con=");
            msg.append(con.toString());
            msg.append(", con-to-work=");
            msg.append(conToWork != null ? conToWork.toString() : null);

            DataAccessException dae = jdbcTemplate.getExceptionTranslator().translate("ConnectionCallback", msg.toString(), e);
            throw new SQLException(dae.getMessage(), dae);
        } finally {
            try {
                if (con != null) {
                    con.commit();
                }
            } catch (SQLException ignored) {
            }

            DataSourceUtils.releaseConnection(con, dataSource);

            con = null;
            conToWork = null;
        }
    }

    /**
     * {@link DataSource}를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 28.     박준홍         최초 작성
     * 2020. 4. 15.     박준홍        클래스 분리로 메소드 이동
     * 2021. 11. 30.    박준홍     코드 복제. (open.commons.spring.jdbc.dao.AbstractSingleDataSourceDao)
     * </pre>
     *
     * @return
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public DataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * {@link JdbcTemplate}를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 28.     박준홍         최초 작성
     * 2020. 4. 15.     박준홍        클래스 분리로 메소드 이동
     * 2021. 11. 30.    박준홍     코드 복제. (open.commons.spring.jdbc.dao.AbstractSingleDataSourceDao)
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
     * 2021. 11. 30.    박준홍     코드 복제. (open.commons.spring.jdbc.dao.AbstractSingleDataSourceDao)
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
