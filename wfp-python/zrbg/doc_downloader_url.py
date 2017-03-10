import sys
import os

curPath = os.path.abspath(os.path.dirname(__file__))
rootPath = os.path.split(curPath)[0]
print(rootPath)
sys.path.append(rootPath)

from common.downloader import download
from common.mongo import mongo_cli
import re
import hashlib
from urllib.error import HTTPError
from urllib.error import URLError
from common import log
import logging
import queue
import time
from common.xunleidata import *
import random


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




def a2(taskid, Url, save_path, Name):
    TaskBase(
        TaskId=taskid,
        Type=1,
        Status=5,  # 3 排队. 5 开始. 7暂停. 8完成
        StatusChangeTime=0,
        # StatusChangeTime = 23284358513164200,
        SavePath=save_path,
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


def get_collection_names(db_name):
    db = mongo_cli.get_database(db_name)
    return [name for name in db.collection_names() if re.match('\d{6}', name)]


def split_url(url):
    i = url.rindex('.')
    sha1 = hashlib.sha1()
    sha1.update(url[:i].encode('utf-8'))
    return sha1.hexdigest(), url[i:]


def write_error(message):
    with open('download_error.txt', 'a') as f:
        f.write('%s\t%s\t%s\t%s\n' % message)
        f.close()


def get_proxies(file=''):
    q = queue.Queue()
    with open(file) as f:
        for line in f.readlines():
            line = line.strip().replace('\n', '')
            if line == '' or line.startswith('#'):
                continue
            sp = line.split(',')
            q.put({
                'http': 'http://%s:%s@%s:%s' % tuple(sp)
            })
        f.close()
    return q


if __name__ == '__main__':
    taskid = int(random.randint(10000009937, 99999819937))
    logger = log.get_logger('doc_downloader', 'downloader.log', level=logging.INFO)
    f = open('pdf_url.txt')
    for line in f.readlines():
        line = line.strip().replace('\n', '')
        if line == '' or line.startswith('#'):
            continue
        save_dir = 'D:\\zrbg_pdf\\'
        ss = line.split(',')
        url = ss[1]
        taskid += 1
        try:
            a1(taskid, url)
        except:
            pass
        try:
            a2(taskid, url, save_dir, ss[0] + '.PDF')
        except:
            pass
            # break
    f.close()
