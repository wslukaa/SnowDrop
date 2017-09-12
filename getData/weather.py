#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys, urllib, urllib2, json, MySQLdb, time

url = 'http://apis.baidu.com/heweather/weather/free?city=beijing'
TIMEFORMAT = '%Y-%m-%d %X'

def main():
	lt = time.strftime(TIMEFORMAT, time.localtime(time.time()))
	req = urllib2.Request(url)
	req.add_header("apikey", "28d1f38f1dd8b642fbb16307c245e39e")
	resp = urllib2.urlopen(req)
	content = resp.read()
	if not content:
	    print lt, "No content"
	#print content
	data = json.loads(content)
	insertIntoDB(data, lt)

def insertIntoDB(data, lt):	
	try:
		conn = MySQLdb.connect(
			host = 'localhost',
			user = 'root',
			passwd = 'snowdrop',
			db = 'sdweather',
			charset = 'utf8'
			)
		cur = conn.cursor()
		iid = "insert into weather (\
			timetamp, \
			location, \
			tmp, \
			pm, \
			hum, \
			pcpn, \
			pres, \
			deg, \
			dir, \
			sc, \
			spd, \
			cond_code, \
			cond_txt) values (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"

		rd = data['HeWeather data service 3.0'][0] #realdata
		values = []
		values.append(rd['basic']['update']['loc'])
		values.append(rd['basic']['city'])
		values.append(rd['now']['tmp'])
		values.append(rd['aqi']['city']['pm25'])
		values.append(rd['now']['hum'])
		values.append(rd['now']['pcpn'])
		values.append(rd['now']['pres'])
		values.append(rd['now']['wind']['deg'])
		values.append(rd['now']['wind']['dir'])
		values.append(rd['now']['wind']['sc'])
		values.append(rd['now']['wind']['spd'])
		values.append(rd['now']['cond']['code'])
		values.append(rd['now']['cond']['txt'])
		
		cur.execute(iid, values)
		conn.commit()
		cur.close()
		conn.close()
		print lt, "Successed"

	except MySQLdb.Error,e:
		print "Mysql Error %d: %s" % (e.args[0], e.args[1])

if __name__ == '__main__':
	main()
