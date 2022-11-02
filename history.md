[2011/11/02]
- Add
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    + insertOrNothingBy(T, Method, Object...)
    + insertOrNothingBy(T, Object...)  
  + open.commons.spring.jdbc.repository.mariadb.AbstractMariadbSingleDataSourceRepository&lt;&gt;
    + insertOrNothingBy(T, Method, Object...)    
  + open.commons.spring.jdbc.repository.oracle.AbstractOracleSingleDataSourceRepoistory&lt;&gt;
    + insertOrNothingBy(T, Method, Object...)    
  + open.commons.spring.jdbc.repository.postgresql.AbstractPostgreSingleDataSourceRepository&lt;&gt;
    + insertOrNothingBy(T, Method, Object...)    

[2011/11/01]
- Add
  + open.commons.spring.jdbc.repository.postgresql.AbstractPostgreSingleDataSourceRepository&lt;T&gt;
    + insertOrUpdateBy(T, Method, Object...): 내부 구현

[2022/10/11]
- Bugfix
  + open.commons.spring.jdbc.dao.AbstractGenericDao.wrapQueryForCount(String)
    + '임시 테이블 생성을 위한 hashCode() 함수 결과에 '-' 포함 처리를 위해서 ` 사용시 Oracle 에서 ORA-00911 에러 발생' 처리

[2022/07/14]
- Add
 + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
   + getColumns()
   + getColumnsAsStream()
   + getColumnValues(T, List&ltString&gt;)
   + getColumnValues(T, String...)
   + insertOrUpdateBy(T, Method, Object...)
   + insertOrUpdateBy(T, Object...)
 + open.commons.spring.jdbc.repository.mariadb.AbstractMariadbSingleDataSourceRepository&lt;T&gt;
   + insertOrUpdateBy(T, Method, Object...)
 + open.commons.spring.jdbc.repository.oracle.AbstractOracleSingleDataSourceRepoistory&lt;T&gt;
   + insertOrUpdateBy(T, Method, Object...)

[2022/04/07]
- Release: 0.4.0-SNAPSHOT
- Tag: 0.3.0
- Dependencies:
  + open.commons.core: 2.0.0-SNAPSHOT

[2022/04/07]
- Release: 0.3.0

[2022/03/28]
- Add
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    + addQueryForInClause(NamedTemplate, String, int)
    + createQueryForInClause(String, int)
    + createQueryForInClause(String, String, int)
    + getValue(String, SQLConsumer&lt;PreparedStatement&gt;, String)

[2022/03/23]
- Modify
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    + getColumnMethods(): 데이터 제공 메소드 미정의 허용

[2022/03/10]
- Bugfix
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    - selectAll(String...)

[2022/03/02]
- Add
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    - getValue(String, SQLConsumer<PreparedStatement>, boolean, String, Function<Object, T>)
    - getValue(String, String, boolean, Function<Object, T>)
    - getValue(String, String, Function<Object, T>)
    - getValues(String, SQLConsumer<PreparedStatement>, String, Function<Object, T>)
    - getValues(String, String, Function<Object, T>)  
  
[2022/02/11]
- Add
  + open.commons.spring.jdbc.repository.IGenericRepository&lt;T&gt;
    + countBy(Map&lt;String, Object&gt;)
    + selectBy(Map&lt;String, Object&gt;, int, int, String...)
    + selectBy(Map&lt;String, Object&gt;, String...)
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    + countBy(Map&lt;String, Object&gt;)
    + selectBy(Map&lt;String, Object&gt;, int, int, String...)
    + selectBy(Map&lt;String, Object&gt;, String...)

[2022/02/10]
- Add
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    + createQueryForOrderByForPagination(Object[], int, int, String...)
    + selectMultiOrderByForPagination(Object[], int, int, String...)
    + selectMultiOrderByForPagination(Object[], int, int, String[], String...)


[2022/01/21]
- Dependencies
  + Update: commons-io:commons-io:2.11.0

[2021/12/28]
- Update
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    + getColumnMethods(): 컬럼 정렬 관련 구현 변경

[2021/12/28]
- Add
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    + countAll()
    + <strike>countOf(Method, Object...)</strike>countBy(Method, Object...)
    + <strike>countOf(Object...)</strike>countBy(Object...)
  
[2021/12/24]
- Add
  + open.commons.spring.jdbc.repository.mariadb.AbstractMariadbSingleDataSourceRepository&lt;T&gt;
- Delete
  + open.commons.spring.jdbc.repository.postgresql.AbstractPostgreSingleDataSourceRepository&lt;T&gt;
    + getList(String, Object[], int, int, Class&lt;E&gt;, String...)
    + wrapQueryForPartition(String)

[2021/12/16]
- Add
  + open.commons.spring.jdbc.repository.oracle.AbstractOracleSingleDataSourceRepoistory&lt;T&gt;

[2021/12/15]
- Modify
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    + addWhereClause(StringBuffer, Method, Object...): Variable Binding 컬럼 개수와  파리미터 개수 검증 기능 개선 
    + attachWhereClause(String, Method, Object...): Variable Binding 컬럼 개수와  파리미터 개수 검증 기능 개선
- Add
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    + hasNoWhereCompares(Collection&lt;JdbcVariableBinder&gt;, WhereCompare...)
    + hasWhereCompares(Collection&ltJdbcVariableBinder&gt;, WhereCompare...)

[2021/12/14]
- Modify
  + open.commons.spring.jdbc.repository.annotation
    - ColumnVariableBinder -> JdbcVariableBinder
  + open.commons.spring.jdbc.repository.AbstractGenericRepository
    - 그외 다수의 메소드 변경 및 추가
- Delete
  + open.commons.spring.jdbc.repository.AbstractGenericRepository
    - getVariableBindingColumn(Method)
    - getVariableBindingParameter(Method)  
- Add
  + open.commons.spring.jdbc.repository.exceptions.UnsupportedVariableBindingException

[2021/12/13]
- Add
  + open.commons.spring.jdbc.repository.annotation.ColumnVariableBinder
  
[2021/12/08]
- Add
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    + addOffsetClause(StringBuffer, int, int)
    + addOrderByClause(StringBuffer, String...)
    + createOrderByClause(String...)
    + createQueryForSelectForPagination(String, Method, int, int, Object...)
    + createQueryForSelectOrderBy(String, Method, Object[], String...)
    + selectAll(int, int, String...)
    + selectAll(String...)
    + selectMultiOrderBy(Method, Object[], String...)
    + selectMultiOrderBy(Method, Object[], String[], String...)
    + selectMultiOrderBy(Object[], String...)
    + selectMultiOrderBy(Object[], String[], String...)
    + selectMultiOrderByForPagination(Method, int, int, Object[], String...)
    + selectMultiOrderByForPagination(Method, int, int, Object[], String[], String...)

[2021/12/08]
- Modify
  + open.commons.spring.jdbc.repository.AbstractGenericRepository&lt;T&gt;
    + Add
      + selectSingleBy(boolean, Method, Object...)
      + selectSingleBy(boolean, Method, Object[], String...)  
    + Delete
      + selectSingleBy(Method, boolean, Object...)
      + selectSingleBy(Method, boolean, Object[], String...)  
  
[2021/12/07]
- Add
  + open.commons.spring.jdbc.repository.IGenericRepository
    + getTableName()
    
[2021/12/06]
- Add
  + open.commons.spring.jdbc.repository.IGenericRepository
    + getEntityType()
  + open.commons.spring.jdbc.repository.AbstractGenericRepository
    + getEntityType()
  
[2021/12/03]
- Add
  + open.commons.spring.jdbc.repository.AbstractGenericRepository
    + AbstractGenericRepository(Class&lt;T&gt;, boolean)
  + open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository
    + AbstractSingleDataSourceRepository(Class&lt;T&gt;, boolean)
  + open.commons.spring.jdbc.repository.postgresql.AbstractPostgreSingleDataSourceRepository
    + AbstractPostgreSingleDataSourceRepository(Class&lt;T&gt;, boolean)

[2021/12/03]
- Add
  + open.commons.spring.jdbc.repository.AbstractGenericRepository
    + containsNull(Object...)
    + deleteBy(Object...)
    + getCurrentMethod(boolean, Object...)
    + getCurrentMethod(Class&lt;?&gt;...)
    + getCurrentMethod(int, boolean, Object...)
    + getCurrentMethod(int, Class&lt;?&gt;...)
    + getCurrentMethod(int, Object...)
    + getCurrentMethod(Object...)
    + selectMultiBy(Object...)
    + selectMultiBy(Object[], String...)
    + selectSingleBy(boolean, Object...)
    + selectSingleBy(boolean, Object[], String...)
    + updateBy(T, Object...)

[2021/12/01]
- Add
  + open.commons.spring.jdbc.repository.AbstractGenericRepository
    + getColumnValues()
    + getColumnValuesOfParameters(Method)
    + getUpdateParameters(T): 객체에서 변경에 사용할 정보 제공
    + getUpdateParameters(T)
    + createColumnAssignQueries(StringBuffer, String, List&lt;ColumnValue&gt;)
    + getAssignQuery(ColumnValue)
    
- Modify
  + open.commons.spring.jdbc.repository.AbstractGenericRepository.updateBy(T, Method, Object...): 버그 수정

[2021/11/30]
- Modify
  + open.commons.spring.jdbc.repository.AbstractGenericRepository.getColumnNamesOfParameters(Method): 'Where' 절에 사용되는 컬럼이름 검증.
- New
  + open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository
- Rename
  + open.commons.spring.jdbc.repository.<strike>AbstractSingleDataSourceRepository</strike>AbstractGenericRepository
- Add
  + open.commons.spring.jdbc.repository.AbstractGenericRepository
  + <strike>open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository</strike>
    + selectMultiBy(Method, Object[], String...)
    + selectSingleBy(Method, boolean, Object[], String...)  
- Add
  + open.commons.spring.jdbc.dao.AbstractGenericDao.objectArray(Object...)

[2021/11/29]
- Add
  + open.commons.spring.jdbc.repository.postgresql.AbstractPostgreSingleDataSourceRepository
    + getColumnNameOfParameter(Method): 메소드 파라미터에 설정된 컬럼명을 제공.
  
[2021/11/26]
- New
  + open.commons.spring.jdbc.repository.AbstractSingleDataSourceRepository
  + open.commons.spring.jdbc.repository.IGenericRepository  
  + open.commons.spring.jdbc.repository.postgresql.AbstractPostgreSingleDataSourceRepository

[2021/11/19]
- Depencencies Vulnerabilities
  + commons-io:commons-io
    - [CVE-2021-29425](https://github.com/advisories/GHSA-gwrp-pvrq-jmwv) __moderate severity__
    - Vulnerable versions: < 2.7
    - Patched version: 2.7
    - In Apache Commons IO before 2.7, When invoking the method FileNameUtils.normalize with an improper input string,\
      like "//../foo", or "\..\foo", the result would be the same value, thus possibly providing access to files in the parent directory,\
      but not further above (thus "limited" path traversal), if the calling code would use the result to construct a path value.

[2021/11/11]
- New
  + open.commons.spring.jdbc.dao.mariadb.AbstractMariadbGenericDao: 단일 Mariadb 연동 객체
- Add
  + open.commons.spring.jdbc.dao.mariadb.AbstractGenericDao
    + executeUpdate(List&lt;E&gt;, SQLTripleFunction&lt;PreparedStatement, Integer, E, Integer&gt;, int, String, String)

[2021/04/23]
- Add
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    - getList(String, SQLFunction&lt;ResultSet, List&lt;E&gt;&gt;)
- Modify
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    - executeQuery(ConnectionCallbackBroker, Class&lt;E&gt;, String...): 소요시간 로그 추가
    - executeQuery(ConnectionCallbackBroker2&lt;S&gt;, Class&lt;E&gt;, String...): 소요시간 로그 추가

[2020/12/03]
- New
  + open.commons.spring.jdbc.config
    - ConfigUtils: Configuration용 함수 제공
    - MultipleDataSourceConfig: 다중 DBMS 연결을 위한 설정 지원
- dependencies
  + spring-boot-starter-parent 적용



[2020/11/21]
- Bugfix
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    - getValues(String, SQLConsumer&lt;PreparedStatement&gt;, String):  getListAsMap(String, SQLConsumer&lt;PreparedStatement&gt;, String...)에 SQLConsumer&lt;PreparedStatement&gt; 전달 누락 수정

[2020/10/29]
- Snapshot: 0.3.0-SNAPSHOT
- Add
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    - executeUpdate(String, SQLConsumer&lt;PreparedStatement&gt;, boolean)
- Update
  + open.commons.spring.jdbc.dao.DefaultConnectionCallback2
    - doInConnection(Connection):  PreparedStatement 를 ConnectionCallbackBroker2에서 제공받도록 수정. 이를 통해서 PreparedStatement와 CallableStatement 를 구분해서 제공한다.
    

[2020/10/29]
- Release: 0.2.0
- Resolve vulnerability
  + GHSA-269g-pwp5-87pp

[2020/08/12]
- Update
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    - createConnectionCallbackBrokers(List<E>, SQLTripleFunction<PreparedStatement, Integer, E, Integer>, int, String, String, String, String)
      - 내부 PreparedStatement Setter 내부 코드 개선: Variable Binding 직후 파라미터 변수 초기화
    - getValue(String, SQLConsumer<PreparedStatement>, boolean, String)
      - 데이타 필수 여부에 따른 객체 확인 코드 추가

[2020/07/30]
- Add
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    - getObjectAsMap(String, boolean, String...)
    - getObjectAsMap(String, SQLConsumer<PreparedStatement>, boolean, String...)
    - getObjectAsMap(String, SQLConsumer<PreparedStatement>, String...)
    - getObjectAsMap(String, String...)
    - getValue(String, SQLConsumer<PreparedStatement>, boolean, String)
    - getValue(String, String)
    - getValue(String, String, boolean)
    - getValues(String, SQLConsumer<PreparedStatement>, String)
    - getValues(String, String)
    
[2020/07/28]
- Add
  + open.commons.spring.jdbc.dao.postgresql.AbstractPostgreSingleDataSourceDao
- Update
  + open.commons.spring.jdbc.dao
    - AbstractGenericDao
    - AbstractMultiDataSourceDao
    - AbstractSingleDataSourceDao

[2020/07/02]
- Bugfix
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    + getListAsMap(String, Class&lt;T&gt;, String...): resolve a compile incompliant.

[2020/06/15]
- Add
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    + executeUpdate(List<E>, Function<List<E>, SQLConsumer<PreparedStatement>>, int, String, String, String, String)
    + executeUpdate(List<E>, SQLTripleFunction<PreparedStatement, Integer, E, Integer>, int, String, String, String, String)
    + getListAsMap(String, Class&lt;T&gt;, String...)
- Modify
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    + findCreator(Class<R>, String...):  DAO Entity 타입으로 Map.class 지원 및 Entity Generator 식별값 생성규칙 변경

[2020/05/27]
- Dependencies
  + open.commons:open-commons-core: 1.6.18

[2020/05/20]
- POM
  + Apply Javadoc
- Vulnerability
  + Upgrade com.fasterxml.jackson.core:jackson-databind to version 2.9.10.4 or later.\
    __CVE-2020-9548__\
    moderate severity\
    Vulnerable versions: >= 2.0.0, <= 2.9.10.3\
    Patched version: 2.9.10.4\
    FasterXML jackson-databind 2.x before 2.9.10.4 mishandles the interaction between serialization gadgets and typing,\
    related to br.com.anteros.dbcp.AnterosDBCPConfig (aka anteros-core).

[2020/04/15]
- Release: 0.1.0
- Tag: 0.2.0-SNAPSHOT

[2020/04/15]
- Vulnerability
  + Bump jackson-databind from 2.9.10.2 to 2.9.10.3 dependencies\
    __CVE-2020-8840__\
    high severity\
    Vulnerable versions: >= 2.9.0, <= 2.9.10.2\
    Patched version: 2.9.10.3\
    FasterXML jackson-databind 2.0.0 through 2.9.10.2 lacks certain xbean-reflect/JNDI blocking,\
    as demonstrated by org.apache.xbean.propertyeditor.JndiConverter.

[2020/04/15]
- Update
  + open.commons.spring.jdbc.dao.AbstractGenericDao.getConnection(Connection, JdbcTemplate)
    -  springframework 5.1.13 >= 대응
- Dependencies
  + org.springframework
    + spring-context: 5.1.13.RELEASE
    + spring-webmvc: 5.1.13.RELEASE
    + spring-jdbc: 5.1.13.RELEASE
  + com.fasterxml.jackson.core
    + jackson-databind: 2.9.10.2
  + open.commons
    + open-commons-core: 1.6.17
    
[2020/02/13]
- Tag: 0.0.7-SNAPSHOT

[2020/02/13]
- Release: 0.0.6
- Add
  + open.commons.spring.jdbc.dao.IAsyncSupportable 
  + open.commons.spring.jdbc.dao.dto.CountDTO
  + open.commons.spring.jdbc.dao.oracle.AbstractAsyncOracleGenericDao
  + open.commons.spring.jdbc.dao.oracle.AbstraceOracleGenericDao
- Update
  + open.commons.spring.jdbc.dao.AbstractGenericDao
  + open.commons.spring.jdbc.dao.DefaultConnectionCallback2
  + open.commons.spring.jdbc.dao.IGenericDao  

[2019/10/17]
- Release: 0.0.5
- Tag: 0.0.6-SNAPSHOT


[2019/7/4]
- Dependency
	+ open.commons.core: 1.6.10
	
[2019/06/20]
- Release: 0.0.4
- Add
  + [open.commons.spring.jdbc.dao.AbstractGenericDao](https://github.com/parkjunhong/open-commons-spring-jdbc/blob/master/src/main/java/open/commons/spring/jdbc/dao/AbstractGenericDao.java)
    - Function<Object[], SQLConsumer<PreparedStatement>> PSSetter 추가

[2019/06/12]
- Release: 0.0.3.1
- BugFix
  + [open.commons.spring.jdbc.dao.AbstractGenericDao](https://github.com/parkjunhong/open-commons-spring-jdbc/blob/master/src/main/java/open/commons/spring/jdbc/dao/AbstractGenericDao.java).
    - getObject(String, SQLConsumer<PreparedStatement>, Class&lt;T&gt;, boolean, String...) 반환상태 설정누락 버그 수정

[2019/06/12]
- Release: 0.0.3
- Update
  + [open.commons.spring.jdbc.dao.AbstractGenericDao](https://github.com/parkjunhong/open-commons-spring-jdbc/blob/master/src/main/java/open/commons/spring/jdbc/dao/AbstractGenericDao.java)
    - addQueryForInClause(StringBuffer, int)
    - addQueryForInClause(StringBuffer, String, String, int)

[2019/06/05]
- Release: 0.0.2
- Update
  + [open.commons.spring.jdbc.dao.AbstractGenericDao](https://github.com/parkjunhong/open-commons-spring-jdbc/blob/master/src/main/java/open/commons/spring/jdbc/dao/AbstractGenericDao.java)
    - execute(SQLFunction<Connection, T>) 내부 구현 수정

[2019/06/04]
- Release: 0.0.1
