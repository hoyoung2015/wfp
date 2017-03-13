from flask import Flask, render_template, request
from pymongo import MongoClient

app = Flask(__name__)
client = MongoClient(host='127.0.0.1', port=27017)
db = client.get_database('wfp_com_page')
last_url_collection = client.get_database('wfp_com_page_note').get_collection('com_page_last_url')


@app.route('/', methods=['POST', 'GET'])
def check(name=None):
    if request.method == 'POST':
        stock_code = request.form['stockCode'].strip()
        url = request.form['url'].strip()
        if stock_code == '' or url == '':
            return render_template('check.html', name=name)
        count = db.get_collection(stock_code).count({
            'url': url
        })
        last_url_collection.find_one_and_replace({'stockCode': stock_code}, {
            'stockCode': stock_code,
            'lastUrl': url,
            'count': count
        }, upsert=True)
        if count > 0:
            s = '存在-1'
        else:
            s = '不存在-0'
        name = '[%s] %s\n%s' % (stock_code, url, s)
    return render_template('check.html', name=name)


if __name__ == '__main__':
    app.run(port=8080, host='0.0.0.0')
