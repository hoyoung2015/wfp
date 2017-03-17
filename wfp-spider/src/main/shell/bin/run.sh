#!/bin/sh
CONF_SH="/home/hoyoung/.bashrc"
[ -f "${CONF_SH}" ] && source $CONF_SH || echo "not exist ${CONF_SH} "
CONF_SH="../conf/common.conf"
[ -f "${CONF_SH}" ] && source $CONF_SH || echo "not exist ${CONF_SH} "
CONF_SH="../conf/classpath_recommend.conf"
[ -f "${CONF_SH}" ] && source $CONF_SH || echo "not exist ${CONF_SH} "

mainClass="net.hoyoung.wfp.spider.comweb.ComWebSpider"
$JAVA -classpath ${CUR_CLASSPATH} $mainClass