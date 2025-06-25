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
 * Date  : 2025. 4. 25. 오전 9:30:33
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import open.commons.core.utils.ExceptionUtils;

/**
 * 프로그램 구동시 DB 테이블 및 데이터를 초기 정보 생성을 위한 정보.
 * 
 * 
 * <br>
 * 
 * <pre>
 * [개정이력]
 *      날짜    	| 작성자			|	내용
 * ------------------------------------------
 * 2025. 4. 25.	parkjunhong	        'open.commons.spring.web.initialize.DatabaseInitResources' of follow 에서 이관됨.
 *                                      &lt;dependency>
 *                                        &lt;groupId>io.github.open-commons&lt;/groupId>
 *                                        &lt;artifactId>open-commons-spring-jdbc&lt;/artifactId>
 *                                        &lt;version>${0.5.0-SNAPSHOT or higher}&lt;/version>
 *                                      &lt;/dependency>
 * </pre>
 * 
 * @since 2025. 4. 25.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class DatabaseInitResources {

    public static final String PATH_CLASSPATH = "classpath:";
    public static final String PATH_FILE = "file:";
    public static final String PATH_DIRECTORY = "dir:";

    /** 테이블 생성을 위한 쿼리 */
    private List<String> schema;

    /** 데이터 초기화를 위한 쿼리 */
    private List<String> data;

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 25.      박준홍         최초 작성
     * </pre>
     *
     *
     * @since 2025. 4. 25.
     * @version 0.8.0
     * @author parkjunhong77@gmail.com
     */
    public DatabaseInitResources() {
    }

    /**
     * SQL 파일 정보에 맞는 {@link Resource}로 변환하여 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 25.      박준홍         최초 작성
     * 2025. 6. 25      박준홍         디렉토리 처리 추가 ("dir:")
     * </pre>
     *
     * @param sqlList
     *            SQL 파일 정보
     * @return
     *
     * @since 2025. 4. 25.
     * @version 0.8.0
     * @author Park, Jun-Hong parkjunhong77@gmail.com
     */
    private List<Resource> getAsResources(List<String> sqlList) {

        if (sqlList == null || sqlList.isEmpty()) {
            return new ArrayList<>();
        }

        return sqlList.stream().map(sql -> {
            if (sql == null) {
                throw ExceptionUtils.newException(IllegalArgumentException.class, "DB 초기화에 사용되는 SQL 파일 경로가 올바르지 않습니다. sql=%s", sql);
            }

            sql = sql.trim();
            if (sql.startsWith(PATH_CLASSPATH)) {
                return Arrays.asList(new ClassPathResource(sql.replace(PATH_CLASSPATH, "")));
            } else if (sql.startsWith(PATH_FILE)) {
                return Arrays.asList(new FileSystemResource(sql.replace(PATH_FILE, "")));
            } else if (sql.startsWith(PATH_DIRECTORY)) {
                String dir = sql.replace(PATH_DIRECTORY, "");
                try {
                    return Files.list(Paths.get(dir)) //
                            .filter(path -> path.toString().endsWith(".sql")) //
                            .map(path -> new FileSystemResource(path)) //
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    throw ExceptionUtils.newException(IllegalArgumentException.class, "DB 초기화에 사용되는 SQL 파일 경로가 올바르지 않습니다. sql=%s", sql);
                }
            } else {
                throw ExceptionUtils.newException(IllegalArgumentException.class, "DB 초기화에 사용되는 SQL 파일 경로가 올바르지 않습니다. sql=%s", sql);
            }
        }).flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * 데이터 생성을 위한 쿼리 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 25.      박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2025. 4. 25.
     * @version 0.8.0
     * @author Park, Jun-Hong parkjunhong77@gmail.com
     */
    public List<Resource> getDataResources() {
        return getAsResources(this.data);
    }

    /**
     * 테이블 생성을 위한 쿼리 정보를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 25.      박준홍         최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2025. 4. 25.
     * @version 0.8.0
     * @author Park, Jun-Hong parkjunhong77@gmail.com
     */
    public List<Resource> getSchemaResources() {
        return getAsResources(this.schema);
    }

    /**
     * DML 쿼리가 있는 파일들을 설정합니다.<br>
     * 파일 경로 정보는 아래와 같은 접두어를 지원합니다.
     * <li>classpath: 패키지 내에 포함된 것을 의미.
     * <ul>
     * <li>예) classpath:resources.queries.user-info
     * </ul>
     * <li>file: 단일 파일을 의미
     * <ul>
     * <li>예) file:./resources/queries/user-info.sql
     * </ul>
     * <li>dir: 디렉토리 내의 파일들을 의미.<br>
     * <code>'dir:'</code>는 해당 디렉토리 내의 모든 파일을 읽기 때문에, <code>'dir:'</code>를 사용하는 경우 명시적으로 DDL(schema)과 DML(data)은 서로 다른
     * 디렉토리에 설정하기 바랍니다.
     * <ul>
     * <li>예) dir:./resources/queries
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 25.      박준홍         최초 작성
     * </pre>
     *
     * @param data
     *            the data to set
     *
     * @since 2025. 4. 25.
     * @version 0.8.0
     * @author parkjunhong77@gmail.com
     *
     * @see #data
     */
    public void setData(@NotNull List<String> data) {
        if (data == null) {
            throw ExceptionUtils.newException(NullPointerException.class, "데이터 생성을 위한 쿼리 파일이 존재하지 않습니다.");
        }
        this.data = data;
    }

    /**
     * DDL 쿼리가 있는 파일들을 설정합니다.<br>
     * 파일 경로 정보는 아래와 같은 접두어를 지원합니다.
     * <li>classpath: 패키지 내에 포함된 것을 의미.
     * <ul>
     * <li>예) classpath:resources.queries.user-table
     * </ul>
     * <li>file: 단일 파일을 의미
     * <ul>
     * <li>예) file:./resources/queries/user-table.sql
     * </ul>
     * <li>dir: 디렉토리 내의 파일들을 의미.<br>
     * <code>'dir:'</code>는 해당 디렉토리 내의 모든 파일을 읽기 때문에, <code>'dir:'</code>를 사용하는 경우 명시적으로 DDL(schema)과 DML(data)은 서로 다른
     * 디렉토리에 설정하기 바랍니다.
     * <ul>
     * <li>예) dir:./resources/queries
     * </ul>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 4. 25.      박준홍         최초 작성
     * </pre>
     *
     * @param schema
     *            the schema to set
     *
     * @since 2025. 4. 25.
     * @version 0.8.0
     * @author parkjunhong77@gmail.com
     *
     * @see #schema
     */
    public void setSchema(@NotNull List<String> schema) {
        if (schema == null) {
            throw ExceptionUtils.newException(NullPointerException.class, "테이블 생성을 위한 쿼리 파일이 존재하지 않습니다.");
        }
        this.schema = schema;
    }

    /**
     *
     * @since 2025. 4. 25.
     * @version 0.8.0
     * @author parkjunhong77@gmail.com
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DatabaseInitResources [schema=");
        builder.append(schema);
        builder.append(", data=");
        builder.append(data);
        builder.append("]");
        return builder.toString();
    }

}
