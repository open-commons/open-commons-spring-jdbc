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
 * Date  : 2025. 4. 1. 오후 4:24:34
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository.h2;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository;

/**
 * H2 DB 연동을 위한 클래스.
 * 
 * @since 2025. 4. 1.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class AbstractH2SingleDataSourceRepository<T> extends AbstractSingleDataSourceRepository<T> {

    protected final String QUERY_FOR_OFFSET = "LIMIT ?, ?";

    /**
     * PK가 충돌시
     */
    protected final String QUERY_FOR_INSERT_IGNORE;

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 1.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2SingleDataSourceRepository(@NotNull Class<T> entityType) {
        this(entityType, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 1.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper Class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2SingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive) {
        this(entityType, forceToPrimitive, true);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 1.		박준홍			최초 작성
     * </pre>
     *
     * @param entityType
     *            DBMS Table에 연결된 데이터 타입.
     * @param forceToPrimitive
     *            Wrapper class인 경우 Primitive 타입으로 강제로 변환할지 여부.
     * @param ignoreNoDataMethod
     *            데이터를 제공하는 메소드가 없는 경우 로그 미발생 여부
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2SingleDataSourceRepository(@NotNull Class<T> entityType, boolean forceToPrimitive, boolean ignoreNoDataMethod) {
        super(entityType, forceToPrimitive, ignoreNoDataMethod);

        // Primary Key 확인
        List<String> pkColumns = getPrimaryKeys();
        if (pkColumns == null || pkColumns.size() < 1) {
            logger.warn("'{}' 테이블에 Primary Key가 설정되지 않았습니다. insertOrNothingBy 또는 insertOrUpdateBy 메소드가 오류가 발생할 수 있습니다.", this.tableName);
            this.QUERY_FOR_INSERT_IGNORE = null;
        } else {
            this.QUERY_FOR_INSERT_IGNORE = new StringBuilder() //
                    .append("INSERT INTO") //
                    .append(" ") //
                    .append(this.tableName) //
                    .append(" (") //
                    .append(queryForColumnNames()) //
                    .append(") ") //
                    .append("SELECT ") //
                    .append(queryForVariableBinding()) //
                    .append(" WHERE NOT EXISTS (") //
                    .append(" SELECT 1 FROM ") //
                    .append(this.tableName) //
                    .append(" WHERE ") //
                    .append(String.join(" AND ", pkColumns.stream()//
                            .map(c -> new StringBuilder(c).append(" = ?").toString()) //
                            .collect(Collectors.toList())) //
                    ) //
                    .toString();
        }
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createQueryForInsertOrNothing(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected String createQueryForInsertOrNothing(T data, @NotNull Method method, Object... whereArgs) {
        return null;
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#createQueryForInsertOrUpdate(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    protected String createQueryForInsertOrUpdate(T data, @NotNull Method method, Object... whereArgs) {
        return null;
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForOffset(int, int)
     */
    @Override
    protected String queryForOffset(@Min(0) int offset, @Min(1) int limit) {
        return null;
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionConcatValue()
     */
    @Override
    protected String queryForPartitionConcatValue() {
        return null;
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionHeader()
     */
    @Override
    protected String queryForPartitionHeader() {
        return null;
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionTail()
     */
    @Override
    protected String queryForPartitionTail() {
        return null;
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractGenericRepository#queryForPartitionValue()
     */
    @Override
    protected String queryForPartitionValue() {
        return null;
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository#setDataSource(javax.sql.DataSource)
     */
    @Override
    public void setDataSource(@NotNull DataSource dataSource) {
    }

    /**
     *
     * @since 2025. 4. 1.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#setQuerySource(org.springframework.context.support.ReloadableResourceBundleMessageSource)
     */
    @Override
    public void setQuerySource(@NotNull ReloadableResourceBundleMessageSource querySource) {
    }

}
