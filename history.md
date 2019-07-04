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
