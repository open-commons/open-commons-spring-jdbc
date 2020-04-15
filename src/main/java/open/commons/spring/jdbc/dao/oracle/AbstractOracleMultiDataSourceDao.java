/*
 *
 * This file is generated under this project, "open-commons-spring-jdbc".
 *
 * Date  : 2020. 4. 15. 오후 3:25:48
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.spring.jdbc.dao.oracle;

import java.sql.PreparedStatement;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import open.commons.Result;
import open.commons.function.SQLTripleFunction;
import open.commons.spring.jdbc.dao.AbstractMultiDataSourceDao;

/**
 * 여러 개의 DBMS 연동을 지원.
 * 
 * @since 2020. 4. 15.
 * @version
 * @author Park_Jun_Hong_(fafanmama_at_naver_com)
 */
public abstract class AbstractOracleMultiDataSourceDao extends AbstractMultiDataSourceDao {

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜    	| 작성자	|	내용
     * ------------------------------------------
     * 2020. 4. 15.		박준홍			최초 작성
     * </pre>
     *
     * @since 2020. 4. 15.
     * @version
     */
    public AbstractOracleMultiDataSourceDao() {
    }

    /**
     * @see open.commons.spring.jdbc.dao.AbstractGenericDao#executeUpdate(java.util.List,
     *      open.commons.function.SQLTripleFunction, int, java.lang.String)
     */
    @Override
    public <E> Result<Integer> executeUpdate(@NotNull List<E> data, @NotNull SQLTripleFunction<PreparedStatement, Integer, E, Integer> dataSetter, @Min(1) int partitionSize,
            @NotNull String valueQuery) {
        return super.executeUpdate(data, dataSetter, partitionSize, "INSERT ALL ", valueQuery, " SELECT 1 FROM DUAL");
    }
}
