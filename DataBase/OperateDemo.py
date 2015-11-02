
import sqlite3

'''
num integer PRIMARY KEY, 
timestamp text, 
location text, 
pm real, 
temperature real, 
humidity real, 
presure real, 
speed real, 
direction text, 
dew real, 
condition text, 
rain integer
'''

def insertDemo(conn):
	c = conn.cursor()
	num = 1;
	timestamp = "2015-10-28-08"
	location = "beijing"
	pm = 318
	temperature = 10
	humidity = 80
	presure = 1029
	speed = 3.6
	direction = "ENE"
	dew = -10
	condition = "Mist"
	rain = 0
	
	c.execute("INSERT INTO weather (num, timestamp, location, pm, temperature, humidity, presure, speed, direction, dew, condition, rain) VALUES ("
		+ str(num) + ","\
		+ "\'" + timestamp + "\',"\
		+ "\'" + location + "\',"\
		+ str(pm) + ","\
		+ str(temperature) + ","\
		+ str(humidity) + ","\
		+ str(presure) + ","\
		+ str(speed) + ","\
		+ "\'" + direction + "\',"\
		+ str(dew) + ","\
		+ "\'" + condition + "\',"\
		+ str(rain) + ")")
	
	'''
	#if we just want to insert some values, we can do like this
	c.execute("INSERT INTO weather (num, pm) VALUES ("\
		+ str(num) + "," + str(pm) + ")")
	c.execute("UPDATE weather SET location = \'"+ location +"\' WHERE num = 1")
	'''
	
def updateDemo(conn, pm):
	c = conn.cursor()
	c.execute("UPDATE weather SET pm = " + str(pm) + " WHERE num = 1")

def getPm():
	return 150

def main():
	conn = sqlite3.connect("weather.db")
	conn.execute("delete from weather")
	insertDemo(conn)
	updateDemo(conn, getPm())
	conn.commit()
	conn.close()
	
if __name__ == '__main__':
	main()