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
 * Date  : 2020. 12. 3. 오전 11:11:27
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.spring.jdbc.config;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

/**
 * 다수 개의 DBMS에 동일한 작업을 수행하기 위한 설정을 지원하는 클래스.
 * 
 * @since 2020. 12. 3.
 * @version 0.3.0
 * @author Park_Jun_Hong_(fafanmama_at_naver_com)
 */
@Validated
public class MultipleDataSourceConfig {

    /** 접속계정명 */
    @NotNull
    @NotEmpty
    private String username;
    /** 접속 비밀번호 */
    @NotNull
    private String password;
    private int mininumIdle;
    private int maximumPoolSize;
    @NotNull
    private String poolName;
    private int idleTimeout;
    private int connectionTimeout;
    private int validationtimeout;
    private int maxLifetime;
    private boolean autoCommit;
    private List<String> jdbcUrls = new ArrayList<>();

    /**
     * 
     * @since 2020. 12. 3.
     */
    public MultipleDataSourceConfig() {
    }

    /**
     *
     * @return the connectionTimeout
     *
     * @since 2020. 12. 3.
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     *
     * @return the idleTimeout
     *
     * @since 2020. 12. 3.
     */
    public int getIdleTimeout() {
        return idleTimeout;
    }

    /**
     *
     * @return the jdbcUrls
     *
     * @since 2020. 12. 3.
     */
    public List<String> getJdbcUrls() {
        return jdbcUrls;
    }

    /**
     *
     * @return the maximumPoolSize
     *
     * @since 2020. 12. 3.
     */
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    /**
     *
     * @return the maxLifetime
     *
     * @since 2020. 12. 3.
     */
    public int getMaxLifetime() {
        return maxLifetime;
    }

    /**
     *
     * @return the mininumIdle
     *
     * @since 2020. 12. 3.
     */
    public int getMininumIdle() {
        return mininumIdle;
    }

    /**
     *
     * @return the password
     *
     * @since 2020. 12. 3.
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @return the poolName
     *
     * @since 2020. 12. 3.
     */
    public String getPoolName() {
        return poolName;
    }

    /**
     *
     * @return the username
     *
     * @since 2020. 12. 3.
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return the validationtimeout
     *
     * @since 2020. 12. 3.
     */
    public int getValidationtimeout() {
        return validationtimeout;
    }

    /**
     *
     * @return the autoCommit
     *
     * @since 2020. 12. 3.
     */
    public boolean isAutoCommit() {
        return autoCommit;
    }

    /**
     * @param autoCommit
     *            the autoCommit to set
     *
     * @since 2020. 12. 3.
     */
    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    /**
     * @param connectionTimeout
     *            the connectionTimeout to set
     *
     * @since 2020. 12. 3.
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @param idleTimeout
     *            the idleTimeout to set
     *
     * @since 2020. 12. 3.
     */
    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    /**
     * @param jdbcUrls
     *            the jdbcUrls to set
     *
     * @since 2020. 12. 3.
     */
    public void setJdbcUrls(@NotNull @NotEmpty List<String> jdbcUrls) {
        this.jdbcUrls = jdbcUrls;
    }

    /**
     * @param maximumPoolSize
     *            the maximumPoolSize to set
     *
     * @since 2020. 12. 3.
     */
    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    /**
     * @param maxLifetime
     *            the maxLifetime to set
     *
     * @since 2020. 12. 3.
     */
    public void setMaxLifetime(int maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    /**
     * @param mininumIdle
     *            the mininumIdle to set
     *
     * @since 2020. 12. 3.
     */
    public void setMininumIdle(int mininumIdle) {
        this.mininumIdle = mininumIdle;
    }

    /**
     * @param password
     *            the password to set
     *
     * @since 2020. 12. 3.
     */
    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    /**
     * @param poolName
     *            the poolName to set
     *
     * @since 2020. 12. 3.
     */
    public void setPoolName(@NotNull String poolName) {
        this.poolName = poolName;
    }

    /**
     * @param username
     *            the username to set
     *
     * @since 2020. 12. 3.
     */
    public void setUsername(@NotNull @NotEmpty String username) {
        this.username = username;
    }

    /**
     * @param validationtimeout
     *            the validationtimeout to set
     *
     * @since 2020. 12. 3.
     */
    public void setValidationtimeout(int validationtimeout) {
        this.validationtimeout = validationtimeout;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 12. 3.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2020. 12. 3.
     * @author Park_Jun_Hong_(fafanmama_at_naver_com)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MultipleDataSourceConfig [username=");
        builder.append(username);
        builder.append(", password=");
        builder.append(password);
        builder.append(", mininumIdle=");
        builder.append(mininumIdle);
        builder.append(", maximumPoolSize=");
        builder.append(maximumPoolSize);
        builder.append(", poolName=");
        builder.append(poolName);
        builder.append(", idleTimeout=");
        builder.append(idleTimeout);
        builder.append(", connectionTimeout=");
        builder.append(connectionTimeout);
        builder.append(", validationtimeout=");
        builder.append(validationtimeout);
        builder.append(", maxLifetime=");
        builder.append(maxLifetime);
        builder.append(", autoCommit=");
        builder.append(autoCommit);
        builder.append(", jdbcUrls=");
        builder.append(jdbcUrls);
        builder.append("]");
        return builder.toString();
    }

}
