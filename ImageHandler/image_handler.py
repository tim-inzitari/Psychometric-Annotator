#!/usr/bin/env python
# -*- coding: utf-8 -*-

import numpy as np
from PIL import Image
import h5py
import os

charlookup = {"a":1,"b":2,"c":3,"d":4,"e":5,"f":6,"g":7,"h":8,"i":9,"l":10,"m":11,"n":12,"o":13,"p":14,"q":15,"r":16,
              "s":17,"t":18,"u":19,"x":20,"y":21,"z":22,"~":23,"ⴈ":24,"ꝑ":25,"ꝓ":26, "ꝗ":27,"ꝝ":28,"ꝩ":29,"ꝯ":30,
              ".":31,";":32,"_":33,"'":34,"?":35}

def loadRawImage(urn):
    # change this
    return np.asarray(Image.open('/images/'+urn+".jpg"))


def document_image(results, user, urn, line, word, character, letterX, letterY, img, anno):
    raw = loadRawImage(urn)
    if letterY < 0:
        letterY = 0
    if letterX < 0:
        letterX = 0
    e = "/"+urn in results
    if not e:
        results.create_group("/"+urn)
    e = "/"+urn+"/"+urn in results
    if e:
        docImg = results["/"+urn+"/"+urn]
    else:
        length,width,depth = raw.shape
        docImg = results.create_dataset("/"+urn+"/"+urn, (length,width,4), dtype='uint8')
    letter_height,letter_width,  thisWillBe3 = np.shape(img)
    target = docImg[letterY:letterY+letter_height,letterX:letterX+letter_width]
    bwImg = np.logical_not(np.equal(np.max(img,2),0))
    bwShape = np.shape(bwImg)
    targetShape = np.shape(target)
    if(bwShape[0] != targetShape[0]):
        bwImg = bwImg[0:targetShape[0],:]
    elif(bwShape[1] != targetShape[1]):
        bwImg = bwImg[:,0:targetShape[1]]
    target[bwImg, 0] = line + 1
    target[bwImg, 1] = word + 1
    target[bwImg, 2] = character + 1
    target[bwImg, 3] = charlookup[anno]

    docImg[letterY:letterY+letter_height,letterX:letterX+letter_width] = target





def main():
    inputDir = "/input/"
    outputDir = "/output/"
    files = []
    for (dirpath, dirnames, filenames) in os.walk(inputDir):
        print( filenames)
        files.extend(filenames)
        break
    results = h5py.File(outputDir + "results.hdf5", "a")
    if not os.path.exists(inputDir + "/done"):
        os.makedirs(inputDir + "/done")
    print (len(files))
    for data in files:
        print ("Processing: " + data)
        if(".png" in data):
            data = inputDir + data
            img = np.asarray(Image.open(data))
            data2 = data.replace(".png",".txt")
            infile = file(data2, 'r')
            urn = infile.readline().strip()
            line = int(infile.readline().strip())
            word = int(infile.readline().strip())
            character = int(infile.readline().strip())
            x = int(infile.readline().strip())
            y = int(infile.readline().strip())
            user = infile.readline().strip()
            anno = infile.readline().strip()
            infile.close()
            urn = urn.split("@")[0]
            document_image(results, user, urn, line, word, character, x, y, img, anno)
            os.rename(data, inputDir + "/done/" + data.split("/")[-1])
            os.rename(data2, inputDir + "/done/" + data2.split("/")[-1])

if __name__ == '__main__':
    main()
