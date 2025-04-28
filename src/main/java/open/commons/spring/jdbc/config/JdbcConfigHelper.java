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
 * Date  : 2025. 4. 28. 오후 1:29:42
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import open.commons.core.text.NamedTemplate;
import open.commons.core.utils.ArrayUtils;
import open.commons.core.utils.ExceptionUtils;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Spring JDBC 기능을 사용하는데 도움을 주는 기능 정의.
 * 
 * @since 2025. 4. 28.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class JdbcConfigHelper {

    private static Logger logger = LoggerFactory.getLogger(JdbcConfigHelper.class);

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 28.		박준홍			최초 작성
     * </pre>
     *
     *
     * @since 2025. 4. 28.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public JdbcConfigHelper() {
    }

    /**
     * {@link DataSource} 정보를 생성해서 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 28.     박준홍         최초 작성
     * </pre>
     *
     * @param <D>
     * @param dataSourceType
     *            데이터 소스 유형.
     * @return
     *
     * @since 2025. 4. 28.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public static <D extends DataSource> D createDataSource(Class<D> dataSourceType) {
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    /**
     * {@link HikariDataSource}를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 28.     박준홍         최초 작성
     * </pre>
     *
     * @param <D>
     * @return
     *
     * @since 2025. 4. 28.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public static HikariDataSource createHikariDataSource() {
        return createDataSource(HikariDataSource.class);
    }

    /**
     * 다중 DBMS 접속을 위한 연결정보를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 12. 3.     박준홍         최초 작성
     * 2025. 4. 28.     박준홍         {@link ConfigUtils#getMultipleDataSource(MultipleDataSourceConfig)} 에서 이관함.
     * </pre>
     *
     * @param config
     * @return {@link HikariDataSource} 객체
     *
     * @since 2020. 12. 3.
     * @version 0.3.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * @see HikariDataSource
     */
    public static Collection<DataSource> getMultipleDataSource(MultipleDataSourceConfig config) {
        return config.getJdbcUrls().stream()//
                .map(jdbcUrl -> {
                    HikariDataSource ds = createHikariDataSource();

                    ds.setUsername(config.getUsername());
                    ds.setPassword(config.getPassword());
                    ds.setMinimumIdle(config.getMininumIdle());
                    ds.setMaximumPoolSize(config.getMaximumPoolSize());
                    ds.setPoolName(config.getPoolName());
                    ds.setIdleTimeout(config.getIdleTimeout());
                    ds.setConnectionTimeout(config.getConnectionTimeout());
                    ds.setValidationTimeout(config.getValidationtimeout());
                    ds.setMaxLifetime(config.getMaxLifetime());
                    ds.setAutoCommit(config.isAutoCommit());
                    ds.setJdbcUrl(jdbcUrl);

                    return ds;
                }) //
                .collect(Collectors.toList());
    }

    /**
     * 주어진 정보를 이용하여 DBMS 초기 구성을 진행합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 28.     박준홍         최초 작성
     * </pre>
     *
     * @param dataSource
     *            DBMS 연결 정보
     * @param initResources
     *            DB 테이블 초기화 정보 (schema, sql, ...)
     * @param properties
     *            DB 테이블 초기화 정보에 반영되는 정보.
     * @return
     * @throws IOException
     *
     * @since 2025. 4. 28.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public static DataSourceInitializer initializeDbmsDefaultData(DataSource dataSource, DatabaseInitResources initResources, Map<String, Object> properties) throws IOException {

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        // 기본 테이블 생성
        Resource[] schemaResources = initResources.getSchemaResources().stream().map(schema -> {
            try {
                return updateInitResource(schema, properties);
            } catch (IOException e) {
                throw ExceptionUtils.newException(RuntimeException.class, e, "Schema SQL 처리 도중 오류가 발생하였습니다. schema=%s", schema);
            }
        }).toArray(Resource[]::new);

        // 기본 데이터 생성
        Resource[] dataResources = initResources.getDataResources().toArray(new Resource[0]);

        populator.addScripts(ArrayUtils.merge(schemaResources, dataResources));

        DataSourceInitializer dsInit = new DataSourceInitializer();
        dsInit.setDataSource(dataSource);
        dsInit.setDatabasePopulator(populator);

        return dsInit;

    }

    /**
     * SQL에 새로운 정보를 반영한 결과를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 28.     박준홍         최초 작성
     * </pre>
     *
     * @param resource
     *            SQL 정보
     * @param properties
     *            SQL에 반영할 내용.
     * @return
     * @throws IOException
     *
     * @since 2025. 4. 28.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    private static Resource updateInitResource(@NotNull Resource resource, @NotNull Map<String, Object> properties) throws IOException {

        String sqlResource;
        try (InputStream in = resource.getInputStream()) {
            sqlResource = IOUtils.toString(in, StandardCharsets.UTF_8);
        }

        final NamedTemplate sqlTemplate = new NamedTemplate(sqlResource);
        properties.forEach((k, v) -> sqlTemplate.addValue(k, v));

        String sql = sqlTemplate.format();

        logger.debug("sql=\n{}", sql);

        return new InputStreamResource(new ByteArrayInputStream(sql.getBytes(StandardCharsets.UTF_8)));
    }

}
