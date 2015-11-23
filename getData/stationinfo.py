
#encoding=utf8
import sys, urllib, urllib2, json, time, MySQLdb

url = 'http://www.pm25.in/api/querys/station_names.json?token=5j1znBVAsnSf5xQyNQyq'

def main():
	req = urllib2.Request(url)
	resp = urllib2.urlopen(req)
	content = resp.read();
	if not content:
		print "Not json"
		return;
	data = json.loads(content)
	if len(data) == 1:
		print data;
		return;
	insertIntoDB(data)

def insertIntoDB(data):
	try:
		conn = MySQLdb.connect(
			host = 'localhost',
			user = 'root',
			passwd = 'snowdrop',
			db = 'sdweather',
			charset = 'utf8'
		)
		cur = conn.cursor()
		iid = "insert into station (name, code, area) values (%s, %s, %s)"
		for city in data:
			cityname = city['city']
			for station in city['stations']:
				values = []
				values.append(station['station_name'])
				values.append(station['station_code'])
				values.append(cityname)
				cur.execute(iid, values)
		conn.commit()
		cur.close()
		conn.close()
		print "Successed"

	except MySQLdb.Error,e:
		print "Mysql Error %d: %s" % (e.args[0], e.args[1])
			
if __name__ == '__main__':
	main()
