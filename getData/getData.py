# -*- coding: GBK -*-
import sys, urllib, urllib2, json, MySQLdb, time

url = "http://www.pm25.in/api/querys/pm2_5.json"
my_api_key = "token=5j1znBVAsnSf5xQyNQyq"
ISOTIMEFORMAT='%Y-%m-%d %X'

def main():
	
	req = urllib2.Request(url+'?'+"city=beijing&"+my_api_key)
	resp = urllib2.urlopen(req)
	content = resp.read()
	if not content:
		return
	spots_info = json.loads(content)
	if len(spots_info) == 1 and spots_info.has_key('error'):
		errorinfo = time.strftime(ISOTIMEFORMAT, time.localtime(time.time())) + ' Error: ' + spots_info['error']
		logfile = open('lf', 'w')
		logfile.write(errorinfo)
		logfile.close()
		return
	insertIntoDB(spots_info)
		#, spot_info["station_code"], spot_info["pm2_5"]

def insertIntoDB(spots_info):
	try:
		conn = MySQLdb.connect(
			host = "localhost",
			user = "mazm13",
			passwd = "NAY9ZLLZ",
			db = "test"
			)
		cur = conn.cursor()
		idt = "insert into station_pm (\
			timestamp, \
			position_name, \
			station_code, \
			pm2_5, \
			pm2_5_24, \
			area, \
			api) \
			values (\
			%s, %s, %s, %s, %s, %s, %s)"
		for s in spots_info:
			values = []
			values.append(s['time_point'])
			values.append(s['position_name'])
			values.append(s['station_code'])
			values.append(s['pm2_5'])
			values.append(s['pm2_5_24h'])
			values.append(s['area'])
			values.append(s['api'])
			cur.execute(idt, values)
		con.commit()
		cur.close()
		conn.close()
	except MySQLdb.Error,e:
		print "MySQLdb Error %d: %s" % (e.args[0], e.args[1])
if __name__ == "__main__":
	main()