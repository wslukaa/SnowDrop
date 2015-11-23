
<?php

$cities = array("beijing"=>"北京","shanghai"=>"上海","guangzhou"=>"广州","yinchuan"=>"银川","haerbin"=>"哈尔滨","haikou"=>"海口","wulumuqi"=>"乌鲁木齐","北京"=>"北京","上海"=>"上海","广州"=>"广州","银川"=>"银川","哈尔滨"=>"哈尔滨","海口"=>"海口","乌鲁木齐"=>"乌鲁木齐");

$city = $_GET["city"] ? $_GET["city"] : "beijing";
$city = $cities[$city];
$mysql_server_name = "localhost";
$mysql_username = "root";
$mysql_password = "snowdrop";
$mysql_database = "sdweather";

$conn = mysql_connect("localhost", "root", "snowdrop");
if(!$conn)
{
	die('Could not connect: '.mysql_error());
}
mysql_query("SET NAMES utf8");
$strsql = "SELECT timestamp FROM spot_pm";
$result = mysql_db_query($mysql_database, $strsql, $conn);

mysql_data_seek($result, mysql_num_rows($result)-1);
$current_time = mysql_fetch_row($result)[0];

$str_pm_data = "SELECT DISTINCT position_name,station_code,pm2_5,pm2_5_24,api FROM spot_pm WHERE timestamp='".$current_time."' and area='".$city."'";
$result_pm_data = mysql_db_query($mysql_database, $str_pm_data, $conn);
$pm_data = array();
while($row = mysql_fetch_array($result_pm_data))
{
	$unit_data = array();
	$unit_data['name'] = $row[0];
	$unit_data['code'] = $row[1];
	$unit_data['pm'] = $row[2];
	$unit_data['pm24'] = $row[3];
	$unit_data['aqi'] = $row[4];
	$lresult = mysql_db_query($mysql_database, "SELECT lat,lng FROM station WHERE code = '".$row[1]."'", $conn);
	$latlng = mysql_fetch_row($lresult);
	$unit_data['lat'] = $latlng[0];
	$unit_data['lng'] = $latlng[1];
	$pm_data[] = $unit_data;
}

$output = array();
$output = array('city'=>$city,'time'=>$current_time,'data'=>$pm_data);
echo (json_encode($output, JSON_UNESCAPED_UNICODE));
?>
