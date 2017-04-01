from common.mongo import mongo_cli
import re
import pandas as pd

db_name = 'wfp_com_page'
db = mongo_cli.get_database(db_name)
stocks = [stock for stock in db.collection_names(include_system_collections=False) if re.match('\d+', stock)]
pd.DataFrame(stocks).to_csv('500_stocks.txt', index=False, header=False)
