[2020/04/15]
- Release: 0.1.0

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
    - getObject(String, SQLConsumer<PreparedStatement>, Class<T>, boolean, String...) 반환상태 설정누락 버그 수정

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
