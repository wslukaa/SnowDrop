
<?php
$cities = array("beijing"=>"北京","shanghai"=>"上海","guangzhou"=>"广州","yinchuan"=>"银川","haerbin"=>"哈尔滨","haikou"=>"海口","wulumuqi"=>"乌鲁木齐","北京"=>"北京","上海"=>"上海","广州"=>"广州","银川"=>"银川","哈尔滨"=>"哈尔滨","海口"=>"海口","乌鲁木齐"=>"乌鲁木齐");
$city = $_GET["city"] ? $_GET["city"] : "beijing";
$city = $cities[$city];
$conn = mysql_connect("localhost", "root", "snowdrop");
if(!$conn) {
	die('Could not connect: '.mysql_error());
}
mysql_query("SET NAMES utf8");
$strsql = "SELECT pm1, pm2, pm3, pm4, pm5, pm6 FROM predict WHERE area='".$city."' order by id";
$result = mysql_db_query("sdweather", $strsql, $conn);

mysql_data_seek($result, mysql_num_rows($result)-1);
$data = mysql_fetch_row($result);

$output = array();
$output = array('data'=>$data);
echo (json_encode($output, JSON_UNESCAPED_UNICODE));
?>
