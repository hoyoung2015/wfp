// 查询所有行业
db.runCommand({
	"distinct" : "company_info",
	"key" : "industry"
})

// 查询目标行业
db.getCollection('company_info').find(
		{
			'industry' : {
				$in : [ "石油勘探与开采", "煤炭开采", "汽车", "电力（IV）", "工业机械", "钢铁",
						"电子电气组件与设备", "金矿开采", "有色金属", "通用化工品", "普通矿开采", "油气综采",
						"铂与贵金属开采", "铝业", "工业金属", "采矿业", "建筑与材料（III）",
						"汽车与零配件（II）", "建筑与材料（II）", "电力（III）" ]
			}
		}).sort({'shareholders':-1})
		
//数据库重命名		
db.copyDatabase('old_name', 'new_name'); 
use old_name 
db.dropDatabase(); 