[2019/06/20]
- Release: 0.0.4
- Add
  + open.commons.spring.jdbc.dao.AbstractGenericDao 클래스
    - Function<Object[], SQLConsumer<PreparedStatement>> PSSetter 추가

[2019/06/12]
- Release: 0.0.3.1
- BugFix
  + open.commons.spring.jdbc.dao.AbstractGenericDao.getObject(String, SQLConsumer<PreparedStatement>, Class<T>, boolean, String...) 반환상태 설정누락 버그 수정

[2019/06/12]
- Release: 0.0.3
- Update
  + open.commons.spring.jdbc.dao.AbstractGenericDao
    - addQueryForInClause(StringBuffer, int)
    - addQueryForInClause(StringBuffer, String, String, int)

[2019/06/05]
- Release: 0.0.2
- Update
  + open.commons.spring.jdbc.dao.AbstractGenericDao.execute(SQLFunction<Connection, T>) 내부 구현 수정

[2019/06/04]
- Release: 0.0.1
