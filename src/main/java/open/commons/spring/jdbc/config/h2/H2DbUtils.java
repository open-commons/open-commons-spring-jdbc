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
 * Date  : 2025. 4. 3. 오전 11:17:42
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.config.h2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import open.commons.core.utils.AnnotationUtils;
import open.commons.core.utils.ExceptionUtils;
import open.commons.spring.jdbc.config.h2.AbstractH2ServerTypeArgs.H2ServerType;

/**
 * H2 DB에 대한 지원기능을 제공합니다.
 * 
 * @since 2025. 4. 3.
 * @version 0.5.0
 * @author parkjunhong77@gmail.com
 */
public class H2DbUtils {

    /** 객체 생성 막기 */
    private H2DbUtils() {
    }

    /**
     * 문자열이 null 또는 비어있는지를 검증합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2025. 3. 28.     박준홍         최초 작성
     * </pre>
     *
     * @param str
     *            확인하려고 하는 정보
     * @param title
     *            정보를 사용하는 대상
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public static void assertNullOrEmpty(String str, String title) {
        Assert.notNull(str, String.format("'%s'(으)로 사용되는 정보는 null 을 허용하지 않습니다.", title));
        str = str.trim();
        Assert.hasLength(str, String.format("'%s'(으)로 사용되는 정보는 빈문자열을 허용하지 않습니다.", title));
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
     * @param H2
     *            서버 설정 정보
     * @param H2
     *            서버 유형
     * @return
     *
     * @since 2025. 4. 3.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    public static List<String> createH2ServerArguments(@NotNull Object o, H2ServerType type) {
        if (o == null) {
            return null;
        }

        List<String> arguments = new ArrayList<>();

        String svrType = type != null ? type.getType() : "";
        List<Method> methods = AnnotationUtils.getAnnotatedMethodsAll(o, H2ServerOptions.class);

        Object v = null;
        H2ServerOptions anno = null;
        String optName = null;
        for (Method m : methods) {
            try {
                // H2 옵션 설정
                v = m.invoke(o);
                if (v == null || v.toString().isEmpty()) {
                    continue;
                }
                anno = m.getAnnotation(H2ServerOptions.class);
                // 옵션 이름
                optName = String.join("", "-", anno.addType() ? svrType : "", anno.name());
                // 옵션 이름 추가.
                arguments.add(optName);
                // 'boolean' 형태가 아닌 경우 설정값 추가.
                if (!Boolean.class.equals(m.getReturnType())) {
                    arguments.add(v.toString());
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw ExceptionUtils.newException(RuntimeException.class, e, "데이터 조회 중 오류가 발생하였습니다. 객체=%s, 메소드=%s", o, m);
            }
        }

        return arguments;
    }
}
