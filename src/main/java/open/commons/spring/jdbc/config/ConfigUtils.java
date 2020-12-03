/*
 * Copyright 2020 Park Jun-Hong_(parkjunhong77/google/com)
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
 * Date  : 2020. 12. 3. 오후 3:54:29
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.spring.jdbc.config;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;

import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @since 2020. 12. 3.
 * @version 0.3.0
 * @author Park_Jun_Hong_(fafanmama_at_naver_com)
 */
public class ConfigUtils {

    /**
     * 
     * @since 2020. 12. 3.
     */
    private ConfigUtils() {
    }

    /**
     * 다중 DBMS 접속을 위한 연결정보를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @param config
     * @return
     *
     * @since 2020. 12. 3.
     * @version 0.3.0
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     */
    public static Collection<DataSource> getMultipleDataSource(MultipleDataSourceConfig config) {
        return config.getJdbcUrls().stream()//
                .map(jdbcUrl -> {
                    HikariDataSource ds = DataSourceBuilder.create().type(HikariDataSource.class).build();

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
}
