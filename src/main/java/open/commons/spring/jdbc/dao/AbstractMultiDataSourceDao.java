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

import javax.sql.DataSource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.validation.annotation.Validated;

import open.commons.TwoValueObject;
import open.commons.function.SQLFunction;

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
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#execute(open.commons.function.SQLFunction)
     */
    @Override
    protected <R> R execute(@NotNull SQLFunction<Connection, R> act) throws SQLException {

        Collection<DataSource> colDataSources = getDataSources();
        ArrayList<TwoValueObject<Connection, DataSource>> cons = new ArrayList<>();
        ArrayList<Connection> consToWork = new ArrayList<>();

        Connection con = null;
        Connection conToWork = null;
        JdbcTemplate jdbcTemplate = null;

        R result = null;
        try {
            for (DataSource ds : colDataSources) {
                cons.add(new TwoValueObject<Connection, DataSource>(con = DataSourceUtils.getConnection(ds), ds));
                jdbcTemplate = getJdbcTemplate(ds);

                con.setAutoCommit(false);
                // (start) [BUG-FIX]: spring 5.x 부터 4.x에 존재하던 public NativeJdbcExtractor getNativeJdbcExtractor()
                // 를 제거함에 따라 호환성 지원 / Park_Jun_Hong_(parkjunhong77@gmail.com): 2019. 6. 5. 오후 5:14:17
                consToWork.add(conToWork = getConnection(con, jdbcTemplate));
                // (end): 2019. 6. 5. 오후 5:14:17

                conToWork.setAutoCommit(false);
                result = act.apply(conToWork);

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
            msg.append(", con-to-work=");
            msg.append(conToWork != null ? conToWork.toString() : null);

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
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 4. 15.		박준홍			최초 작성
     * </pre>
     * 
     * @return the dataSources
     *
     * @since 2020. 4. 15.
     * @version
     * 
     * @see #dataSources
     */
    public Collection<DataSource> getDataSources() {
        return dataSources;
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
