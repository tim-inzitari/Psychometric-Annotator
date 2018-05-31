import numpy as np
from PIL import Image
import cStringIO
import h5py
from os import walk

charlookup = {"a":1,"b":2,"c":3,"d":4,"e":5,"f":6,"g":7,"h":8,"i":9,"l":10,"m":11,"n":12,"o":13,"p":14,"q":15,"r":16,
              "s":17,"t":18,"u":19,"x":20,"y":21,"z":22,"~":23,"ⴈ":24,"ꝑ":25,"ꝓ":26, "ꝗ":27,"ꝝ":28,"ꝩ":29,"ꝯ":30,
              ".":31,";":32,"_":33,"'":34,"?":35}

def loadRawImage(urn):
    return np.asarray(Image.open('/image/'+urn+".jpg"))


def document_image(raw, results, user, urn, line, word, character, letterX, letterY, img, anno):
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
        print urn
        length,width = getSize(urn)
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
    target[bwImg, 3] = anno

    docImg[letterY:letterY+letter_height,letterX:letterX+letter_width] = target
    pylab.show()





def main():
    inputDir = "out/"
    outputDir = ""
    files = []
    for (dirpath, dirnames, filenames) in walk(inputDir):
        files.extend(filenames)
        break
    results = h5py.File(outputDir + "results.hdf5", "a")
    for data in files:
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







    pylab.set_cmap("prism")
    # pylab.imshow(np.asarray(Image.open("REG_VAT12_186r_RAW.jpg"),np.uint8))

    pylab.figure("lines")
    pylab.imshow(results["/" + urn + "/" + urn][:,:,0])
    pylab.figure("words")
    pylab.imshow(results["/" + urn + "/" + urn][:, :, 1])
    pylab.figure("Chars")
    pylab.imshow(results["/" + urn + "/" + urn][:, :, 2])
    pylab.show()
    results.close()
