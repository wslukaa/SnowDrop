# -*- coding: utf-8 -*-
import sys, urllib, urllib2, json

url = 'http://apis.baidu.com/heweather/weather/free?city=beijing'

def main():
	req = urllib2.Request(url)
	req.add_header("apikey", "28d1f38f1dd8b642fbb16307c245e39e")
	resp = urllib2.urlopen(req)
	content = resp.read()
	if not content:
	    print "No content"
	print content
	data = json.loads(content)

def insertIntoDB(data):	
	try:
		conn = MySQL.connect(
			host = 'localhost',
			user = 'root',
			passwd = 'snowdrop',
			db = 'sdweather',
			charset = 'utf8'
			)
		cur = conn.cursor()
		iid = "insert into weather (\
			timestamp, \
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
		print "Successed"

	except MySQLdb.Error,e:
		print "Mysql Error %d: %s" % (e.args[0], e.args[1])

if __name__ == '__main__':
	main()