#!/bin/bash
#info's username
uname='mazm13'
# your info's password's md5sum
pass='c7698e1f3f9d146fd44dd7073da21f2d'
####################################################
do_login() {
  login_data='username='$uname'&password={MD5_HEX}'$pass'&action=login&ac_id=1'
  check_data='action=check_online'

  # check whether already online
  con=`curl -d $check_data -s https://net.tsinghua.edu.cn/do_login.php`

  if [ $con != "online" ]; then
      # start login
      res=`curl -d $login_data -s https://net.tsinghua.edu.cn/do_login.php`

      #handle result
      pe=`echo $res | grep error`
      if [ -z $pe ]; then
          echo "Login Success!"  
      else
          echo $pe
          exit 0
      fi

      res=`curl -s https://net.tsinghua.edu.cn/rad_user_info.php`
      # display flux infomation
      flux=`echo $res | awk -F ',' '{print $4}'`
      a=$(($flux/1000000000))
      b=$((($flux%1000000000)/100000000))
      c=$((($flux%100000000)/10000000))
      echo "Used Flux: "$a"."$b$c"G."   

  else
      echo "Already Online!"

      con=`curl -s https://net.tsinghua.edu.cn/rad_user_info.php`
      # display flux information and online time
      flux=`echo $con | awk -F ',' '{print $4}'`
      # time=`echo $con | awk -F ',' '{print $5}'`
      a=$(($flux/1000000000))
      b=$((($flux%1000000000)/100000000))
      c=$((($flux%100000000)/10000000))
      # h=$(($time/3600))
      # m=$(($(($time%3600))/60))
      # s=$(($(($time%3600))%60))
      echo "Used Flux: "$a"."$b$c"G" #, Online Time: "$h":"$m":"$s"."
  fi
}

do_logout() {
  # start logout
  res=`curl -d 'action=logout' -s http://net.tsinghua.edu.cn/do_logout`

  #handle result
  if [ "$res" == "logout_ok" ]; then
      echo "Logout Success!"
  elif [ "$res" == "not_online_error" ]; then
      echo "You're not Online!"
  else
      echo "Operation Failed!"
  fi
}

#############################################################
if [ "$1" == "login" ]; then
  do_login
elif [ "$1" == "logout" ]; then
  do_logout
else
  echo "Usage: "$0" {login|logout}"
fi
