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
 * Date  : 2025. 4. 3. 오전 11:18:04
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config.h2;

import javax.validation.constraints.NotEmpty;

/**
 * 
 * @since 2025. 4. 3.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class H2TcpArgs extends AbstractH2ServerTypeArgs {

    /**
     * SSL 연결 암호화 여부
     * 
     * <pre>
     * H2 Option: 
     * [-webSSL] Use encrypted (HTTPS) connections
     * </pre>
     */
    private boolean enableSSL;
    /**
     * 서버를 중지시킬 때 사용하는 비밀번호
     * 
     * <pre>
     * H2 Option:
     * [-tcpPassword &lt;pwd&gt;] The password for shutting down a TCP server
     * </pre>
     */
    private String password;
    /**
     * 서버 종료 URL
     * 
     * <pre>
     * H2 Option:
     * [-tcpShutdown "&lt;url&gt;"] Stop the TCP server; example: tcp://localhost
     * </pre>
     */
    private String shutdown;
    /**
     * 서버 강제 종료 여부
     * 
     * <pre>
     * H2 Option:
     * [-tcpShutdownForce] Do not wait until all connections are closed
     * </pre>
     */
    private boolean shutdownForce;

    /**
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public H2TcpArgs() {
        super(H2ServerType.TCP, 9092 /* 기본값 */);
    }

    /**
     * 서버 정지용 비밀번호를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return 서버 정지용 비밀번호
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #password
     */
    @H2ServerOptions(name = "Password")
    public String getPassword() {
        return password;
    }

    /**
     * 서버 정지용 URL을 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return 서버 정지용 URL
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #shutdown
     */
    @H2ServerOptions(name = "Shutdown")
    public String getShutdown() {
        return shutdown;
    }

    /**
     * SSL 연결 암호화 여부를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return SSL 연결 암호화 여부
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #enableSSL
     */
    @H2ServerOptions(name = "SSL")
    public Boolean isEnableSSL() {
        return enableSSL ? enableSSL : null;
    }

    /**
     * 서버 강제 종료 여부를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return 서버 강제 종료 여부
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #shutdownForce
     */
    @H2ServerOptions(name = "ShutdownForce")
    public Boolean isShutdownForce() {
        return shutdownForce ? shutdownForce : null;
    }

    /**
     * SSL 연결 암호화 여부를 설정합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param enableSSL
     *            SSL 연결 암호화 여부
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #enableSSL
     */
    public void setEnableSSL(boolean enableSSL) {
        this.enableSSL = enableSSL;
    }

    /**
     * 서버 정지용 비밀번호를 설정합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param password
     *            서버 정지용 비밀번호
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #password
     */
    public void setPassword(@NotEmpty String password) {
        H2DbUtils.assertNullOrEmpty(password, "비밀번호");
        this.password = password;
    }

    /**
     * 서버 정지용 URL을 설정합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param shutdownUrl
     *            서버 정지용 URL
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #shutdown
     */
    public void setShutdown(@NotEmpty String shutdownUrl) {
        H2DbUtils.assertNullOrEmpty(shutdownUrl, "서버를 정지시키는 URL 정보");
        this.shutdown = shutdownUrl;
    }

    /**
     * 서버 강제 종료 여부를 설정합니다.ㅏ <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param shutdownForce
     *            서버 강제 종료 여부
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #shutdownForce
     */
    public void setShutdownForce(boolean shutdownForce) {
        this.shutdownForce = shutdownForce;
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
        builder.append("H2TcpArgs [type=");
        builder.append(type);
        builder.append(", port=");
        builder.append(port);
        builder.append(", allowOthers=");
        builder.append(allowOthers);
        builder.append(", daemon=");
        builder.append(daemon);
        builder.append(", enableSSL=");
        builder.append(enableSSL);
        builder.append(", password=");
        builder.append(password);
        builder.append(", shutdown=");
        builder.append(shutdown);
        builder.append(", shutdownForce=");
        builder.append(shutdownForce);
        builder.append("]");
        return builder.toString();
    }

}
