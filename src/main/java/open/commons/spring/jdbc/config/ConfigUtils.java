/*
 * Copyright 2020 Park Jun-Hong (parkjunhong77@gmail.com)
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
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.spring.jdbc.config;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @since 2020. 12. 3.
 * @version 0.3.0
 * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
 * @deprecated Use {@link JdbcConfigHelper}. All functions are tranfered. SHOULD BE REMOVED next.
 */
public class ConfigUtils {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

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
     * @return {@link HikariDataSource} 객체
     *
     * @since 2020. 12. 3.
     * @version 0.3.0
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * @see HikariDataSource
     * 
     * @deprecated Use {@link JdbcConfigHelper#getMultipleDataSource(MultipleDataSourceConfig)}
     */
    public static Collection<DataSource> getMultipleDataSource(MultipleDataSourceConfig config) {
        return config.getJdbcUrls().stream()//
                .map(jdbcUrl -> {
                    HikariDataSource ds = JdbcConfigHelper.createHikariDataSource();

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
