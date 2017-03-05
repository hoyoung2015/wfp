#!/bin/sh
min=1
max=10000000
while [ $min -le $max ]
do
    /Users/baidu/local/anaconda3/bin/python3.5 /Users/baidu/workspace/wfp/wfp-python/compage/doc_downloader.py
    min=`expr $min + 1`
done