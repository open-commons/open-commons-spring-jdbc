/*
 * Copyright 2021 Park Jun-Hong (parkjunhong77@gmail.com)
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
 * Date  : 2021. 11. 26. 오후 12:45:07
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import open.commons.core.Result;
import open.commons.core.annotation.ColumnDef;
import open.commons.core.annotation.ColumnValue;
import open.commons.core.database.ConnectionCallbackBroker2;
import open.commons.core.database.DefaultConCallbackBroker2;
import open.commons.core.database.annotation.TableDef;
import open.commons.core.function.SQLConsumer;
import open.commons.core.function.SQLTripleFunction;
import open.commons.core.test.StopWatch;
import open.commons.core.utils.ArrayUtils;
import open.commons.core.utils.ExceptionUtils;
import open.commons.core.utils.SQLUtils;
import open.commons.spring.jdbc.dao.DefaultConnectionCallback2;
import open.commons.spring.jdbc.exception.RuntimeDataAccessException;
import open.commons.spring.jdbc.view.AbstractGenericView;

/**
 * DBMS Table Entity에 기반하여 공통 기능을 제공하는 클래스.
 * 
 * @param <T>
 *            DBMS Table에 연결된 데이터 타입.
 * 
 * @since 2021. 11. 26.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 * 
 * @see TableDef
 * @see ColumnDef
 * @see ColumnValue
 */
public abstract class AbstractGenericRepository<T> extends AbstractGenericView<T> implements IGenericRepository<T> {

    /**
     * 1개의 데이터를 추가하는 쿼리.<br>
     * 패턴:
     * <code>INSERT INTO {table-name} ( {comma-separated-column-names} ) VALUES ( {comma-separated-question-marks} )</code>
     * 
     * @see #queryForInsert()
     */
    protected final String QUERY_FOR_INSERT;

    /**
     * 여러 개 데이터를 추가하는 쿼리 중 테이블 및 데이터 선언 관련 쿼리<br>
     * 
     * @see #queryForPartitionHeader()
     */
    protected final String QUERY_FOR_PARTITION_HEADER;
    /**
     * 여러 개 데이터를 추가하는 쿼리 중 데이터 연결(Variable Binding) 관련 쿼리<br>
     * 
     * @see #queryForPartitionValue()
     */
    protected final String QUERY_FOR_PARTITION_VALUE;
    /**
     * 여러 개 데이터를 추가하는 쿼리 중 데이터 연결(Variable Binding) 쿼리를 이어주는 쿼리<br>
     * 
     * @see #queryForPartitionConcatValue()
     */
    protected final String QUERY_FOR_PARTITION_CONCAT_VQ;

    /**
     * 여러 개 데이터를 추가하는 쿼리 중 마지막 쿼리<br>
     * 
     * @see #queryForPartitionTail()
     */
    protected final String QUERY_FOR_PARTITION_TAIL;

    /**
     * 데이터를 삭제하는 쿼리의 테이블 선언 관련 쿼리<br>
     * 패턴: <code>DELETE FROM {table-name}</code>
     * 
     * @see #queryForDeleteHeader()
     */
    protected final String QUERY_FOR_DELETE_HEADER;

    /**
     * 데이터를 변경하는 쿼리의 테이블 선언 관련 쿼리<br>
     * 패턴: <code>UPDATE {table-name}</code>
     * 
     * @see #queryForUpdateHeader()
     */
    protected final String QUERY_FOR_UPDATE_HEADER;

    /**
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 6.     박준홍         최초 작성
     * </pre>
     * 
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @since 2021. 12. 6.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #AbstractGenericRepository(Class, boolean, boolean)
     */
    public AbstractGenericRepository(@NotNull Class<T> entityType) {
        this(entityType, true, true);
    }

    /**
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     * 
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #AbstractGenericRepository(Class, boolean, boolean)
     */
    public AbstractGenericRepository(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        this(entityType, forceToPrimitive, true);
    }

    /**
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2023. 8. 24.     박준홍         최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     * @param ignoreNoDataMethod
     *            데이터를 제공하는 메소드가 없는 경우 로그 미발생 여부
     *
     * @since 2023. 8. 24.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractGenericRepository(@NotNull Class<T> entityType, boolean forceToPrimitive, boolean ignoreNoDataMethod) {
        super(entityType, forceToPrimitive, ignoreNoDataMethod);

        this.QUERY_FOR_INSERT = queryForInsert();

        this.QUERY_FOR_PARTITION_HEADER = queryForPartitionHeader();
        this.QUERY_FOR_PARTITION_VALUE = queryForPartitionValue();
        this.QUERY_FOR_PARTITION_CONCAT_VQ = queryForPartitionConcatValue();
        this.QUERY_FOR_PARTITION_TAIL = queryForPartitionTail();

        this.QUERY_FOR_DELETE_HEADER = queryForDeleteHeader();
        this.QUERY_FOR_UPDATE_HEADER = queryForUpdateHeader();

    }

    /**
     * 주어진 파라미터를 이용하여 생성한 데이터 변경 쿼리를 제공합니다. <br>
     * 패턴: <code>{query} SET {column} = {variable-binding-query} (, {column} = {variable-binding-query})*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     * 
     * @param queryHeader
     *            중요 쿼리
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String attachSetClause(String queryHeader) {

        List<ColumnValue> columns = getUpdatableColumnValues();

        if (columns.size() < 1) {
            throw new IllegalArgumentException(String.format("데이터를 변경할 컬럼 정보가 존재하지 않습니다. entity={}", this.entityType));
        }

        return String.join(" ", queryHeader, createSetClause(columns));
    }

    /**
     * 데이터 생성에 사용될 {@link ConnectionCallbackBroker2}를 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected final ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>> createBrokerForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs) {

        String query = createQueryForInsertOrNothing(data, method, whereArgs);
        Object params = createParametersForInsertOrNothing(data, method, whereArgs);
        SQLConsumer<PreparedStatement> setter = SQLConsumer.setParameters(params);

        logger.debug("query={}, data={}", query, params);

        return new DefaultConCallbackBroker2(query, setter, false);
    }

    /**
     * 데이터 생성에 사용될 {@link ConnectionCallbackBroker2}를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected final ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>> createBrokerForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs) {
        String query = createQueryForInsertOrUpdate(data, method, whereArgs);
        Object params = createParametersForInsertOrUpdate(data, method, whereArgs);
        SQLConsumer<PreparedStatement> setter = SQLConsumer.setParameters(params);

        logger.debug("query={}, data={}", query, params);

        return new DefaultConCallbackBroker2(query, setter, false);
    }

    /**
     * 컬럼에 값을 설정하는 쿼리를 제공합니다. <br>
     * 패턴: <code>{column} = {variable-binding-query} ( AND {column} = {variable-binding-query} )*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 1.     박준홍         최초 작성
     * 2022. 7. 14.     박준홍         반환 데이터 추가.
     * </pre>
     *
     * @param buf
     *            쿼리 버퍼
     * @param concat
     *            컬럼 설정쿼리 연결 문자열
     * @param columns
     *            컬럼 정보
     * 
     * @return 전달받은 쿼리 버퍼
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected StringBuffer createColumnAssignQueries(StringBuffer buf, String concat, List<ColumnValue> columns) {

        // variable binding
        Iterator<ColumnValue> itr = columns.iterator();
        buf.append(getAssignQuery(itr.next()));

        while (itr.hasNext()) {
            buf.append(" ");
            buf.append(concat);
            buf.append(" ");
            buf.append(getAssignQuery(itr.next()));
        }

        return buf;
    }

    /**
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @param data
     * @param partitionSize
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final <E> ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] createInsertBrokers(List<E> data, int partitionSize) {

        logger.debug("query.header={}, query.value={}, query.tail={}, data.size={}", QUERY_FOR_PARTITION_HEADER, QUERY_FOR_PARTITION_VALUE, QUERY_FOR_PARTITION_TAIL, data.size());

        return createConnectionCallbackBrokers(data, SQLTripleFunction.setParameters(), partitionSize //
                , QUERY_FOR_PARTITION_HEADER //
                , QUERY_FOR_PARTITION_VALUE //
                , QUERY_FOR_PARTITION_CONCAT_VQ //
                , QUERY_FOR_PARTITION_TAIL);
    }

    /**
     * 2개 테이블의 컬럼들을 매칭시키고, 각 매칭된 내용을 연결자를 이용해서 연결한 구문을 제공합니다.<br>
     * 
     * 예)
     * 
     * <pre>
     * - T.ID = SRC.ID AND T.CREDENTIAL = SRC.CREDENTIAL
     * - T.EMAIL = SRC.EMAIL, T.TITLE = SRC.TITLE
     * </pre>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 3.      박준홍         최초 작성
     * </pre>
     *
     * @param clmns
     *            컬럼 목록
     * @param one
     *            테이블 1
     * @param other
     *            테이블 2
     * @param concatenator
     *            각 컬럼 매칭을 연결할 문자.<br>
     *            컬럼 매칭 결과에 문자열 형태로 이어지기 때문에,<br>
     *            "연결자가 기호가 아니라 문자일 경우"<br>
     *            반드시 맨앞/맨뒤에 빈칸을 추가해야 함.
     * @return
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    private String createMergeAssembleColumnMatch(@NotNull Collection<String> clmns, @NotNull final String one, @NotNull final String other, @NotEmpty String concatenator) {
        return clmns.stream().map(clmn -> new StringBuilder().append(one).append(".").append(clmn) //
                .append(" = ") //
                .append(other).append(".").append(clmn).toString()) //
                .collect(Collectors.joining(concatenator));
    }

    /**
     * Primary Key가 중복되는 경우 데이터를 갱신하기 위한 구문을 생성해서 제공합니다. <br>
     * 
     * 참고 쿼리
     * 
     * <pre>
     * MERGE INTO TB_USER T
     * USING (
     *   SELECT 
     *     'user001' AS ID, 
     *     'cred_001' AS CREDENTIAL, 
     *     'manager' AS TITLE,
     *     'user001@newmail.com' AS EMAIL
     * ) AS SRC
     * ON (T.ID = SRC.ID AND T.CREDENTIAL = SRC.CREDENTIAL)
     * WHEN MATCHED THEN
     *   UPDATE SET T.EMAIL = SRC.EMAIL
     * WHEN NOT MATCHED THEN
     *   INSERT (ID, CREDENTIAL, TITLE, EMAIL)
     *   VALUES (SRC.ID, SRC.CREDENTIAL, SRC.TITLE, SRC.EMAIL);
     * </pre>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 2.      박준홍         최초 작성
     * </pre>
     *
     * @param clmns
     *            컬럼 목록
     * @param one
     *            테이블 1
     * @param other
     *            테이블 2
     * @return
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    protected String createMergeUpdateSetClause(@NotNull Collection<String> clmns, @NotNull final String one, @NotNull final String other) {
        return createMergeAssembleColumnMatch(clmns, one, other, ", ");
    }

    /**
     * Primary Key가 중복되는 경우 데이터를 갱신하기 위한 데이터 제공 구문을 생성해서 제공합니다. <br>
     * 
     * 참고 쿼리
     * 
     * <pre>
     * MERGE INTO TB_USER T
     * USING (
     *   SELECT 
     *     'user001' AS ID, 
     *     'cred_001' AS CREDENTIAL, 
     *     'manager' AS TITLE,
     *     'user001@newmail.com' AS EMAIL
     * ) AS SRC
     * ON (T.ID = SRC.ID AND T.CREDENTIAL = SRC.CREDENTIAL)
     * WHEN MATCHED THEN
     *   UPDATE SET T.EMAIL = SRC.EMAIL
     * WHEN NOT MATCHED THEN
     *   INSERT (ID, CREDENTIAL, TITLE, EMAIL)
     *   VALUES (SRC.ID, SRC.CREDENTIAL, SRC.TITLE, SRC.EMAIL);
     * </pre>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 2.      박준홍         최초 작성
     * </pre>
     *
     * @param clmns
     *            컬럼 목록
     * @param one
     *            비교 테이블 1
     * @param other
     *            비교 테이블 2
     * @return
     *
     * @since 2025. 4. 2.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    protected String createMergeUsingOnClause(@NotNull Collection<String> clmns, @NotNull final String one, @NotNull final String other) {
        return createMergeAssembleColumnMatch(clmns, one, other, " AND ");
    }

    /**
     * 데이터 생성에 사용될 파라미터를 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Object createParametersForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs) {
        return data;
    }

    /**
     * 데이터 생성에 사용될 파라미터를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Object createParametersForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs) {
        return data;
    }

    /**
     * 데이터 생성에 사용될 Query를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String createQueryForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs);

    /**
     * 데이터 생성에 사용될 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     *
     * @since 2022. 11. 29.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String createQueryForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs);

    /**
     * 주어진 컬럼값을 변경하는 'Set' 구문을 제공합니다. <br>
     * 패턴: <code>SET {column} = ? (, {column} = ? )*</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param columns
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected String createSetClause(@NotNull List<ColumnValue> columns) {

        StringBuffer buf = new StringBuffer();

        if (columns.size() > 1) {
            buf.append("SET");
            buf.append(" ");

            createColumnAssignQueries(buf, ",", columns);
        }

        return buf.toString();
    }

    /**
     * 데이터를 삭제합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @param <V>
     * @param method
     *            사용자 정의 메소드 정보
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     * @throws RuntimeDataAccessException
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> deleteBy(@NotNull Method method, Object... whereArgs) throws RuntimeDataAccessException {
        String query = attachWhereClause(QUERY_FOR_DELETE_HEADER, method, whereArgs);

        logger.debug("Query: {}, data={}", query, Arrays.toString(whereArgs));

        return executeUpdate(query, SQLConsumer.setParameters(whereArgs));
    }

    /**
     * 데이터를 삭제합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 3.        박준홍         최초 작성
     * </pre>
     *
     * @param <V>
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     * @return
     * @throws RuntimeDataAccessException
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> deleteBy(Object... whereArgs) throws RuntimeDataAccessException {
        return deleteBy(getCurrentMethod(1, whereArgs), whereArgs);
    }

    /**
     * 단일/다중 (Insert/Update/Delete) 쿼리 요청을 처리합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 28.     박준홍         최초 작성
     * </pre>
     *
     * @param brokers
     *            요청쿼리 처리 객체
     * @return 쿼리 처리결과
     *         <ul>
     *         <li>&lt;T&gt; 요청받을 데이타 타입
     *         </ul>
     * @throws RuntimeDataAccessException
     * 
     * @since 2019. 3. 28.
     * @version 0.1.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    @SafeVarargs
    public final <E> Result<Integer> executeUpdate(@NotNull ConnectionCallbackBroker2<E>... brokers) throws RuntimeDataAccessException {

        Result<Integer> result = new Result<>();

        StopWatch watch = new StopWatch();
        watch.start();

        Integer updated = 0;
        try {
            updated = execute(con -> {
                int pos = 0;
                DefaultConnectionCallback2<E> action = null;
                int total = 0;
                int inserted = 0;
                try {
                    for (ConnectionCallbackBroker2<E> broker : brokers) {
                        action = new DefaultConnectionCallback2<E>(broker);
                        inserted = action.doInConnection(con);
                        total += inserted;

                        watch.record("inserted");
                        logger.trace("Data.count: {}, Elapsed.{}: {}", inserted, inserted, watch.getAsPretty("inserted"));

                        pos++;
                    }
                } catch (SQLException e) {
                    logger.error("data.pos={}, cause={}", pos, e.getMessage(), e);
                    throw e;
                }

                return total;
            });

            result.andTrue().setData(updated);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeDataAccessException("", e);
        } finally {
            watch.stop();
            logger.trace("Data.count: {}, Elapsed.total: {}", updated, watch.getAsPretty());
        }

        return result;
    }

    /**
     * 다수 개의 데이터를 설정된 크기로 나누어 추가합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 17.     박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param dataSetter
     *            다수 개의 객체형 파라미터를 {@link PreparedStatement}에 설정하는 기능.<br>
     *            참조: {@link SQLTripleFunction#setParameters(String...)}
     * @param partitionSize
     *            데이터 분할 크기
     * @param valueQuery
     *            데이터 바인딩 쿼리
     * @return
     *
     * @since 2020. 1. 17.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @see SQLTripleFunction#setParameters(String...)
     */
    public <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String valueQuery) {
        // !!! 세부 기능을 구현해야 합니다. !!!
        throw new UnsupportedOperationException("세부 기능을 구현해야 합니다.");
    }

    /**
     * 다수 개의 데이터를 설정된 크기로 나누어 추가합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 11.        박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     * @param data
     *            추가할 데이터
     * @param dataSetter
     *            다수 개의 객체형 파라미터를 {@link PreparedStatement}에 설정하는 기능.<br>
     *            참조: {@link SQLTripleFunction#setParameters(String...)}
     * @param partitionSize
     *            데이터 분할 크기
     * @param headerQuery
     *            데이터 추가 헤더 쿼리. (테이블 및 컬럼 정보가 기재됨)
     * @param valueQuery
     *            데이터 바인딩 쿼리
     * @return
     *
     * @since 2021. 11. 11.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String headerQuery, @NotNull String valueQuery) {
        // !!! 세부 기능을 구현해야 합니다. !!!
        throw new UnsupportedOperationException("세부 기능을 구현해야 합니다.");
    }

    /**
     * 다수 개의 데이터를 설정된 크기로 나누어 추가합니다. <br>
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 20.     박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     *            데이터 타입.
     * @param data
     *            추가하려는 데이타.
     * @param dataSetter
     *            객체 데이터를 {@link PreparedStatement}에 추가하는 주체.<br>
     *            참조: {@link SQLTripleFunction#setParameters(String...)}
     * @param partitionSize
     *            데이터 분할 크기
     * @param headerQuery
     *            여러개 데이터 추가용 쿼리 헤더, INSERT ... 이 포함된 구문이 설정됨.
     * @param valueQuery
     *            여러개 데이터 바인딩용 쿼리. (?, ?, ...) 이 포함된 구문이 설정됨.
     * @param tailQuery
     *            쿼리 마지막
     * @return
     * @throws RuntimeDataAccessException
     *
     * @since 2020. 1. 20.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @see SQLTripleFunction#setParameters(String...)
     */
    public final <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String headerQuery, @NotNull String valueQuery, String tailQuery) throws RuntimeDataAccessException {
        return executeUpdate(data, dataSetter, partitionSize, headerQuery, valueQuery, "", tailQuery);
    }

    /**
     * 다수 개의 데이터를 설정된 크기로 나누어 추가합니다. <br>
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 6. 15.     박준홍         최초 작성
     * </pre>
     *
     * @param <E>
     *            데이터 타입.
     * @param data
     *            추가하려는 데이타.
     * @param dataSetter
     *            객체 데이터를 {@link PreparedStatement}에 추가하는 주체.<br>
     *            참조: {@link SQLTripleFunction#setParameters(String...)}
     * @param partitionSize
     *            데이터 분할 크기
     * @param headerQuery
     *            여러개 데이터 추가용 쿼리 헤더, INSERT ... 이 포함된 구문이 설정됨.
     * @param valueQuery
     *            여러개 데이터 바인딩용 쿼리. (?, ?, ...) 이 포함된 구문이 설정됨.
     * @param concatForVQ
     *            바인딩용 쿼리 연결자
     * @param tailQuery
     *            쿼리 마지막
     * @return
     * @throws RuntimeDataAccessException
     *
     * @since 2020. 6. 15.
     * @version 0.2.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * 
     * @see SQLTripleFunction#setParameters(String...)
     */
    public final <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String headerQuery, @NotNull String valueQuery, String concatForVQ, String tailQuery) throws RuntimeDataAccessException {
        ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] brokers = createConnectionCallbackBrokers(data, dataSetter, partitionSize, headerQuery, valueQuery, concatForVQ,
                tailQuery);
        return executeUpdate(brokers);
    }

    /**
     * 단일 요청쿼리를 처리합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 3. 29.     박준홍         최초 작성
     * </pre>
     *
     * @param query
     *            요청쿼리
     * @param setter
     *            요청쿼리 파라미터 설정 객체
     * @return 쿼리 처리결과
     * @throws RuntimeDataAccessException
     *
     * @since 2019. 3. 29.
     * @version 0.0.6
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     */
    public Result<Integer> executeUpdate(@NotNull String query, SQLConsumer<PreparedStatement> setter) throws RuntimeDataAccessException {
        return executeUpdate(query, setter, false);
    }

    public Result<Integer> executeUpdate(@NotNull String query, SQLConsumer<PreparedStatement> setter, boolean forStoredProcedure) throws RuntimeDataAccessException {
        return executeUpdate(new DefaultConCallbackBroker2(query, setter, forStoredProcedure));
    }

    /**
     * 컬럼에 값을 설정하는 쿼리를 제공합니다. <br>
     * 패턴: <code>{column-name} = {variable-binding-query}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 1.     박준홍         최초 작성
     * </pre>
     *
     * @param cv
     * @return
     *
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final String getAssignQuery(@NotNull ColumnValue cv) {
        return String.join(" = ", getColumnName(cv), cv.variableBinding());
    }

    /**
     * {@link ColumnValue#name()}이 기본값 (빈 문자열)일 경우를 {@link Method#getName()}값을 이용하여 컬럼명을 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 25.        박준홍         최초 작성
     * 2025. 4. 2           박준홍         DBMS Reserved Keyword 검증 적용
     * </pre>
     *
     * @param clmnValue
     * @return
     *
     * @since 2022. 11. 25.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    private String getColumnName(@NotNull ColumnValue clmnValue) {

        Optional<Method> opt = getUpdatableColumnsAsStream().filter(m -> clmnValue.equals(m.getAnnotation(ColumnValue.class))).findAny();

        if (opt.isPresent()) {
            return SQLUtils.getColumnName(clmnValue, opt.get());
        } else {
            throw ExceptionUtils.newException(IllegalArgumentException.class, "'%s'에 해당하는 Method가 존재하지 않습닏.", clmnValue);
        }
    }

    /**
     * 여러 개의 데이터를 저장하는 경우 한번에 저장할 데이터 개수를 반환합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected int getPartitionSize() {
        return 100;
    }

    /**
     * 변경 대상인 컬럼 목록을 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final List<String> getUpdatableColumnNames() {
        return getUpdatableColumnsAsStream().map(m -> SQLUtils.getColumnNameByColumnValue(m)).collect(Collectors.toList());
    }

    /**
     * 변경 대상인 컬럼 데이터를 제공하는 {@link Method} 목록을 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final List<Method> getUpdatableColumns() {
        return getUpdatableColumnsAsStream() //
                .collect(Collectors.toList());
    }

    /**
     * 변경 대상인 컬럼 데이터를 제공하는 {@link Method} 목록을 {@link Stream}으로 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 14.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 12. 14.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final Stream<Method> getUpdatableColumnsAsStream() {
        return getColumnMethods().stream() //
                .filter(m -> m.getAnnotation(ColumnValue.class).updatable());
    }

    /**
     * 변경 대상인 컬럼 정보를 {@link Iterator}형태로 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 1.     박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected final List<ColumnValue> getUpdatableColumnValues() {
        return getUpdatableColumnsAsStream().map(m -> m.getAnnotation(ColumnValue.class)) //
                .collect(Collectors.toList());
    }

    /**
     * 데이터 갱신에 사용될 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 1.     박준홍         최초 작성
     * </pre>
     *
     * @param data
     * @return
     *
     * @since 2021. 12. 1.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected Object[] getUpdateParameters(T data) {
        return getUpdatableColumns().stream() //
                .map(m -> {
                    try {
                        return m.invoke(data);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        String errMsg = String.format("'%s' 정보를 변경하기 위한 정보를 생성 도중 에러가 발생하였습니다. 원인=%s", data.getClass(), e.getMessage());
                        logger.error(errMsg, e);
                        throw new UnsupportedOperationException(errMsg, e);
                    }
                }).toArray();
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#insert(java.util.List)
     */
    @Override
    public Result<Integer> insert(List<T> data) {
        return insert(data, getPartitionSize());
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#insert(java.util.List, int)
     */
    @Override
    public Result<Integer> insert(List<T> data, @Min(1) int partitionSize) {
        ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>>[] brokers = createInsertBrokers(data, partitionSize);
        return executeUpdate(brokers);
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.IGenericRepository#insert(java.lang.Object)
     */
    @Override
    public Result<Integer> insert(T data) {

        logger.debug("query={}, data={}", QUERY_FOR_INSERT, data);

        return executeUpdate(QUERY_FOR_INSERT, SQLConsumer.setParameters(data));
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 아무런 동작을 하지 않습니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 2.     박준홍         최초 작성
     * </pre>
     *
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     * @throws RuntimeDataAccessException
     *
     * @since 2022. 11. 2.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> insertOrNothingBy(T data, @NotNull Method method, Object... whereArgs) throws RuntimeDataAccessException {
        ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>> broker = createBrokerForInsertOrNothing(data, method, whereArgs);
        return executeUpdate(broker);
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 아무런 동작을 하지 않습니다.<br>
     * 
     * 파라미터 중에 2번째({@link Object} ...)은 DBMS에 따라 구현할 때 사용되지 않을 수도 있습니다.
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 2.     박준홍         최초 작성
     * </pre>
     *
     * @param data
     *            추가할 데이터
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     * @throws RuntimeDataAccessException
     *
     * @since 2022. 11. 2.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> insertOrNothingBy(T data, Object... whereArgs) throws RuntimeDataAccessException {
        return insertOrNothingBy(data, getCurrentMethod(1, ArrayUtils.objectArray(data, whereArgs)), whereArgs);
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 아무런 동작을 하지 않습니다.<br>
     * 
     * 파라미터 중에 2번째({@link Method}), 3번째({@link Object} ...)은 DBMS에 따라 구현할 때 사용되지 않을 수도 있습니다.
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 7. 13.     박준홍         최초 작성
     * </pre>
     *
     * @param data
     *            추가할 데이터
     * @param method
     *            구현 클래스의 {@link Method}
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     * @throws RuntimeDataAccessException
     *
     * @since 2022. 7. 13.
     * @version 2.0.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> insertOrUpdateBy(T data, @NotNull Method method, Object... whereArgs) throws RuntimeDataAccessException {
        ConnectionCallbackBroker2<SQLConsumer<PreparedStatement>> broker = createBrokerForInsertOrUpdate(data, method, whereArgs);
        return executeUpdate(broker);
    }

    /**
     * 데이터를 추가하거나 이미 존재하는 경우 설정된 데이터를 갱신합니다. <br>
     * 
     * 파라미터 중에 2번째({@link Object} ...)은 DBMS에 따라 구현할 때 사용되지 않을 수도 있습니다.
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 7. 14.     박준홍         최초 작성
     * </pre>
     *
     * @param data
     *            추가할 데이터
     * @param whereArgs
     *            쿼리 Where 절 파라미터
     * @return
     * @throws RuntimeDataAccessException
     *
     * @since 2022. 7. 14.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected Result<Integer> insertOrUpdateBy(T data, Object... whereArgs) throws RuntimeDataAccessException {
        return insertOrUpdateBy(data, getCurrentMethod(1, ArrayUtils.objectArray(data, whereArgs)), whereArgs);
    }

    /**
     * 데이터를 삭제하는 쿼리의 테이블 선언 관련 쿼리를 제공합니다.<br>
     * 패턴: <code>DELETE FROM {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #queryForDeleteHeader(String)
     */
    protected String queryForDeleteHeader() {
        return queryForDeleteHeader(this.tableName);
    }

    /**
     * 데이터를 삭제하는 쿼리의 테이블 선언 관련 쿼리를 제공합니다.<br>
     * 패턴: <code>DELETE FROM {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
     * </pre>
     *
     * @param tableName
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForDeleteHeader(String tableName) {
        return new StringBuffer() //
                .append("DELETE FROM") //
                .append(" ") //
                .append(tableName) //
                .toString();
    }

    /**
     * 1개의 데이터를 추가하는 쿼리를 제공합니다.<br>
     * 패턴:
     * <code>INSERT INTO {table-name} ( {comma-separated-column-names} ) VALUES ( {comma-separated-question-marks} )</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see #queryForInsert(String)
     */
    protected String queryForInsert() {
        return queryForInsert(this.tableName);
    }

    /**
     * 1개의 데이터를 추가하는 쿼리를 제공합니다.<br>
     * 패턴:
     * <code>INSERT INTO {table-name} ( {comma-separated-column-names} ) VALUES ( {comma-separated-question-marks} )</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
     * </pre>
     *
     * @param tableName
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForInsert(String tableName) {
        return new StringBuffer() //
                .append("INSERT INTO") //
                .append(" ") //
                .append(tableName) //
                .append(" (")//
                .append(queryForColumnNames()) //
                .append(") ") //
                .append("VALUES") //
                .append(" (")//
                .append(queryForVariableBinding()) //
                .append(") ") //
                .toString();
    }

    /**
     * 여러 개의 데이터를 추가하는 쿼리의 '데이터 쿼리' 연결 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String queryForPartitionConcatValue();

    /**
     * 여러 개의 데이터를 추가하는 쿼리의 '헤더' 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String queryForPartitionHeader();

    /**
     * 여러 개의 데이터를 추가하는 쿼리의 '마지막' 쿼리/정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String queryForPartitionTail();

    /**
     * 여러 개의 데이터를 추가하는 쿼리의 '데이터' 쿼리를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    protected abstract String queryForPartitionValue();

    /**
     * 데이터를 변경하는 쿼리의 테이블 선언 관련 쿼리를 제공합니다.<br>
     * 패턴: <code>UPDATE {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * 2022. 11. 15.        박준홍     내구 구현을 {@link #queryForUpdateHeader(String)}를 이용.
     * </pre>
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * @see #queryForUpdateHeader(String)
     */
    protected String queryForUpdateHeader() {
        return queryForUpdateHeader(this.tableName);
    }

    /**
     * 데이터를 변경하는 쿼리의 테이블 선언 관련 쿼리를 제공합니다.<br>
     * 패턴: <code>UPDATE {table-name}</code>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2022. 11. 15.        박준홍         최초 작성
     * </pre>
     *
     * @param tableName
     * @return
     *
     * @since 2022. 11. 15.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     */
    protected String queryForUpdateHeader(@NotEmpty String tableName) {
        return new StringBuffer() //
                .append("UPDATE") //
                .append(" ") //
                .append(tableName) //
                .toString();
    }

    /**
     * 주어진 조건에 맞는 데이터를 갱신합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 29.        박준홍         최초 작성
     * </pre>
     * 
     * @param data
     *            변경할 데이터 정보.
     * @param method
     *            사용자 정의 메소드 정보
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     *
     * @since 2021. 11. 29.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<Integer> updateBy(T data, @NotNull Method method, Object... whereArgs) {
        String querySet = attachSetClause(QUERY_FOR_UPDATE_HEADER);
        String query = attachWhereClause(querySet, method, whereArgs);
        Object[] params = ArrayUtils.objectArray(getUpdateParameters(data), whereArgs);

        logger.debug("Query: {}, data={}", query, Arrays.toString(params));

        return executeUpdate(query, SQLConsumer.setParameters(params));
    }

    /**
     * 주어진 조건에 맞는 데이터를 갱신합니다. <br>
     * 이 메소드({@link #updateBy(Object, Object...)})를 호출하는 메소드의 파라미터는 이 메소드의 파라미터 순서와 동일한 순서(갱신할 데이터, Where ...)이어야 합니다.
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 12. 3.     박준홍         최초 작성
     * </pre>
     * 
     * @param data
     *            변경할 데이터 정보.
     * @param whereArgs
     *            'WHERE' 절에 사용될 파라미터.
     *
     * @return
     * @throws RuntimeDataAccessException
     *
     * @since 2021. 12. 3.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    protected Result<Integer> updateBy(T data, Object... whereArgs) throws RuntimeDataAccessException {
        return updateBy(data, getCurrentMethod(1, ArrayUtils.objectArray(data, whereArgs)), whereArgs);
    }

}
