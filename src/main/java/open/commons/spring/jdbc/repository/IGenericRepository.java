/*
 * Copyright 2021 Park Jun-Hong_(parkjunhong77@gmail.com)
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
 * Date  : 2021. 11. 26. 오후 12:07:30
 *
 * Author: parkjunhong77@gmail.com
 * 
 */

package open.commons.spring.jdbc.repository;

import java.util.List;

import javax.validation.constraints.Min;

import open.commons.core.Result;
import open.commons.core.annotation.ColumnValue;

/**
 * DBMS Table 모델에 기반하여 단순한 CRUD 수준의 기능을 정의.
 * 
 * @param <T>
 * @since 2021. 11. 26.
 * @version 0.3.0
 * @author parkjunhong77@gmail.com
 */
public interface IGenericRepository<T> extends IGenericRetrieve<T> {

    /**
     * 여러 개의 데이터를 추가합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @param data
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public Result<Integer> insert(List<T> data);

    /**
     * 여러 개의 데이터를 추가합니다. <br>
     * 
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2021. 11. 26.		박준홍			최초 작성
     * </pre>
     *
     * @param data
     * @param partitionSize
     *            한번에 저장할 데이터 개수.
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     */
    public Result<Integer> insert(List<T> data, @Min(1) int partitionSize);

    /**
     * 데이터를 추가합니다. <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2021. 11. 26.        박준홍         최초 작성
     * </pre>
     *
     * @param data
     * @return
     *
     * @since 2021. 11. 26.
     * @version 0.3.0
     * @author parkjunhong77@gmail.com
     * 
     * @see ColumnValue
     */
    public Result<Integer> insert(T data);

}
