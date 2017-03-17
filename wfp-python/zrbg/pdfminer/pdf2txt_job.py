"""
Converts PDF text content (though not images containing text) to plain text, html, xml or "tags".
"""
import sys
import pdfminer.settings
import re

pdfminer.settings.STRICT = False
import pdfminer.high_level
import pdfminer.layout
from pdfminer.image import ImageWriter
import os


def extract_text(files=[], outfile='-',
                 _py2_no_more_posargs=None,  # Bloody Python2 needs a shim
                 no_laparams=False, all_texts=None, detect_vertical=None,  # LAParams
                 word_margin=None, char_margin=None, line_margin=None, boxes_flow=None,  # LAParams
                 output_type='text', codec='utf-8', strip_control=False,
                 maxpages=0, page_numbers=None, password="", scale=1.0, rotation=0,
                 layoutmode='normal', output_dir=None, debug=False,
                 disable_caching=False, **other):
    if _py2_no_more_posargs is not None:
        raise ValueError("Too many positional arguments passed.")
    if not files:
        raise ValueError("Must provide files to work upon!")

    # If any LAParams group arguments were passed, create an LAParams object and
    # populate with given args. Otherwise, set it to None.
    if not no_laparams:
        laparams = pdfminer.layout.LAParams()
        for param in ("all_texts", "detect_vertical", "word_margin", "char_margin", "line_margin", "boxes_flow"):
            paramv = locals().get(param, None)
            if paramv is not None:
                setattr(laparams, param, paramv)
    else:
        laparams = None

    imagewriter = None
    if output_dir:
        imagewriter = ImageWriter(output_dir)

    if output_type == "text" and outfile != "-":
        for override, alttype in ((".htm", "html"),
                                  (".html", "html"),
                                  (".xml", "xml"),
                                  (".tag", "tag")):
            if outfile.endswith(override):
                output_type = alttype

    if outfile == "-":
        outfp = sys.stdout
        if outfp.encoding is not None:
            codec = 'utf-8'
    else:
        outfp = open(outfile, "wb")

    for fname in files:
        with open(fname, "rb") as fp:
            pdfminer.high_level.extract_text_to_fp(fp, **locals())
    return outfp


if __name__ == '__main__':
    pdf_dir = '/Users/baidu/tmp/zrbg_pdf_2010'
    txt_dir = '/Users/baidu/tmp/zrbg_txt_2010'
    pdf_filenames = [f for f in os.listdir(pdf_dir) if re.match('.+\.(pdf|PDF)$', f)]
    total = len(pdf_filenames)
    cnt = 0
    for filename in pdf_filenames:
        out_put_file = txt_dir + '/' + filename[:-3] + 'txt'
        cnt += 1
        print('\r%d/%d\t%s' % (cnt, total, out_put_file), end='')
        if os.path.exists(out_put_file):
            continue
        try:
            extract_text(files=[pdf_dir + '/' + filename], outfile=out_put_file)
        except:
            print('%s extract error' % filename)
        # break
