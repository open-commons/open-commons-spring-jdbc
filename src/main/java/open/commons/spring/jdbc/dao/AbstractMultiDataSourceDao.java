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
 * Date  : 2020. 4. 15. 오후 12:59:20
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.spring.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import open.commons.core.TwoValueObject;
import open.commons.core.function.SQLFunction;

/**
 * 여러 개의 DBMS에 동일한 작업(SQL)를 수행하는 기능을 지원.
 * 
 * @since 2020. 4. 15.
 * @version
 * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
 */
@Validated
public abstract class AbstractMultiDataSourceDao extends AbstractGenericDao {

    protected Collection<DataSource> dataSources;

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
    public AbstractMultiDataSourceDao() {
    }

    /**
     * @see open.commons.spring.jdbc.dao.AbstractGenericRetrieve#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notEmpty(this.dataSources, "DataSource MUST NOT BE 'null' & 'empty'.");
    }

    /**
     * @see open.commons.spring.jdbc.dao.AbstractGenericRetrieve#execute(open.commons.core.function.SQLFunction)
     */
    @Override
    protected <R> R execute(@NotNull SQLFunction<Connection, R> act) throws SQLException {

        Collection<DataSource> colDataSources = getDataSource();
        ArrayList<TwoValueObject<Connection, DataSource>> cons = new ArrayList<>();

        Connection con = null;
        JdbcTemplate jdbcTemplate = null;

        R result = null;
        try {
            for (DataSource ds : colDataSources) {
                cons.add(new TwoValueObject<Connection, DataSource>(con = DataSourceUtils.getConnection(ds), ds));
                jdbcTemplate = getJdbcTemplate(ds);

                con.setAutoCommit(false);
                result = act.apply(con);

                if (result == null) {
                    continue;
                }

                if (Collection.class.isAssignableFrom(result.getClass())) {
                    logger.debug("result-set.size={}", ((Collection<?>) result).size());
                } else {
                    logger.debug("result={}", result);
                }
            }
        } catch (SQLException e) {
            logger.warn("Fail to execute query.", e);

            cons.forEach(c -> {
                try {
                    c.first.rollback();
                } catch (SQLException ignored) {
                }
            });
            StringBuffer msg = new StringBuffer();
            msg.append("con=");
            msg.append(con.toString());

            DataAccessException dae = jdbcTemplate.getExceptionTranslator().translate("ConnectionCallback", msg.toString(), e);
            throw new SQLException(dae.getMessage(), dae);
        } finally {
            cons.forEach(c -> {
                try {
                    if (c.first != null) {
                        c.first.commit();
                    }
                } catch (SQLException ignored) {
                }
                DataSourceUtils.releaseConnection(c.first, c.second);
            });
        }

        return result;
    }

    /**
     *
     * @since 2025. 6. 11.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.dao.IGenericDao#getDataSource()
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getDataSource() {
        return (T) this.dataSources.stream().map(//
                ds -> ds instanceof TransactionAwareDataSourceProxy //
                        ? ds //
                        : new TransactionAwareDataSourceProxy(ds) //
        ).collect(Collectors.toList());
    }

    /**
     * 주어진 {@link DataSource}를 이용하여 새로운 {@link JdbcTemplate}를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 4. 15.     박준홍         최초 작성
     * </pre>
     *
     * @param dataSource
     * @return
     *
     * @since 2020. 4. 15.
     * @version
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    private JdbcTemplate getJdbcTemplate(@NotNull DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * 여러 개의 {@link DataSource} 객체를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 4. 15.     박준홍         최초 작성
     * </pre>
     *
     * @param dataSources
     * 
     * @since 2020. 4. 15.
     * @version
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     */
    public abstract void setDataSources(@NotNull @NotEmpty Collection<DataSource> dataSources);
}
