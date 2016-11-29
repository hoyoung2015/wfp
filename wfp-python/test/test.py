__author__ = 'huyang'

import os
import platform
def getSeparator():
    if 'Windows' in platform.system():
        separator = '\\'
    else:
        separator = '/'
    return separator

print(getSeparator())
print(os.getcwd())