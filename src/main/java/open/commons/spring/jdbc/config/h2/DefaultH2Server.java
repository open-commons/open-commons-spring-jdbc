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
 * Date  : 2025. 4. 28. 오후 2:12:56
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config.h2;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import open.commons.core.function.SQLFunction;
import open.commons.core.utils.ExceptionUtils;
import open.commons.spring.jdbc.config.h2.AbstractH2ServerTypeArgs.H2ServerType;

/**
 * H2 서버 구동을 위한 상위 클래스. (자동 실행/종료)
 * 
 * @since 2025. 4. 28.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class DefaultH2Server {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /** H2 서버 구동을 위한 정보 */
    protected final H2ServerArgs serverArgs;
    /** H2 서버 유형 */
    protected final H2ServerType serverType;
    /** H2 서버 */
    protected Server server;

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
     * @param serverArgs
     *            서버 구동을 위한 정보.
     * @param serverType
     *            서버 유형.
     *
     * @since 2025. 4. 28.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public DefaultH2Server(@NotNull H2ServerArgs serverArgs, @NotNull H2ServerType serverType) {
        this.serverArgs = serverArgs;
        this.serverType = serverType;
    }

    /**
     * 서버 유형에 따라 H2 서버를 구동시킵니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 4. 28.		박준홍			최초 작성
     * </pre>
     *
     * @throws SQLException
     *
     * @since 2025. 4. 28.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public void startServer() throws SQLException {

        SQLFunction<String[], Server> creator = null;

        switch (this.serverType) {
            case PG:
                creator = Server::createPgServer;
                break;
            case TCP:
                creator = Server::createTcpServer;
                break;
            case WEB:
                creator = Server::createWebServer;
                break;
            default:
                throw ExceptionUtils.newException(IllegalArgumentException.class, "지원하지 않는 서버 유형입니다. 입력값=%s", this.serverType);
        }

        List<String> baseArgs = this.serverArgs.getBaseArguments();
        List<String> tcpArgs = this.serverArgs.getServerArguments(this.serverType);
        String[] totalArgs = Stream.concat(baseArgs.stream(), tcpArgs.stream()).toArray(String[]::new);

        this.server = creator.apply(totalArgs);
        this.server.start();
    }

    @PreDestroy
    public void stopServer() {
        if (this.server != null) {
            this.server.shutdown();
        }
    }
}
