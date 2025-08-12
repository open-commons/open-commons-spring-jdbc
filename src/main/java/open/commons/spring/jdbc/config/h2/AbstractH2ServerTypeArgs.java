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
 * Date  : 2025. 4. 3. 오전 11:17:22
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config.h2;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import open.commons.core.utils.ExceptionUtils;

/**
 * H2 서버 구동을 위한 파라미터 공통 데이터.
 * 
 * @since 2025. 4. 3.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class AbstractH2ServerTypeArgs implements IH2ServerArgs {

    /** 서버 타입 */
    @NotNull
    protected final H2ServerType type;
    /**
     * 서버 포트
     * 
     * <pre>
     * H2 Option:
     * [-{type}Port &lt;port&gt;] The port.
     * </pre>
     * 
     * @see #type
     */
    @Min(1)
    protected int port;
    /**
     * 외부 접속 허용 여부.
     * 
     * <pre>
     * H2 Option:
     * [-{type}AllowOthers] Allow other computers to connect
     * </pre>
     * 
     * @see #type
     */
    protected boolean allowOthers = false;

    /**
     * daemo thread로 구동할지 여부.
     * 
     * <pre>
     * H2 Option:
     * [-{type}Daemon] Use a daemon thread
     * </pre>
     * 
     * @see #type
     */
    protected boolean daemon = false;

    /**
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param 서버
     *            유형
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2ServerTypeArgs(@NotNull H2ServerType type) {
        Assert.notNull(type, "서버 구동방식은 반드시 설정되어야 합니다.");
        this.type = type;
    }

    /**
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param type
     *            서버 유형
     * @param port
     *            서버 포트
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public AbstractH2ServerTypeArgs(@NotNull H2ServerType type, @Min(1) int port) {
        Assert.notNull(type, "서버 구동방식은 반드시 설정되어야 합니다.");
        this.type = type;
        this.port = port;
    }

    /**
     * 서버 포트 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the port
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #port
     */
    @H2ServerOptions(name = "Port")
    public int getPort() {
        return port;
    }

    /**
     * 서버 구동 방식을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the type
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #type
     */
    public H2ServerType getType() {
        return type;
    }

    /**
     * 외부 접속 허용 여부를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return the allowOthers
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #allowOthers
     */
    @H2ServerOptions(name = "AllowOthers")
    public Boolean isAllowOthers() {
        return allowOthers ? allowOthers : null;
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
     * @return the isDaemon
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #daemon
     */
    @H2ServerOptions(name = "Daemon")
    public Boolean isDaemon() {
        return daemon ? daemon : null;
    }

    /**
     * 외부 접속 허용 여부를 설정합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param allowOthers
     *            외부 접속 허용 여부
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #allowOthers
     */
    public void setAllowOthers(boolean allowOthers) {
        this.allowOthers = allowOthers;
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
     * @param isDaemon
     *            the isDaemon to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #daemon
     */
    public void setDaemon(boolean isDaemon) {
        this.daemon = isDaemon;
    }

    /**
     * 서버 포트를 설정합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param port
     *            서버 포트
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #port
     */
    public void setPort(@Min(1) int port) {
        if (port < 1) {
            throw ExceptionUtils.newException(IllegalArgumentException.class, "서버 포트는 0보다 커야 합니다. 입력값=%,d", port);
        }
        this.port = port;
    }

    /**
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see kr.co.ymtech.gis.config.h2.IH2ServerArgs#toArguments()
     */
    @Override
    public List<String> toArguments() {
        List<String> args = H2DbUtils.createH2ServerArguments(this, this.type);
        args.add(0, String.join("", "-", this.type.getType()));
        return args;
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
        builder.append(getClass().getSimpleName());
        builder.append(" [type=");
        builder.append(type);
        builder.append(", daemon=");
        builder.append(daemon);
        builder.append(", port=");
        builder.append(port);
        builder.append(", allowOthers=");
        builder.append(allowOthers);
        builder.append("]");
        return builder.toString();
    }

    /**
     * H2 서버 구동 방식
     * 
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public static enum H2ServerType {
        WEB("web"), TCP("tcp"), PG("pg");

        private final String type;

        private H2ServerType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
