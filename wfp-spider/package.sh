#!/bin/sh
mvn clean package -Dmaven.test.skip=true
cd target
tar xvf wfp-spider-0.0.1-SNAPSHOT.tar.gz
cd wfp-spider-0.0.1-SNAPSHOT/bin
#nohup sh run.sh &
#tail -f nohup.out
