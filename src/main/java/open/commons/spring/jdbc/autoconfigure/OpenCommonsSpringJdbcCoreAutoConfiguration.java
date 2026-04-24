/*
 * Copyright 2026 Park Jun-Hong (parkjunhong77@gmail.com)
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
 * Date  : 2026. 4. 24. 오전 11:10:28
 *
 * Author: Park Jun-Hong (parkjunhong77@gmail.com)
 * 
 */

package open.commons.spring.jdbc.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * * <b><i>{@code open.commons.spring.jdbc.autoconfigure}</i></b> 패키지에 선언된
 * {@link AutoConfiguration}이 적용된 클래스 이외에 {@link ComponentScan}의 대상이 되는 '빈'을 포함하는
 * <b><i>{@code package}</i></b>를 로딩하여 자동으로 등록하는 클래스.<br>
 * 
 * <p>
 * <b>🎯 @ComponentScan이 찾아내는 핵심 어노테이션</b>
 * </p>
 * 
 * <p>
 * 스프링 프레임워크에서 컴포넌트 스캔의 대상이 되는 어노테이션들은 기본적으로 <code>@Component</code>를 메타 어노테이션으로 포함하고 있습니다.
 * </p>
 * 
 * <p>
 * <b>1. 기본 및 계층형 어노테이션</b>
 * </p>
 * <ul>
 * <li><code>@Component</code>: 스프링이 관리할 모든 빈의 가장 기본이 되는 어노테이션입니다.</li>
 * <li><code>@Service</code>: 비즈니스 로직을 담당하는 클래스에 부여합니다.</li>
 * <li><code>@Repository</code>: 데이터베이스 접근 계층의 예외를 스프링의 <code>DataAccessException</code>으로 변환하는 기능이
 * 포함된 어노테이션입니다.</li>
 * <li><code>@Controller</code>, <code>@RestController</code>: 웹 프레젠테이션 계층에서 HTTP 요청을 처리하는 클래스에
 * 부여합니다.</li>
 * </ul>
 * 
 * <p>
 * <b>2. 설정 및 확장 어노테이션</b>
 * </p>
 * <ul>
 * <li><code>@Configuration</code>: <code>@Bean</code> 메서드를 포함하는 설정 클래스입니다. 이 어노테이션도 내부적으로
 * <code>@Component</code>를 포함하므로 컴포넌트 스캔 대상입니다.
 * <ul>
 * <li>🚨 주의: 일반 <code>@Configuration</code>은 스캔 대상이지만, <code>@AutoConfiguration</code>은 일반적인 컴포넌트
 * 스캔 대상으로 다루지 않아야 합니다.</li>
 * </ul>
 * </li>
 * <li><code>@ControllerAdvice</code>, <code>@RestControllerAdvice</code>: 전역 예외 처리를 담당하며, 정상 동작하려면
 * 스캔 경로에 포함되어야 합니다.</li>
 * </ul>
 * 
 * <p>
 * <b>3. AOP 관련 어노테이션</b>
 * </p>
 * <ul>
 * <li><code>@Aspect</code>: AOP를 위한 어노테이션입니다. 다만 <code>@Aspect</code> 자체만으로는 컴포넌트 스캔 대상이 아니므로, 스프링
 * 빈으로 등록하려면 보통 <code>@Component</code>를 함께 선언해야 합니다.</li>
 * </ul>
 * 
 * <pre>
 * [개정이력]
 *      날짜       | 작성자                   |   내용
 * -----------------------------------------------------
 * 2026. 4. 24.     parkjunhong77@gmail.com     최초 작성
 * </pre>
 * 
 * @since 2026. 4. 24.
 * @version 4.0.0
 * @author Park Jun-Hong (parkjunhong77@gmail.com)
 */
@AutoConfiguration
// 1. 설정 레코드들이 모여있는 패키지를 스캔하도록 지정합니다.
@ConfigurationPropertiesScan(basePackages = { //
})
// 2. [명시적 로드] 순서와 조건부 평가가 중요한 @Configuration 클래스들은 @Import로 관리합니다.
@Import({ //
})
// 3. [일괄 스캔 로드] 개수가 많고 로드 순서가 독립적인 일반 @Component, @Service 등을 스캔합니다.
@ComponentScan(basePackages = { //
})
public class OpenCommonsSpringJdbcCoreAutoConfiguration {
    public OpenCommonsSpringJdbcCoreAutoConfiguration() {
    }
}
