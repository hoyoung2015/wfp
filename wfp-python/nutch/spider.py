# -*- coding: utf-8 -*-
"""
从nutch采集的mongodb库中导出外部链接的域名
"""
import sys
import os

NUTCH_HOME='/home/hoyoung/workspace/eclipse/nutch-wfp/runtime/local/'
TMP_PATH='../.tmp'

if __name__=='__main__':
    os.environ['NUTCH_CONF']=NUTCH_HOME
    os.system('echo $NUTCH_CONF')
    pass
        
        



    
