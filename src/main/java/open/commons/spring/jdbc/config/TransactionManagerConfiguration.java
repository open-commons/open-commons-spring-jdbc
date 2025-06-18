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
 * Date  : 2025. 6. 11. 오후 2:53:16
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 * @since 2025. 6. 11.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
@Configuration
@EnableTransactionManagement
public class TransactionManagerConfiguration {

    private final Logger logger = LoggerFactory.getLogger(TransactionManagerConfiguration.class);

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 6. 11.		박준홍			최초 작성
     * </pre>
     *
     *
     * @since 2025. 6. 11.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public TransactionManagerConfiguration() {
    }

    /**
     * {@link Repository} 계층에서 사용하는 {@link Connection}에 대한 기본 transaction를 관리 Bean을 제공합니다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 6. 11.		박준홍			최초 작성
     * </pre>
     *
     * @param dataSources
     * @return
     *
     * @since 2025. 6. 11.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    @Bean
    @ConditionalOnSingleCandidate(DataSource.class)
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        PlatformTransactionManager txManager = new DataSourceTransactionManager(dataSource);
        logger.info("[transaction-manager] default-tx-manager={}", txManager);
        return txManager;
    }

}
