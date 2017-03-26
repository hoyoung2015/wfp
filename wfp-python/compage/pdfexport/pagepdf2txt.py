import os
import re
from common.pdfutils import extract_text
from common.mongo import mongo_cli
import pymongo

"""
将下载的pdf转为txt
保持原目录结构不变
"""

if __name__ == '__main__':
    pdf_errors = mongo_cli.get_database('tmp').get_collection('pdf_errors')
    pdf_errors.create_index([('pdf_path', pymongo.ASCENDING)], unique=True)

    pdf_path = '/media/hoyoung/win7 home/compage'
    txt_path = '/home/hoyoung/tmp/compage_txt'

    if os.path.exists(txt_path) is False:
        os.makedirs(txt_path)

    stocks = os.listdir(pdf_path)
    cnt_stock = 0
    for stock in stocks:
        cnt_stock += 1
        stock_pdf_dir = pdf_path + '/' + stock
        stock_txt_dir = txt_path + '/' + stock
        if os.path.exists(stock_txt_dir) is False:
            os.makedirs(stock_txt_dir)
        stock_pdf_files = [x for x in os.listdir(stock_pdf_dir) if re.match('.+\.(pdf|PDF)$', x)]
        total_pdf_files = len(stock_pdf_files)
        cnt_pdf_files = 0
        for pdf in stock_pdf_files:
            cnt_pdf_files += 1
            print('\r%d-%s\t%d/%d' % (cnt_stock, stock, cnt_pdf_files, total_pdf_files), end='')
            name = pdf[:pdf.rindex('.')]
            out_file = stock_txt_dir + '/' + name + '.txt'
            # print(stock_pdf_dir + '/' + pdf, out_file)
            if os.path.exists(out_file) and os.path.getsize(out_file) > 0:
                continue
            if pdf_errors.count({'pdf_path': stock_pdf_dir + '/' + pdf}) > 0:
                continue
            try:
                extract_text(files=[stock_pdf_dir + '/' + pdf], outfile=out_file)
            except Exception as e:
                try:
                    pdf_errors.insert_one({'stockCode': stock, 'pdf_path': stock_pdf_dir + '/' + pdf})
                except:
                    pass
                print(e)
                # break
        print()
        if os.path.exists('STOP'):
            break
            # break
