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
 * Date  : 2025. 4. 3. 오전 11:18:08
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config.h2;

import java.util.List;

import open.commons.core.utils.StringUtils;

/**
 * 
 * @since 2025. 4. 3.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class H2WebArgs extends AbstractH2ServerTypeArgs {
    /**
     * 서버에서 외부로 제공하는 이름 또는 IP 주소 정보 (콤마로 구분됨)
     * 
     * <pre>
     * H2 Option:
     * [-webExternalNames &lt;names&gt;] The comma-separated list of external names and IP addresses of this server, used together with -webAllowOthers
     * </pre>
     */
    private List<String> externalNames;

    /**
     * HTTPS 연결 암호화 여부
     * 
     * <pre>
     * H2 Option:
     * [-webSSL] Use encrypted (HTTPS) connections
     * </pre>
     */
    private boolean enableSSL;

    /**
     * Web 접속시 연결페이지 제공 여부
     * 
     * <pre>
     * H2 Option:
     * [-browser] Start a browser connecting to the web server
     * </pre>
     */
    private boolean browser;

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
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public H2WebArgs() {
        super(H2ServerType.WEB, 8082 /* 기본값 */);
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
     * @return the webExternalNames
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #externalNames
     */
    @H2ServerOptions(name = "ExternalNames")
    public String getExternalNames() {
        return externalNames != null //
                ? StringUtils.concat(this.externalNames, ",", false, true, false) //
                : null;
    }

    /**
     * Web 접속시 연결페이지 제공 여부를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return Web 접속시 연결페이지 제공 여부
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #browser
     */
    @H2ServerOptions(name = "browser", addType = false)
    public boolean isBrowser() {
        return browser;
    }

    /**
     * HTTPS 연결 암호화 여부를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     * 
     * @return HTTPS 연결 암호화 여부
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #enableSSL
     */
    @H2ServerOptions(name = "SSL")
    public boolean isEnableSSL() {
        return enableSSL;
    }

    /**
     * Web 접속시 연결페이지 제공 여부를 설정합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param browser
     *            Web 접속시 연결페이지 제공 여부
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #browser
     */
    public void setBrowser(boolean browser) {
        this.browser = browser;
    }

    /**
     * HTTPS 연결 암호화 여부를 설정합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param enableSSL
     *            HTTPS 연결 암호화 여부
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
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 27.     박준홍         최초 작성
     * </pre>
     *
     * @param externalNames
     *            the externalNames to set
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     *
     * @see #externalNames
     */
    public void setExternalNames(List<String> externalNames) {
        this.externalNames = externalNames;
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
        builder.append("H2WebArgs [type=");
        builder.append(type);
        builder.append(", port=");
        builder.append(port);
        builder.append(", allowOthers=");
        builder.append(allowOthers);
        builder.append(", enableSSL=");
        builder.append(enableSSL);
        builder.append(", browser=");
        builder.append(browser);
        builder.append("]");
        return builder.toString();
    }

}
