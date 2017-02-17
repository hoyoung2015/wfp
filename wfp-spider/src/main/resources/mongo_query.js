// 按照stockCode分组统计
db.getCollection('com_page').group({
	"key" : {
		"stockCode" : true
	},
	"initial" : {
		"count" : 0
	},
	"reduce" : function(obj, prev) {
		prev.count++;
	}
})
db.runCommand({
	"distinct":"com_page",
	"key":"stockCode"
})