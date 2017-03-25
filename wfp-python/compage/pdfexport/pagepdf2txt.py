import os
import re
from common.pdfutils import extract_text
"""
将下载的pdf转为txt
保持原目录结构不变
"""

if __name__ == '__main__':
    pdf_path = ''
    txt_path = ''
    stocks = os.listdir(pdf_path)
    for stock in stocks:
        stock_dir = pdf_path + '/' + stock
        for pdf in os.listdir(stock_dir):
            if re.match('.+\.(pdf|PDF|doc|DOC|docx|DOCX)$', pdf) is False:
                continue
            name = pdf[:pdf.rindex('.')]
            extract_text(stock_dir+'/'+pdf,)
