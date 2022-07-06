import os
from tkinter.tix import COLUMN
from tkinter.ttk import Separator
import requests
import pandas as pd
import numpy as np

#latest version of unicode
uniNames = pd.read_csv("http://unicode.org/Public/UNIDATA/UnicodeData.txt", sep=';', header=None, index_col=0)

n_cols = len(uniNames.columns)

#drop all but name and index
uniNames = uniNames.drop(uniNames.iloc[:, 1:],axis = 1)
uniNames.columns = ['name']
uniNames.index.names=['hex code']
#drop control chars
uniNames = uniNames[uniNames.name != '<control>']
uniNames['name']=uniNames['name'].str.strip()
print(uniNames.head(25))



# PARSE BLOCKS
blocks = pd.read_csv("https://unicode.org/Public/UNIDATA/Blocks.txt", sep=';', comment='#', names=['Range', 'Name'], index_col=None, header=None)
blocks['Name']=blocks['Name'].str.strip()

# split range to start and end
blocks[['Start','toDrop','End']]=blocks.Range.str.split('.', expand=True)
blocks = blocks.drop(columns=['Range','toDrop'])
blocks['Start'] = '0x'+blocks['Start']
blocks['End'] = '0x'+blocks['End']
blocks['Start'] = blocks['Start'].apply(int, base=16)
blocks['End'] = blocks['End'].apply(int, base=16)
print(blocks.head(5))


# write to text files
path = 'js/unicodeTxt'
os.makedirs(path, exist_ok=True)

import csv
quoteRule = csv.QUOTE_NONNUMERIC

uniNames.to_csv(f'{path}/unicodeNames.csv', header=False, quoting=quoteRule)
blocks.to_csv(f'{path}/blockRanges.csv', index=False,header=False,quoting=quoteRule)


def csvToJs(jsPath, csvPath, var):
    myJSFile = open(jsPath, 'w')
    myJSFile.write(f"var {var} = [\n")

    myCSVFile = open(csvPath, 'r')
    for line in myCSVFile .readlines() :
        myJSFile.write("[%s],\n" % line.strip())

    myJSFile.write("];")

    myJSFile.close()
    myCSVFile.close()

csvToJs('js/unicodeNames.js', f'{path}/unicodeNames.csv', 'unicodeNameCSV')
csvToJs('js/blockRanges.js', f'{path}/blockRanges.csv', 'blockRangeCSV')