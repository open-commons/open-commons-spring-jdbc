/*
 * Copyright 2019 Park Jun-Hong_(parkjunhong77@gmail.com)
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
 * COPYLEFT by 'Open Commons' & Park Jun-Hong (parkjunhong77@gmail.com) All Rights Reserved.
 *
 * This file is generated under this project, "open-commons-springframework4".
 *
 * Date  : 2019. 3. 28. 오후 3:37:48
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.spring.jdbc.dao;

import java.util.Locale;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

/**
 * DAO 클래스 공통 기능 정의 클래스.
 * 
 * @since 2019. 3. 28.
 * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
 * @version 0.1.0
 */
public interface IGenericDao extends InitializingBean, DisposableBean {

    /**
     * {@link JdbcTemplate}를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2019. 3. 28.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * @version 0.1.0
     */
    // JdbcTemplate getJdbcTemplate();

    /**
     * {@link TransactionAwareDataSourceProxy} 객체 또는 {@link TransactionAwareDataSourceProxy} 제공하는 객체를 제공합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2025. 6. 11.		박준홍		 제공되는 {@link DataSource} 객체가 {@link TransactionAwareDataSourceProxy} 을 강제하기 위해서 구현 클래스에서 제공되는 것을 최상위 인터페이스로 옮김.<br>
     *                               
     * </pre>
     *
     * @return {@link TransactionAwareDataSourceProxy} 객체 또는 {@link TransactionAwareDataSourceProxy} 제공하는 객체
     *
     * @since 2025. 6. 11.
     * @version 0.5.0
     * @author parkjunhong77@gmail.com
     */
    <T> T getDataSource();

    /**
     * 이름에 해당하는 쿼리를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param name
     *            쿼리 이름.
     * @return
     *
     * @since 2019. 3. 28.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * @version 0.1.0
     * 
     * @see #getQuerySource()
     * @see ReloadableResourceBundleMessageSource#getMessage(String, Object[], Locale)
     */
    String getQuery(@NotNull String name);

    /**
     * 파라미터 정보에 해당하는 쿼리를 제공한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param name
     *            쿼리 이름
     * @param args
     *            쿼리 파라미터
     * @param locale
     *            지역정보
     * @return
     *
     * @since 2019. 3. 28.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * @version 0.1.0
     * 
     * @see ReloadableResourceBundleMessageSource#getMessage(String, Object[], String, Locale)
     */
    String getQuery(@NotNull String name, Object[] args, Locale locale);

    /**
     * 파라미터 정보에 해당하는 쿼리를 제공한다. 없는 경우 기본값을 제공한다.<br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @param name
     *            쿼리 이름
     * @param args
     *            쿼리 파라미터
     * @param defaultMessage
     *            기본값
     * @param locale
     *            지역정보
     * @return
     *
     * @since 2019. 3. 28.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * @version 0.1.0
     */
    String getQuery(@NotNull String name, Object[] args, String defaultMessage, Locale locale);

    /**
     * JDBC용 쿼리문을 제공하는 객체를 반환한다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2019. 3. 28.		박준홍			최초 작성
     * </pre>
     *
     * @return
     *
     * @since 2019. 3. 28.
     * @author Park_Jun_Hong_(parkjunhong77@gmail.com)
     * @version 0.1.0
     */
    ReloadableResourceBundleMessageSource getQuerySource();

}
