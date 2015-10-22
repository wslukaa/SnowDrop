import sqlite3
conn = sqlite3.connect('update.db')
    #	c = conn.execute("SELECT timestamp, pm FROM airweather")
    #	for row in c:
    #		if row[0] == inputTime:
    #			print "timestamp = ", row[0], "pm = ", row[1]
    #    print "Operation done Successfully"
c = conn.execute("SELECT num, timestamp, temperature, humidity, pressure, speed, condition, pm, dew, rain, direction  FROM airweather")
    
    # row[1]->temperature row[6]->pm
for row in c:
    flag = 0
    for j in range(11):
        print row[j],
    print ""
#    cnt+=1
#    database.append(row)
    
    #    print database[0][0]
    #    print cnt
    
    
    #    conn.execute("HELP")
conn.close()
