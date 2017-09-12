#encoding=utf8
import MySQLdb, urllib, urllib2, json, time, sys

url = "http://api.map.baidu.com/place/v2/search"
url_default = "&ak=M7sDWVlmcSWnot4AOMoq0wLO&output=json&page_size=1&page_num=0&scope=1"

def main():
	#req = urllib2.Request(url+'?'+"city=beijing&"+my_api_key)
	x = '北京'
	print x
	insertIntoDB();

def insertIntoDB():
	try:
		conn = MySQLdb.connect(
			host = 'localhost',
			user = 'root',
			passwd = 'snowdrop',
			db = 'sdweather',
			charset = 'utf8'
			)
		cur = conn.cursor()
		cur.execute("select * from station")
		results = cur.fetchall()
		for r in results:
#			print r[0],r[1];
			
			url_search = "&query="+r[0].encode('utf8')+"&region="+r[1].encode('utf8')
#			print url_search
			if r[0].encode('utf8')=='22中南校区':
				url_search = "&query="+'22中'+"&region="+r[1].encode('utf8')
			req = urllib2.Request(url+'?'+url_default+url_search);
			resp = urllib2.urlopen(req)
			content = resp.read()
			if not content:
				continue
		
			data = json.loads(content)
#			print data		
			iid = "update station set lat = %s, lng = %s where name = %s and area = %s"
			tmp = data['results']
			if not tmp:
				continue
			s = tmp[0]
#			print '1111'
			values = []
			if 'location' in s:
				aa = s['location'];
				values.append(aa['lat'])
				values.append(aa['lng'])
				values.append(r[0])
				values.append(r[1])
				cur.execute(iid, values)
			else:
				values.append('0')
				values.append('0')
				values.append(r[0])
				values.append(r[1])
				cur.execute(iid, values)
		conn.commit()
		cur.close()
		conn.close()
		print "Successed"

	except MySQLdb.Error,e:
		print "Mysql Error %d: %s" % (e.args[0], e.args[1])
	except IndexError, e:
		print 'ss'

if __name__ == '__main__':
	main()
