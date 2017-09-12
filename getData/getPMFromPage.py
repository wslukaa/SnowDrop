#!/usr/bin/env python
# -*- coding: utf-8 -*-

import re
import urllib
import time, sys, MySQLdb

cities = ["beijing", "shanghai", "yinchuan", "wulumuqi", "haerbin", "haikou", "guangzhou"]
cities_name = ["北京", "上海", "银川", "乌鲁木齐", "哈尔滨", "海口", "广州"]
urladr = "http://www.pm25s.com/"
TIMEFORMAT = '%Y-%m-%d %X'

def main():

	ti = time.strftime(TIMEFORMAT, time.localtime(time.time()))

	conn = MySQLdb.connect(
		host = 'localhost',
		user = 'root',
		passwd = 'snowdrop',
		db = 'sdweather',
		charset = 'utf8'
		)
	cur = conn.cursor()
	iid = "insert into insect (area, name, pm25, timestamp) values ( %s, %s, %s, %s )"
	f = file("text.txt", "w")
	for i in range(0, len(cities)):
		f.write(cities_name[i]+':\n')
		area = cities_name[i]
		addr = urladr + cities[i] + ".html"
		html = urllib.urlopen(addr).read()
		reg = r'class="site">([\x80-\xff]+)</span><span class="\w+">\d+</span><span class="aqis">(\d+)</span>'
		pattern = re.compile(reg)
		lt = pattern.findall(html)
		for r in lt:
			values = []
			values.append(area)
			values.append(r[0])
			values.append(r[1])
			values.append(ti)
			cur.execute(iid, values)
		conn.commit()
	cur.close()
	conn.close()
	print "Successed"

if __name__ == '__main__':
	main()
