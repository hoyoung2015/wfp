import sqlobject
from sqlobject.sqlite import builder

conn = builder()('sql.dat')


class User(sqlobject.SQLObject):
    _connection = conn
    name = sqlobject.StringCol(length=10, notNone=True)
    age = sqlobject.IntCol(length=10)


User.createTable(ifNotExists=True)

user1 = User(name='hoyoung', age=26)
