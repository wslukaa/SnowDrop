<?php
$output = array();
$spot = @$_GET['spot'] ? @$_GET['spot'] : '';

$output = array('pm'=>20);
exit(json_encode($output));

