#!/usr/bin/env python
#encoding=utf8
import MySQLdb, urllib, urllib2, json, time, sys

url = "http://www.pm25.in/api/querys/pm2_5.json"
allcityurl = "http://www.pm25.in/api/querys/all_cities.json"
my_api_key = "token=5j1znBVAsnSf5xQyNQyq" 
TIMEFORMAT = '%Y-%m-%d %X'

def main():
	#req = urllib2.Request(url+'?'+"city=beijing&"+my_api_key)
	print time.strftime(TIMEFORMAT, time.localtime(time.time()))
	req = urllib2.Request(allcityurl+'?'+my_api_key);
	resp = urllib2.urlopen(req)
	content = resp.read()
	if not content:
		return
	print content
	data = json.loads(content)
	if len(data) == 1 and data.has_key('error'):
		#logfile = open('lf', 'w+')
		#logfile.write('error')
		#logfile.write(errorinfo.encode('utf-8'))
		#logfile.close()
		print 'Error: 使用次数用完了'
		return
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
		iid = "insert into spot_pm (\
			timestamp, \
			position_name, \
			station_code, \
			pm2_5, \
			pm2_5_24, \
			area, \
			api) \
			values (\
			%s, %s, %s, %s, %s, %s, %s)"
		for s in data:
			values = []
			values.append(s['time_point'])
			values.append(s['position_name'])
			values.append(s['station_code'])
			values.append(s['pm2_5'])
			values.append(s['pm2_5_24h'])
			values.append(s['area'])
			values.append(s['aqi'])
			cur.execute(iid, values)
		conn.commit()
		cur.close()
		conn.close()
		print "Successed"

	except MySQLdb.Error,e:
		print "Mysql Error %d: %s" % (e.args[0], e.args[1])

if __name__ == '__main__':
	main()
