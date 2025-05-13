/*
 * Copyright 2025 Park Jun-Hong_(parkjunhong77@gmail.com)
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
 * Date  : 2025. 5. 13. 오후 4:54:54
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.view;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import open.commons.core.function.SQLFunction;
import open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository;

/**
 * 
 * @since 2025. 5. 13.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public abstract class AbstractSingleDataSourceView<T> extends AbstractGenericView<T> {

    protected DataSource dataSource;
    protected JdbcTemplate jdbcTemplate;

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
    public AbstractSingleDataSourceView(@NotNull Class<T> entityType) {
        this(entityType, true, true);
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
    public AbstractSingleDataSourceView(@NotNull Class<T> entityType, boolean forceToPrimitive) {
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
    public AbstractSingleDataSourceView(@NotNull Class<T> entityType, boolean forceToPrimitive, boolean ignoreNoDataMethod) {
        super(entityType, forceToPrimitive, ignoreNoDataMethod);
    }

    /**
     *
     * @since 2025. 5. 13.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.dao.AbstractGenericRetrieve#execute(open.commons.core.function.SQLFunction)
     */
    @Override
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
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 5. 13.		박준홍			코드 복제. {@link AbstractSingleDataSourceRepository}
     * </pre>
     *
     * @return
     *
     * @since 2025. 5. 13.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public DataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * {@link JdbcTemplate}를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 5. 13.		박준홍			코드 복제. {@link AbstractSingleDataSourceRepository}
     * </pre>
     *
     * @return
     *
     * @since 2025. 5. 13.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public JdbcTemplate getJdbcTemplate() {
        if (this.jdbcTemplate == null) {
            this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        }

        return this.jdbcTemplate;
    }

    /**
     * {@link DataSource} 객체를 설정한다. <br>
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 5. 13.		박준홍			코드 복제. {@link AbstractSingleDataSourceRepository}
     * </pre>
     *
     * @param dataSource
     *
     * @since 2025. 5. 13.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public abstract void setDataSource(@NotNull DataSource dataSource);

}
