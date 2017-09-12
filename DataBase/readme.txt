DataBase
mySQL + php + apache2
mySQL
|--user: root
|--password: snowdrop
~--DataBase: sdweather
    ~--table
	|--spot_pm, record every spot's pm in every city [ok]
	|--weather, just weather's orighinal data [ok, and can be traindata]
	|--traindata, after clean and be the train data for predict [ok]
	~--predict, predict result
php
|--lookup.php: api, and look up data from spot's pm [ok]
~--predict.php: api, and look up data from predict

python
|--insert.py, get data from www.2_5pm.in [ok]
~--weatherinsert.py, get data from... [ok]

crontab -l
|--30 * * * * /home/snowdrop/py/insert.py >> /home/snowdrop/py/insert.log 2>&1 [ok]
~--30 * * * * /home/snowdrop/py/weatherinsert.py >> /home/snowdrop/py/weatherinsert.log 2>&1