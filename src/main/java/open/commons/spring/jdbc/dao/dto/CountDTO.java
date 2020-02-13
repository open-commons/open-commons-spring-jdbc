/*
 * Copyright 2019 Park Jun-Hong_(parkjunhong77/google/com)
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
 * Date  : 2020. 1. 22. 오전 1:41:59
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.spring.jdbc.dao.dto;

import open.commons.annotation.ColumnDef;
import open.commons.annotation.ColumnDef.ColumnNameType;

/**
 * 조회결과 개수를 전달받는 클래스.
 * 
 * @since 2020. 1. 18.
 * @version
 * @author Park_Jun_Hong_(fafanmama_at_naver_com)
 */
public class CountDTO {

    private int count;

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 18.     박준홍         최초 작성
     * </pre>
     *
     * @since 2020. 1. 18.
     * @version
     */
    public CountDTO() {
    }

    /**
     *
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 18.     박준홍         최초 작성
     * </pre>
     * 
     * @return the count
     *
     * @since 2020. 1. 18.
     * @version
     * 
     * @see #count
     */
    public int getCount() {
        return count;
    }

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2020. 1. 18.     박준홍         최초 작성
     * </pre>
     *
     * @param count
     *            the count to set
     *
     * @since 2020. 1. 18.
     * @version
     * 
     * @see #count
     */
    @ColumnDef(name = "count", type = int.class, caseSensitive = false, columnNameType = ColumnNameType.SNAKE_CASE)
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CountDTO [count=");
        builder.append(count);
        builder.append("]");
        return builder.toString();
    }

}
