#!/usr/bin/python
# -*- coding: utf-8 -*-
import sqlobject
from sqlobject.sqlite import builder

conn = builder()('TaskDb.dat')


class P2spTask(sqlobject.SQLObject):
    _connection = conn
    # app_id = sqlobject.StringCol(length=14, unique=True)
    Id = sqlobject.IntCol(length=0, dbName='TaskId', alternateID=False)
    ResourceUsageStrategy = sqlobject.IntCol(length=0, dbName='ResourceUsageStrategy')
    ResourceReportStrategy = sqlobject.IntCol(length=0, dbName='ResourceReportStrategy')
    Cookie = sqlobject.StringCol(length=0)
    AccountNeeded = sqlobject.IntCol(length=0, dbName='AccountNeeded')
    UserName = sqlobject.StringCol(length=0, dbName='UserName')
    Password = sqlobject.StringCol(length=0)
    UseOriginResourceOnly = sqlobject.IntCol(length=0, dbName='UseOriginResourceOnly')
    OriginResourceSupportRange = sqlobject.IntCol(length=0, dbName='OriginResourceSupportRange')
    ReceiveOriginResourceSize = sqlobject.IntCol(length=0, dbName='ReceiveOriginResourceSize')
    OriginResourceRetryInterval = sqlobject.IntCol(length=0, dbName='OriginResourceRetryInterval')
    OriginResourceRetryTimes = sqlobject.IntCol(length=0, dbName='OriginResourceRetryTimes')
    OriginResourceThreadCount = sqlobject.IntCol(length=0, dbName='OriginResourceThreadCount')
    DownloadSpeedLimit = sqlobject.IntCol(length=0, dbName='DownloadSpeedLimit')
    FileNameFixed = sqlobject.IntCol(length=0, dbName='FileNameFixed')
    HttpRequestHeader = sqlobject.BLOBCol(length=0, dbName='HttpRequestHeader')
    VideoHeadFirstTime = sqlobject.IntCol(length=0, dbName='VideoHeadFirstTime')
    VideoHeadFirstStatus = sqlobject.IntCol(length=0, dbName='VideoHeadFirstStatus')
    ResourceQuerySize = sqlobject.IntCol(length=0, dbName='ResourceQuerySize')
    DisplayUrl = sqlobject.StringCol(length=0, dbName='DisplayUrl')
    UserAgent = sqlobject.StringCol(length=0, dbName='UserAgent')

    class sqlmeta:
        table = 'P2spTask'
        # idName = 'TaskId'


class TaskBase(sqlobject.SQLObject):
    _connection = conn
    # app_id = sqlobject.StringCol(length=14, unique=True)
    TaskId = sqlobject.IntCol(length=0, dbName='TaskId')
    Type = sqlobject.IntCol(length=0, dbName='Type')
    Status = sqlobject.IntCol(length=0, dbName='Status')
    StatusChangeTime = sqlobject.IntCol(length=0, dbName='StatusChangeTime')
    SavePath = sqlobject.StringCol(length=0, dbName='SavePath')
    TotalReceiveSize = sqlobject.IntCol(length=0, dbName='TotalReceiveSize')
    TotalSendSize = sqlobject.IntCol(length=0, dbName='TotalSendSize')
    TotalReceiveValidSize = sqlobject.IntCol(length=0, dbName='TotalReceiveValidSize')
    TotalUploadSize = sqlobject.IntCol(length=0, dbName='TotalUploadSize')
    CreationTime = sqlobject.IntCol(length=0, dbName='CreationTime')
    FileCreated = sqlobject.IntCol(length=0, dbName='FileCreated')
    CompletionTime = sqlobject.IntCol(length=0, dbName='CompletionTime')
    DownloadingPeriod = sqlobject.IntCol(length=0, dbName='DownloadingPeriod')
    RemovingToRecycleTime = sqlobject.IntCol(length=0, dbName='RemovingToRecycleTime')
    FailureErrorCode = sqlobject.IntCol(length=0, dbName='FailureErrorCode')
    Url = sqlobject.StringCol(length=0, dbName='Url')
    ReferenceUrl = sqlobject.StringCol(length=0, dbName='ReferenceUrl')
    ResourceSize = sqlobject.IntCol(length=0, dbName='ResourceSize')
    Name = sqlobject.StringCol(length=0, dbName='Name')
    Cid = sqlobject.BLOBCol(length=0, dbName='Cid')
    Gcid = sqlobject.BLOBCol(length=0, dbName='Gcid')
    Description = sqlobject.StringCol(length=0, dbName='Description')
    CategoryId = sqlobject.IntCol(length=0, dbName='CategoryId')
    ResourceQueryCid = sqlobject.BLOBCol(length=0, dbName='ResourceQueryCid')
    CreationRequestType = sqlobject.IntCol(length=0, dbName='CreationRequestType')
    StartMode = sqlobject.IntCol(length=0, dbName='StartMode')
    NamingType = sqlobject.IntCol(length=0, dbName='NamingType')
    StatisticsReferenceUrl = sqlobject.StringCol(length=0, dbName='StatisticsReferenceUrl')
    UserRead = sqlobject.IntCol(length=0, dbName='UserRead')
    FileSafetyFlag = sqlobject.IntCol(length=0, dbName='FileSafetyFlag')
    Playable = sqlobject.IntCol(length=0, dbName='Playable')
    BlockInfo = sqlobject.BLOBCol(length=0, dbName='BlockInfo')
    OpenOnComplete = sqlobject.IntCol(length=0, dbName='OpenOnComplete')
    SpecialType = sqlobject.IntCol(length=0, dbName='SpecialType')
    Proxy = sqlobject.BLOBCol(length=0, dbName='Proxy')
    OriginReceiveSize = sqlobject.IntCol(length=0, dbName='OriginReceiveSize')
    P2pReceiveSize = sqlobject.IntCol(length=0, dbName='P2pReceiveSize')
    P2sReceiveSize = sqlobject.IntCol(length=0, dbName='P2sReceiveSize')
    OfflineReceiveSize = sqlobject.IntCol(length=0, dbName='OfflineReceiveSize')
    VipReceiveSize = sqlobject.IntCol(length=0, dbName='VipReceiveSize')
    VipResourceEnableNecessary = sqlobject.IntCol(length=0, dbName='VipResourceEnableNecessary')
    ConsumedVipSize = sqlobject.IntCol(length=0, dbName='ConsumedVipSize')
    Forbidden = sqlobject.IntCol(length=0, dbName='Forbidden')
    OptionalChannelDataSize = sqlobject.BLOBCol(length=0, dbName='OptionalChannelDataSize')
    OwnerProductId = sqlobject.IntCol(length=0, dbName='OwnerProductId')

    UserData = sqlobject.BLOBCol(length=0, dbName='UserData')
    # UserData = sqlobject.StringCol(length=0, dbName='UserData')

    UrlCodePage = sqlobject.IntCol(length=0, dbName='UrlCodePage')
    ReferenceUrlCodePage = sqlobject.IntCol(length=0, dbName='ReferenceUrlCodePage')
    StatisticsReferenceUrlCodePage = sqlobject.IntCol(length=0, dbName='StatisticsReferenceUrlCodePage')
    GroupTaskId = sqlobject.IntCol(length=0, dbName='GroupTaskId')
    DownloadSubTask = sqlobject.IntCol(length=0, dbName='DownloadSubTask')
    TagValue = sqlobject.IntCol(length=0, dbName='TagValue')
    InnerNatReceiveSize = sqlobject.IntCol(length=0, dbName='InnerNatReceiveSize')
    AdditionFlag = sqlobject.IntCol(length=0, dbName='AdditionFlag')

    class sqlmeta:
        table = 'TaskBase'
        # idName = 'TaskId'


# P2spTask.createTable(ifNotExists=True)
# TaskBase.createTable(ifNotExists=True)
