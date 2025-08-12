/*
 * Copyright 2025 Park Jun-Hong (parkjunhong77@gmail.com)
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
 * Date  : 2025. 4. 3. 오전 11:17:51
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config.h2;

import java.util.List;
import java.util.Map;

import open.commons.core.utils.ExceptionUtils;
import open.commons.spring.jdbc.config.h2.AbstractH2ServerTypeArgs.H2ServerType;

/**
 * H2 서버 구동 설정.
 * 
 * @since 2025. 4. 3.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class H2ServerArgs implements IH2ServerArgs {
    /**
     * H2 서버 설정.
     * 
     * <pre>
     * H2 Option:
     * [-properties "&lt;dir&gt;"] Server properties (default: ~, disable: null)
     * </pre>
     */
    private String properties;

    /**
     * H2 DB 기본 디렉토리
     * 
     * <pre>
     * H2 Option:
     * [-baseDir &lt;dir&gt;] The base directory for H2 databases (all servers)
     * </pre>
     */
    private String baseDir;
    /**
     * 기존 DB만 사용하는지 여부.
     * 
     * <pre>
     * H2 Option:
     * [-ifExists] Only existing databases may be opened (all servers)
     * </pre>
     */
    private boolean exists;
    /**
     * DB가 없는 경우 생성하는지 여부.
     * 
     * <pre>
     * H2 Option:
     * [-ifNotExists] Databases are created when accessed
     * </pre>
     */
    private boolean notExists;
    /**
     * 구동시 로그 생성 여부.
     * 
     * <pre>
     * H2 Option:
     * [-trace] Print additional trace information (all servers)
     * </pre>
     */
    private boolean trace;
    /**
     * DB 이름과 다른 이름의 연결 정보.<br>
     * 이 정보는 'from'과 'to'를 'key'로 하는 2개의 값만 지원.
     * 
     * <pre>
     * H2 Option:
     * [-key &lt;from&gt; &lt;to&gt;] Allows to map a database name to another (all servers)
     * </pre>
     */
    private Map<String, String> key;

    /** H2 Web 서버 설정 */
    private H2WebArgs web;
    /** H2 TCP 서버 설정 */
    private H2TcpArgs tcp;
    /** H2 PG 서버 설정 */
    private H2PgArgs pg;

    /**
     * H2 서버 기본설정을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public List<String> getBaseArguments() {
        return toArguments();
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the baseDir
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #baseDir
     */
    @H2ServerOptions(name = "baseDir", addType = false)
    public String getBaseDir() {
        return baseDir;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the key
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #key
     */
    @H2ServerOptions(name = "key", addType = false)
    public String getKey() {
        if (this.key == null) {
            return null;
        }

        String from = this.key.get("from");
        if (from == null || (from = from.trim()).isEmpty()) {
            return null;
        }
        String to = this.key.get("to");
        if (to == null || (to = to.trim()).isEmpty()) {
            return null;
        }

        return String.join(" ", from, to);
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the pg
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #pg
     */
    public H2PgArgs getPg() {
        return pg;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the properties
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #properties
     */
    public String getProperties() {
        return properties;
    }

    /**
     * 서버 유형에 대한 설정을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param server
     * @return
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public List<String> getServerArguments(H2ServerType server) {
        switch (server) {
            case WEB:
                return this.web != null ? this.web.toArguments() : null;
            case TCP:
                return this.tcp != null ? this.tcp.toArguments() : null;
            case PG:
                return this.pg != null ? this.pg.toArguments() : null;
            default:
                throw ExceptionUtils.newException(IllegalArgumentException.class, "지원하지 않는 서버 유형입니다. 입력값=%s", server);
        }
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the tcp
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #tcp
     */
    public H2TcpArgs getTcp() {
        return tcp;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the web
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #web
     */
    public H2WebArgs getWeb() {
        return web;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the exists
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #exists
     */
    @H2ServerOptions(name = "ifExists", addType = false)
    public Boolean isExists() {
        return exists ? exists : null;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the notExists
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #notExists
     */
    @H2ServerOptions(name = "ifNotExists", addType = false)
    public Boolean isNotExists() {
        return notExists ? notExists : null;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the trace
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #trace
     */
    @H2ServerOptions(name = "trace", addType = false)
    public Boolean isTrace() {
        return trace ? trace : null;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param baseDir
     *            the baseDir to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #baseDir
     */
    public void setBaseDir(String baseDir) {
        H2DbUtils.assertNullOrEmpty(baseDir, "H2 기본 경로");
        this.baseDir = baseDir;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param exists
     *            the exists to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #exists
     */
    public void setExists(boolean exists) {
        this.exists = exists;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param key
     *            the key to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #key
     */
    public void setKey(Map<String, String> key) {
        this.key = key;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param notExists
     *            the notExists to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #notExists
     */
    public void setNotExists(boolean notExists) {
        this.notExists = notExists;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param pg
     *            the pg to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #pg
     */
    public void setPg(H2PgArgs pg) {
        this.pg = pg;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param properties
     *            the properties to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #properties
     */
    public void setProperties(String properties) {
        this.properties = properties;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param tcp
     *            the tcp to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #tcp
     */
    public void setTcp(H2TcpArgs tcp) {
        this.tcp = tcp;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param trace
     *            the trace to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #trace
     */
    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param web
     *            the web to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #web
     */
    public void setWeb(H2WebArgs web) {
        this.web = web;
    }

    /**
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see open.commons.spring.jdbc.config.h2.IH2ServerArgs#toArguments()
     */
    @Override
    public List<String> toArguments() {
        return H2DbUtils.createH2ServerArguments(this, null);
    }

    /**
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("H2ServerArgs [properties=");
        builder.append(properties);
        builder.append(", baseDir=");
        builder.append(baseDir);
        builder.append(", exists=");
        builder.append(exists);
        builder.append(", notExists=");
        builder.append(notExists);
        builder.append(", trace=");
        builder.append(trace);
        builder.append(", key=");
        builder.append(key);
        builder.append(", web=");
        builder.append(web);
        builder.append(", tcp=");
        builder.append(tcp);
        builder.append(", pg=");
        builder.append(pg);
        builder.append("]");
        return builder.toString();
    }

}
