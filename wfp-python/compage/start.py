# -*- coding: utf-8 -*-
import sys
import csv, json
import random
import sqlobject
from sqlobject import *
from common.xunleidata import *


# 运行start.py       by johnny
# C:\Program Files (x86)\Thunder Network\Thunder\Profiles\TaskDb.dat
# TaskDb.dat 文件为迅雷任务存储文件，请将本脚本放在TaskDb.dat同一文件夹下运行
# 必备文件 TaskDb.dat,test.csv；TaskDb.dat为本机测试文件，每个迅雷Profiles目录存都存在TaskDb.dat文件
# 迅雷自定义添加下载任务。运行前请关闭迅雷，并检查以上文件是否存在
# Status = 7, #3 排队. 5 开始. 7暂停. 8完成
# test.csv文件格式如下：url,filename
# http://down.x.com/x/downbook.php?id=NS8JEjguDxg%3D,0.f4v
# http://down.x.com/x/downbook.php?id=NS8JEjguDx8%3D,1.f4v


def a1(taskid, DisplayUrl):
    P2spTask(
        Id=taskid,
        ResourceUsageStrategy=0,
        ResourceReportStrategy=0,
        Cookie='',
        AccountNeeded=0,
        UserName='',
        Password='',
        UseOriginResourceOnly=0,
        OriginResourceSupportRange=1,
        ReceiveOriginResourceSize=0,
        OriginResourceRetryInterval=-1,
        OriginResourceRetryTimes=-1,
        OriginResourceThreadCount=5,
        DownloadSpeedLimit=0,
        FileNameFixed=0,
        HttpRequestHeader=None,
        VideoHeadFirstTime=-1,
        VideoHeadFirstStatus=0,
        ResourceQuerySize=0,
        DisplayUrl=DisplayUrl,
        UserAgent=''
    )


# P2spTask._connection.debug = True




def a2(taskid, Url, Name):
    TaskBase(
        TaskId=taskid,
        Type=1,
        Status=5,  # 3 排队. 5 开始. 7暂停. 8完成
        StatusChangeTime=0,
        # StatusChangeTime = 23284358513164200,
        SavePath='C:\迅雷下载\\',
        TotalReceiveSize=0,
        TotalSendSize=0,
        TotalReceiveValidSize=0,
        TotalUploadSize=0,
        CreationTime=0,
        # CreationTime = 23284358328614900,
        FileCreated=0,
        CompletionTime=0,
        DownloadingPeriod=0,
        RemovingToRecycleTime=0,
        FailureErrorCode=0,
        Url=Url,
        ReferenceUrl='',
        ResourceSize=0,
        Name=Name,
        Cid=None,
        Gcid=None,
        Description='',  # 备注名称
        CategoryId=-1,
        ResourceQueryCid=None,
        CreationRequestType=0,
        StartMode=0,
        NamingType=1,
        StatisticsReferenceUrl='',
        UserRead=0,
        FileSafetyFlag=0,
        Playable=0,
        BlockInfo=None,
        OpenOnComplete=0,
        SpecialType=-1,
        Proxy=None,
        OriginReceiveSize=0,
        P2pReceiveSize=0,
        P2sReceiveSize=0,
        OfflineReceiveSize=0,
        VipReceiveSize=0,
        VipResourceEnableNecessary=0,
        ConsumedVipSize=0,
        Forbidden=0,
        OptionalChannelDataSize=None,
        OwnerProductId=0,
        UserData=None,
        UrlCodePage=-1,
        ReferenceUrlCodePage=-1,
        StatisticsReferenceUrlCodePage=-1,
        GroupTaskId=-1,
        DownloadSubTask=0,
        TagValue=0,
        InnerNatReceiveSize=0,
        AdditionFlag=0, )


i = 0

taskid = int(random.randint(10000009937, 99999819937))
csvfile = open('xunlei.csv')
for line in csvfile.readlines():
    line = line.strip().replace('\n', '')
    sp = line.split(',')
    i = i + 1
    taskid = taskid + 67108865

    print(taskid, i, sp[0], sp[1])
    DisplayUrl = sp[0]

    Url = DisplayUrl
    Name = sp[1]

    try:
        a1(taskid, DisplayUrl)

    except:
        pass
    # n1 = P2spTask.get(taskid)

    try:
        a2(taskid, Url, Name)
    except:
        pass
        # n2 = TaskBase.get(taskid)

csvfile.close()
