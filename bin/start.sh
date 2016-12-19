#!/bin/sh
#-------------------------------------------------------------------------------------------------------------
#该脚本的使用方式为-->[sh startup.sh]
#该脚本可在服务器上的任意目录下执行,不会影响到日志的输出位置等
#-------------------------------------------------------------------------------------------------------------
#JAVA_HOME="/usr/java/jdk1.6.0_31"
JAVA_OPTS="-Duser.timezone=GMT+8 -server -Xms448m -Xmx448m"
APP_LOG=/code/ants-java/bin
APP_HOME=/code/ants-java
APP_MAIN=org.wcong.ants.Ants
CLASSPATH=.
for tradePortalJar in "$APP_HOME"/libs/*.jar;
do
   CLASSPATH="$CLASSPATH":"$tradePortalJar"
done

tradePortalPID=0

getTradeProtalPID(){
    javaps=`jps -l | grep $APP_MAIN`
    if [ -n "$javaps" ]; then
        tradePortalPID=`echo $javaps | awk '{print $1}'`
    else
        tradePortalPID=0
    fi
}

startup(){
    getTradeProtalPID
    echo "================================================================================================================"
    if [ $tradePortalPID -ne 0 ]; then
        echo "$APP_MAIN already started(PID=$tradePortalPID)"
        echo "================================================================================================================"
    else
        echo -n "Starting $APP_MAIN"
        nohup java $JAVA_OPTS -classpath $CLASSPATH $APP_MAIN > $APP_LOG/nohup.log &
        getTradeProtalPID
        if [ $tradePortalPID -ne 0 ]; then
            echo "(PID=$tradePortalPID)...[Success]"
            echo "================================================================================================================"
        else
            echo "[Failed]"
            echo "================================================================================================================"
        fi
    fi
}

startup

