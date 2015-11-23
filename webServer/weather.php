
<?php

$cities = array("beijing"=>"北京", "shanghai"=>"上海", "guangzhou"=>"广州", "yinchuan"=>"银川", "haikou"=>"海口", "haerbin"=>"哈尔滨", "wulumuqi"=>"乌鲁木齐", "北京"=>"北京", "上海"=>"上海", "广州"=>"广州", "银川"=>"银川", "海口"=>"海口", "哈尔滨"=>"哈尔滨", "乌鲁木齐"=>"乌鲁木齐");

$city = $_GET["city"] ? $_GET["city"] : "beijing";
$city = $cities[$city];

$conn = mysql_connect("localhost", "root", "snowdrop");
if(!$conn)
{
	die('Could not connect: '.mysql_error());
}
mysql_query("SET NAMES utf8");
$strw = "SELECT timetamp, tmp, pm, hum, pcpn, pres, deg, dir, sc, spd, cond_code, cond_txt, pm10, sqi, fl, vis, sport_sug, sport_txt FROM citiesweather WHERE location = '".$city."'";
$strf = "SELECT date, hum, pcpn, pop, pres, tmpmax, tmpmin, vis, winddeg, winddir, windsc, windspd, cond_d, cond_n, txt_d, txt_n FROM forecast WHERE area = '".$city."'";

$result = mysql_db_query("sdweather", $strw, $conn);
mysql_data_seek($result, mysql_num_rows($result)-1);
$dailydata = mysql_fetch_row($result);
$aqi = array("city"=>array("aqi"=>$dailydata[13],"pm25"=>$dailydata[2], "pm10"=>$dailydata[12]));
$wind = array("deg"=>$dailydata[6], "dir"=>$dailydata[7], "sc"=>$dailydata[8], "spd"=>$dailydata[9]);
$now = array("cond"=>array("code"=>$dailydata['10'], "txt"=>$dailydata[11]), "fl"=>$dailydata[14],"hum"=>$dailydata[3], "pcpn"=>$dailydata[4], "pres"=>$dailydata[5], "tmp"=>$dailydata[1], "vis"=>$dailydata[15],"wind"=>$wind);
$suggestion = array("sport"=>array("brf"=>$dailydata[16], "txt"=>$dailydata[17]));

$fcresult = mysql_db_query("sdweather", $strf, $conn);
mysql_data_seek($fcresult, mysql_num_rows($fcresult)-7);
$daily_forecast = array();
while($day = mysql_fetch_array($fcresult)) {
	$unit = array();
	$unit["cond"] = array("code_d"=>$day[12], "code_n"=>$day[13], "txt_d"=>$day[14], "txt_n"=>$day[15]);
	$unit["date"] = $day[0];
	$unit["hum"] = $day[1];
	$unit["pcpn"] = $day[2];
	$unit["pop"] = $day[3];
	$unit["pres"] = $day[4];
	$unit["tmp"] = array("max"=>$day[5], "min"=>$day[6]);
	$unit["vis"] = $day[7];
	$unit["wind"] = array("deg"=>$day[8], "dir"=>$day[9], "sc"=>$day[10], "spd"=>$day[11]);
	$daily_forecast[] = $unit;
}

$output = array();
$output = array("SnowdropWeather"=>array("basic"=>array("update"=>array("loc"=>$dailydata[0])) ,"aqi"=>$aqi, "now"=>$now, "suggestion"=>$suggestion,"daily_forecast"=>$daily_forecast));
echo json_encode($output, JSON_UNESCAPED_UNICODE);
?>
