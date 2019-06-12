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
 * This file is generated under this project, "open-commons-springframework4".
 *
 * Date  : 2019. 3. 28. 오후 3:42:37
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.spring.jdbc.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiFunction;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

import open.commons.Result;
import open.commons.annotation.ColumnDef;
import open.commons.database.ConnectionCallbackBroker;
import open.commons.database.ConnectionCallbackBroker2;
import open.commons.database.DefaultConCallbackBroker2;
import open.commons.database.IConnectionCallbackSetter;
import open.commons.function.SQLBiFunction;
import open.commons.function.SQLConsumer;
import open.commons.function.SQLFunction;
import open.commons.utils.AssertUtils;
import open.commons.utils.SQLUtils;

/**
 * DAO 공통 기능 제공 클래스.<br>
 * 
 * Springframework {@link JdbcTemplate}에서 사용하는 저레벨 수준의 코드를 이용하여 customize 하였음.
 * 
 * <hr>
 * {@link SQLConsumer}를 이용한 파라미터 설정 객체 생성 예제 <br>
 * 
 * <pre>
 * static final Function&lt;QueryParamObj, SQLConsumer&lt;PreparedStatement>> PROVIDER = param -> pstmt -> {
 *     pstmt.setString(1, param.getName());
 *     pstmt.setString(1, param.getCost());
 *     pstmt.setString(1, param.getDate());
 * };
 * 
 * static SQLConsumer&lt;PreparedStatement> create(QueryParamObj param) {
 *     return pstmt -> {
 *         pstmt.setString(1, param.getName());
 *         pstmt.setString(1, param.getCost());
 *         pstmt.setString(1, param.getDate());
 *     };
 * }
 * 
 * public static void main(String[] args) {
 *     // 쿼리 파라미터.
 *     QueryParamObj param = new QueryObj();
 * 
 *     // #1. java.util.function.Function 을 이용하는 법
 *     SQLConsumer<PreparedStatement> setter = PROVIDER.apply(param);
 *     // #2. 메소드를 이용하는 법
 *     setter = create(param);
 * 
 *     AbstractGenericDao dao = null; // ...
 * 
 *     // insert/update/delete
 *     String query = "INSERT ...";
 *     Result&lt;Integer> executeUpdate = dao.executeUpdate(query, setter);
 * 
 *     // select
 *     query = "SELECT ...";
 *     Result&lt;List&lt;QueryObj>> getList = dao.getList(query, setter, QueryObj.class);
 * 
 *     // select
 *     query = "SELECT ...";
 *     Result&lt;QueryObj> getObject = dao.getObject(query, setter, QueryObj.class);
 * }
 * </pre>
 * 
 * @since 2019. 3. 28.
 * @version 0.1.0
 * @author Park_Jun_Hong_(fafanmama_at_naver_com)
 */
public abstract class AbstractGenericDao implements IGenericDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected DataSource dataSource;
    protected ReloadableResourceBundleMessageSource querySource;
    protected JdbcTemplate jdbcTemplate;

    private final ConcurrentSkipListMap<String, SQLBiFunction<ResultSet, Integer, ?>> CREATORS = new ConcurrentSkipListMap<>();

    final BiFunction<Connection, JdbcTemplate, Connection> CONN_CREATOR = (c, t) -> {
        return (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class<?>[] { ConnectionProxy.class }, new CloseSuppressingInvocationHandler(c, t));
    };

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     */
    public AbstractGenericDao() {
    }

    /**
     * 기존 쿼리에 IN Clause(<code>"IN ( ?, ?, ...)")를 추가한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 6. 12.     박준홍         최초 작성
     * </pre>
     *
     * @param sqlBuffer
     *            쿼리 버퍼
     * @param inParamCount
     *            IN ( ... )에 사용될 파라미터 개수
     *
     * @since 2019. 6. 12.
     * @version
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    protected void addQueryForInClause(StringBuffer sqlBuffer, int inParamCount) {

        if (inParamCount < 1) {
            throw new IllegalArgumentException("Parameter count MUST BE LARGER than 0.");
        }

        sqlBuffer.append(" ( ?");

        for (int i = 1; i < inParamCount; i++) {
            sqlBuffer.append(", ?");
        }

        sqlBuffer.append(" )");
    }

    /**
     * 기존 쿼리 WHERE Clause에 IN Clause (<code>"IN ( ?, ?, ...)")를 추가한다.. <br>
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 6. 12.		박준홍			최초 작성
     * </pre>
     *
     * @param sqlBuffer
     *            쿼리 버퍼
     * @param concatenator
     *            WHERE 조건 병합 연산자 [ AND | OR ]
     * @param columnName
     *            비교 컬럼명
     * @param inParamCount
     *            IN ( ... )에 사용될 파라미터 개수
     *
     * @since 2019. 6. 12.
     * @version _._._
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    protected void addQueryForInClause(StringBuffer sqlBuffer, String concatenator, String columnName, int inParamCount) {

        if (inParamCount < 1) {
            throw new IllegalArgumentException("Parameter count MUST BE LARGER than 0.");
        }

        sqlBuffer.append(" ");
        sqlBuffer.append(concatenator);
        sqlBuffer.append(" ");
        sqlBuffer.append(columnName);
        sqlBuffer.append(" IN");

        addQueryForInClause(sqlBuffer, inParamCount);
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        AssertUtils.assertNull("DataSource MUST NOT BE null", this.dataSource);
        AssertUtils.assertNull("QuerySource Source MUST NOT BE null", this.querySource);
    }

    private <T> List<T> createObject(ResultSet rs, Class<T> entity, String... columns) throws SQLException {

        SQLBiFunction<ResultSet, Integer, T> creator = findCreator(entity, columns);

        List<T> l = new ArrayList<>();
        int i = 1;
        while (rs.next()) {
            l.add(creator.apply(rs, i++));
        }
        return l;
    }

    /**
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
    }

    /**
     * 쿼리 요청을 처리하고 결과를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * 2019. 6. 5.		박준홍			작업용 Connection 객체 생성 로직 수직
     * </pre>
     *
     * @param act
     *            {@link Connection}을 전달받아 요청쿼리를 처리하는 객체
     * @return 쿼리처리 결과.
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    private <T> T execute(SQLFunction<Connection, T> act) throws SQLException {

        Connection con = DataSourceUtils.getConnection(getDataSource());
        Connection conToWork = null;

        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        try {
            con.setAutoCommit(false);
            // (start) [BUG-FIX]: spring 5.x 부터 4.x에 존재하던 public NativeJdbcExtractor getNativeJdbcExtractor()
            // 를 제공함에 따라 호환성 지원 / Park_Jun_Hong_(fafanmama_at_naver_com): 2019. 6. 5. 오후 5:14:17
            conToWork = getConnection(con, jdbcTemplate);
            // (end): 2019. 6. 5. 오후 5:14:17

            conToWork.setAutoCommit(false);
            T r = act.apply(conToWork);

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
            msg.append("con-to-work=");
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
     * 요청쿼리를 실행하고 결과를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param broker
     *            요청쿼리와 쿼리 파라미터를 처리하는 객체
     * @param entity
     *            요청쿼리 처리 결과 데이타 모델
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과.
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @throws SQLException
     * 
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     * @see {@link ColumnDef}
     */
    private <T> List<T> executeQuery(ConnectionCallbackBroker broker, Class<T> entity, String... columns) throws SQLException {
        return execute(con -> {
            PreparedStatement pstmt = con.prepareStatement(broker.getQuery());

            IConnectionCallbackSetter setter = broker.getSetter();
            if (setter != null) {
                setter.set(pstmt);
            }

            ResultSet rs = pstmt.executeQuery();

            return createObject(rs, entity, columns);
        });
    }

    /**
     * 요청쿼리를 실행하고 결과를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 28.     박준홍         최초 작성
     * </pre>
     *
     * @param broker
     *            요청쿼리와 쿼리 파라미터를 처리하는 객체
     * @param entity
     *            요청쿼리 처리 결과 데이타 모델
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과.
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     * @throws SQLException
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    private <S, T> List<T> executeQuery(ConnectionCallbackBroker2<S> broker, Class<T> entity, String... columns) throws SQLException {
        return execute(con -> {
            PreparedStatement pstmt = con.prepareStatement(broker.getQuery());
            broker.set(pstmt);

            ResultSet rs = pstmt.executeQuery();

            return createObject(rs, entity, columns);
        });
    }

    /**
     * 단일/다중 (Insert/Update/Delete) 쿼리 요청을 처리한다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param brokers
     *            요청쿼리 처리 객체
     * @return 쿼리 처리결과.
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public Result<Integer> executeUpdate(ConnectionCallbackBroker... brokers) {
        return executeUpdate(Arrays.asList(brokers));
    }

    /**
     * 단일/다중 (Insert/Update/Delete) 쿼리 요청을 처리한다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param brokers
     *            요청쿼리 처리 객체
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    @SuppressWarnings("unchecked")
    public <T> Result<Integer> executeUpdate(ConnectionCallbackBroker2<T>... brokers) {

        Result<Integer> result = new Result<>();

        try {
            Integer updated = execute(con -> {
                DefaultConnectionCallback2<T> action = null;
                int inserted = 0;
                for (ConnectionCallbackBroker2<T> broker : brokers) {
                    action = new DefaultConnectionCallback2<T>(broker);
                    inserted += action.doInConnection(con);
                }

                return inserted;
            });

            result.andTrue().setData(updated);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 다중 (Insert/Update/Delete) 쿼리 요청을 처리한다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param brokers
     *            요청쿼리 처리 객체
     * @return 쿼리 처리결과
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public Result<Integer> executeUpdate(List<ConnectionCallbackBroker> brokers) {

        Result<Integer> result = new Result<>();

        try {
            Integer updated = execute(con -> {
                DefaultConnectionCallback action = null;
                int inserted = 0;
                for (ConnectionCallbackBroker broker : brokers) {
                    action = new DefaultConnectionCallback(broker);
                    inserted += action.doInConnection(con);
                }
                return inserted;
            });

            result.andTrue().setData(updated);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 단일 요청쿼리를 처리한다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @return 쿼리 처리결과
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public Result<Integer> executeUpdate(String query, IConnectionCallbackSetter setter) {
        return executeUpdate(new ConnectionCallbackBroker(query, setter));
    }

    /**
     * 단일 요청쿼리를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 29.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @return 쿼리 처리결과
     *
     * @since 2019. 3. 29.
     * @version _._._
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    @SuppressWarnings("unchecked")
    public Result<Integer> executeUpdate(String query, SQLConsumer<PreparedStatement> setter) {
        return executeUpdate(new DefaultConCallbackBroker2(query, setter));
    }

    /**
     * 데이타 타입에 맞는 객체 생성자를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param entity
     *            쿼리처리 결과 데이타 타입
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version _._._
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    @SuppressWarnings("unchecked")
    private <T> SQLBiFunction<ResultSet, Integer, T> findCreator(Class<T> entity, String... columns) {

        SQLBiFunction<ResultSet, Integer, T> creator = (SQLBiFunction<ResultSet, Integer, T>) CREATORS.get(entity.getName());

        if (creator == null) {
            creator = (rs, rowNum) -> {
                return SQLUtils.newInstance(entity, rs, columns);
            };
            CREATORS.put(entity.getName(), creator);
        }

        return creator;
    }

    /**
     * 작업용 Connection 객체를 제공한다.<br>
     * Springframework 5.x 부터 4.x에 존재하던 아래 메소드를 제거함에 따라 호환성 제공을 목적으로 한다.
     * 
     * <pre>
     * public NativeJdbcExtractor getNativeJdbcExtractor() {
     *     return this.nativeJdbcExtractor;
     * }
     * </pre>
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 6. 5.		박준홍			최초 작성
     * </pre>
     *
     * @param con
     * @param jdbcTemplate
     * @return
     * @throws SQLException
     *
     * @since 2019. 6. 5.
     * @version _._._
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    private Connection getConnection(Connection con, JdbcTemplate jdbcTemplate) throws SQLException {
        try {
            NativeJdbcExtractor nativeJdbcExtractor = jdbcTemplate.getNativeJdbcExtractor();
            if (nativeJdbcExtractor != null) {
                return nativeJdbcExtractor.getNativeConnection(con);
            } else {
                return CONN_CREATOR.apply(con, jdbcTemplate);
            }
        } catch (NoSuchMethodError e) {
            return CONN_CREATOR.apply(con, jdbcTemplate);
        }
    }

    /**
     * {@link DataSource}를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public DataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getJdbcTemplate()
     */
    @Override
    public JdbcTemplate getJdbcTemplate() {
        if (this.jdbcTemplate == null) {
            this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        }

        return this.jdbcTemplate;
    }

    /**
     * 데이터 조회 요청쿼리를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param entity
     *            결과 데이타 타입
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public <T> Result<List<T>> getList(String query, Class<T> entity, String... columns) {
        return getList(query, (IConnectionCallbackSetter) null, entity, columns);
    }

    /**
     * 데이터 조회 요청쿼리를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @param entity
     *            결과 데이타 타입
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     * 
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public <T> Result<List<T>> getList(String query, IConnectionCallbackSetter setter, Class<T> entity, String... columns) {

        Result<List<T>> result = new Result<>();

        try {
            List<T> list = executeQuery(new ConnectionCallbackBroker(query, setter), entity, columns);
            result.andTrue().setData(list);
        } catch (SQLException e) {
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 데이터 조회 요청쿼리를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            데이터 조회 요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체 <br>
     * @param entity
     *            결과 데이타 타입
     * @columns 요청쿼리 처리 결과에서 필요한 컬럼이름.
     *          <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * 
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public <T> Result<List<T>> getList(String query, SQLConsumer<PreparedStatement> setter, Class<T> entity, String... columns) {

        Result<List<T>> result = new Result<>();

        try {
            List<T> list = executeQuery(new DefaultConCallbackBroker2(query, setter), entity, columns);
            result.andTrue().setData(list);
        } catch (SQLException e) {
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 데이터 1개 요청쿼리를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            조회 요청쿼리
     * @param entity
     *            결과 데이타 타입
     * @param required
     *            필수여부
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public <T> Result<T> getObject(String query, Class<T> entity, boolean required, String... columns) {
        return getObject(query, null, entity, required, columns);
    }

    /**
     * 데이터 1개 요청쿼리를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            조회 요청쿼리
     * @param entity
     *            결과 데이타 타입
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public <T> Result<T> getObject(String query, Class<T> entity, String... columns) {
        return getObject(query, null, entity, false, columns);
    }

    /**
     * 데이터 1개 요청쿼리를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            조회 요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @param entity
     *            결과 데이타 타입
     * @param required
     *            필수 여부
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public <T> Result<T> getObject(String query, SQLConsumer<PreparedStatement> setter, Class<T> entity, boolean required, String... columns) {
        Result<T> result = new Result<>();

        try {
            List<T> list = executeQuery(new DefaultConCallbackBroker2(query, setter), entity, columns);

            switch (list.size()) {
                case 0:
                    if (required) {
                        throw new EmptyResultDataAccessException(1);
                    }
                    result.andTrue();
                    break;
                case 1:
                    result.setData(list.get(0));
                    break;
                default:
                    throw new IncorrectResultSizeDataAccessException(1, list.size());
            }
        } catch (SQLException e) {
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 데이터 1개 요청쿼리를 처리한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param query
     *            조회 요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @param entity
     *            결과 데이타 타입.
     * @param columns
     *            요청쿼리 처리 결과에서 필요한 컬럼이름.
     *            <li><b><code>entity</code></b> 모델의 메소드에 적용된 {@link ColumnDef#name()} 값들.
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public <T> Result<T> getObject(String query, SQLConsumer<PreparedStatement> setter, Class<T> entity, String... columns) {
        return getObject(query, setter, entity, false, columns);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuery(java.lang.String)
     */
    @Override
    public String getQuery(String name) {
        return this.querySource.getMessage(name, null, null);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuery(java.lang.String, java.lang.Object[], java.util.Locale)
     */
    @Override
    public String getQuery(String name, Object[] args, Locale locale) {
        return this.querySource.getMessage(name, args, locale);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuery(java.lang.String, java.lang.Object[], java.lang.String,
     *      java.util.Locale)
     */
    @Override
    public String getQuery(String name, Object[] args, String defaultMessage, Locale locale) {
        return this.querySource.getMessage(name, args, defaultMessage, locale);
    }

    /**
     * @see open.commons.spring.jdbc.dao.IGenericDao#getQuerySourece()
     */
    @Override
    public ReloadableResourceBundleMessageSource getQuerySourece() {
        return this.querySource;
    }

    /**
     * {@link DataSource} 객체를 설정한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param dataSource
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public abstract void setDataSource(DataSource dataSource);

    /**
     * 쿼리 정보 객체를 설정한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param querySource
     *
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public abstract void setQuerySource(ReloadableResourceBundleMessageSource querySource);

    /**
     * Invocation handler that suppresses close calls on JDBC Connections. Also prepares returned Statement
     * (Prepared/CallbackStatement) objects.
     * 
     * @see java.sql.Connection#close()
     */
    private class CloseSuppressingInvocationHandler implements InvocationHandler {

        private final Connection target;

        private JdbcTemplate jdbcTemplate;

        public CloseSuppressingInvocationHandler(Connection target, JdbcTemplate jdbcTemplate) {
            this.target = target;
            this.jdbcTemplate = jdbcTemplate;
        }

        private void applyStatementSettings(JdbcTemplate jdbcTemplate, Statement stmt) throws SQLException {
            int fetchSize = jdbcTemplate.getFetchSize();
            if (fetchSize > 0) {
                stmt.setFetchSize(fetchSize);
            }
            int maxRows = jdbcTemplate.getMaxRows();
            if (maxRows > 0) {
                stmt.setMaxRows(maxRows);
            }
            DataSourceUtils.applyTimeout(stmt, getDataSource(), jdbcTemplate.getQueryTimeout());
        }

        @SuppressWarnings("rawtypes")
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Invocation on ConnectionProxy interface coming in...

            if (method.getName().equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0]);
            } else if (method.getName().equals("hashCode")) {
                // Use hashCode of PersistenceManager proxy.
                return System.identityHashCode(proxy);
            } else if (method.getName().equals("unwrap")) {
                if (((Class) args[0]).isInstance(proxy)) {
                    return proxy;
                }
            } else if (method.getName().equals("isWrapperFor")) {
                if (((Class) args[0]).isInstance(proxy)) {
                    return true;
                }
            } else if (method.getName().equals("close")) {
                // Handle close method: suppress, not valid.
                return null;
            } else if (method.getName().equals("isClosed")) {
                return false;
            } else if (method.getName().equals("getTargetConnection")) {
                // Handle getTargetConnection method: return underlying Connection.
                return this.target;
            }

            // Invoke method on target Connection.
            try {
                Object retVal = method.invoke(this.target, args);

                // If return value is a JDBC Statement, apply statement settings
                // (fetch size, max rows, transaction timeout).
                if (retVal instanceof Statement) {
                    applyStatementSettings(jdbcTemplate, ((Statement) retVal));
                }

                return retVal;
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }

}
