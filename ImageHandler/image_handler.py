import os
import webapp2
import h5py


classList = {"a":1,"b":2,"c":3,"d":4,"e":5,"f":6,"g":7,"h":8,"i":9,"l":10,"m":11,
"n":12,"o":13,"p":14,"q":15,"r":16,"s":17,"t":18,"u":19,"x":20,"y":21,"z":22,
"~":23,"ⴈ":24,"ꝑ":25,"ꝓ":26, "ꝗ":27,"ꝝ":28,"ꝩ":29,"ꝯ":30,".":31,"_":32,"\'":33,
";":34,"?":35}

class CharImg(object):
    x = 0
    y = 0
    urn = ""
    line = 0
    word = 0
    character = 0
    user = "TEST"
    imgURL = ""

    # The class "constructor" - It's actually an initializer
    def __init__(self, x, y, urn, line, word, character, user, imgURL ,annotation):
        self.x = x
        self.y = y
        self.urn = urn.split("@")[0]
        self.line = line
        self.word = word
        self.character = character
        self.user = user
        self.imgURL = imgURL
        self.annotation = annotation


class MainPage(webapp2.RequestHandler):

    def get(self):
        self.response.write("Hello!")

    def post(self):
        try:
            inputChar = CharImg(int(self.request.params["x"]),int(self.request.params["y"]),
                                    self.request.params["urn"],int(self.request.params["line"]),
                                    int(self.request.params["word"]),int(self.request.params["character"]),
                                    self.request.params["user"], self.request.params["imgURL"], self.request.params["annotation"])
        except (OSError, IOError) as e:
            print e

def document_image(inputChar):
    if inputChar.y < 0:
        inputChar.y = 0
    if inputChar.x < 0:
        inputChar.x = 0
    e = "/"+inputChar.urn in results
    if not e:
        results.create_group("/"+inputChar.urn)
    e = "/"+inputChar.urn+"/"+inputChar.urn in results
    if e:
        docImg = results["/"+inputChar.urn+"/"+inputChar.urn]
    else:
        print inputChar.urn
        length,width = getSize(inputChar.urn)
        docImg = results.create_dataset("/"+inputChar.urn+"/"+inputChar.urn, (length,width,4), dtype='uint8')
    letter_height,letter_width,  thisWillBe3 = np.shape(img)
    target = docImg[inputChar.y:inputChar.y+letter_height,inputChar.x:inputChar.x+letter_width]
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
    target[bwImg, 3] = classList[inputChar.annotation]

    docImg[inputChar.y:inputChar.y+letter_height,inputChar.x:inputChar.x+letter_width] = target



app = webapp2.WSGIApplication([
    ('/', MainPage),
], debug=False)
