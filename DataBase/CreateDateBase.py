
import sqlite3

'''
	create a database file named "weather.db" and 
	two tables, weather for storing pre-existing data,
	predict for storing data that predicted
'''
'''
	Warning：run for one time
'''


def main():
	conn = sqlite3.connect("weather.db")
	c = conn.cursor()
	c.execute("CREATE TABLE weather (num integer PRIMARY KEY, \
		timestamp text, \
		location text, \
		pm real, \
		temperature real, \
		humidity real, \
		presure real, \
		speed real, \
		direction text, \
		dew real, \
		condition text, \
		rain integer)")
	c.execute("CREATE TABLE predict (num integer PRIMARY Key, \
		timestamp text, \
		location text, \
		pm real)")
	c.close()

if __name__ == '__main__':
	main()