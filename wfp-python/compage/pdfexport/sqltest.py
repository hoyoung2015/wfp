import sqlite3

# conn = sqlite3.connect('/media/hoyoung/win7 home/Program Files/Thunder Network/Thunder9/Profiles/TaskDb.dat')
conn = sqlite3.connect('TaskDb.dat')

cursor = conn.execute("""
select a.DisplayUrl,b.Status from P2spTask a
left join TaskBase b on a.TaskId=b.TaskId
where b.Status!=8
""")

for row in cursor:
    print(row)



conn.close()