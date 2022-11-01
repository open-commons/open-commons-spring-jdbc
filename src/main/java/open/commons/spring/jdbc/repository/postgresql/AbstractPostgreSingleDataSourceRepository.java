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

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import open.commons.core.Result;
import open.commons.core.annotation.ColumnValue;
import open.commons.core.function.SQLConsumer;
import open.commons.core.utils.AnnotationUtils;
import open.commons.core.utils.SQLUtils;
import open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository;

/**
 * PostgreSQL 연동을 위한 클래스.
 * 
 * @since 2021. 11. 26.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
public abstract class AbstractPostgreSingleDataSourceRepository<T> extends AbstractSingleDataSourceRepository<T> {

    protected final String QUERY_FOR_OFFSET = "OFFSET ? LIMIT ?";

    /**
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
     * @see #AbstractPostgreSingleDataSourceRepository(Class, boolean)
     */
    public AbstractPostgreSingleDataSourceRepository(@NotNull Class<T> entityType) {
        this(entityType, true);
    }

    /**
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper Class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractPostgreSingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        super(entityType, forceToPrimitive);
    }

    /**
     * 
     * @since 2022. 7. 14.
     * @version 0.4.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#insertOrUpdateBy(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected Result<Integer> insertOrUpdateBy(T data, @NotNull Method method, Object... whereArgs) {

        // #1. 데이터 변경 쿼리 생성
        List<String> updateClmns = getUpdatableColumnNames().stream() // 업데이트 가능한 컬럼 도출
                .collect(Collectors.toList()) //
        ;

        StringBuilder queryBuf = new StringBuilder();
        queryBuf.append(QUERY_FOR_INSERT);

        if (!updateClmns.isEmpty()) {
            // 'CONFLICT' 여부 확인
            List<String> primaryKeys = AnnotationUtils.getAnnotatedMethodsAllAsStream(data.getClass(), ColumnValue.class) //
                    .filter(m -> m.getAnnotation(ColumnValue.class).primaryKey()) //
                    .map(m -> SQLUtils.getColumnName(m)) //
                    .collect(Collectors.toList()) //
            ;

            if (!primaryKeys.isEmpty()) {
                queryBuf.append(" ");
                queryBuf.append("ON CONFLICT (");
                queryBuf.append(String.join(",", primaryKeys));
                queryBuf.append(") ");
                queryBuf.append("DO UPDATE SET (");
                queryBuf.append(String.join(",", updateClmns));
                queryBuf.append(") = ROW(");
                queryBuf.append(String.join(",", updateClmns.stream().map(clmn -> String.join(".", "excluded", clmn)).collect(Collectors.toList())));
                queryBuf.append(")");
            }
        }

        logger.debug("query={}, data={}", queryBuf.toString(), data);

        return executeUpdate(queryBuf.toString(), SQLConsumer.setParameters(data));
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForOffset(int, int)
     */
    @Override
    protected String queryForOffset(int offset, int limit) {
        return QUERY_FOR_OFFSET;
    }

    /**
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionConcatValue()
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
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionHeader()
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
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionTail()
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
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionValue()
     */
    @Override
    protected String queryForPartitionValue() {
        return new StringBuilder().append("( ").append(queryForVariableBinding()).append(" )").toString();
    }
}
