from PIL import Image
import deepzoom
import os
import shutil
import MySQLdb

inputDir = "/images/"
outputDir = "/images/"

db = MySQLdb.connect(user='software',
            passwd=os.getenv('PSYANN_DB_PASSWORD', 'badpass'),
            host=os.getenv('PSYANN_DB_HOST', 'db'),
            db=os.getenv('PSYANN_DATABASE', 'documents'))
cur1 = db.cursor()
stmt1 = "SELECT count(*) FROM doc WHERE URN = %s"
cur2 = db.cursor()
stmt2 = "SELECT count(*) FROM doc"
cur3 = db.cursor()
stmt3 = "INSERT INTO doc(ID,URN) VALUES (%s,%s)"



files = []
for (dirpath, dirnames, filenames) in os.walk(inputDir):
    files.extend(filenames)
    break

IC = deepzoom.ImageCreator()
for source in files:
    print(source)
    if (".jpg" in source):
        urn = source.replace(".jpg","")
        urn_parts= urn.split(":")
        path = ""
        filename = ""
        for part in range(0,len(urn_parts)):
            if part < len(urn_parts)-1:
                path = path + urn_parts[part] + "_"
            else:
                filename = filename + urn_parts[part] + "_"
            if len(urn_parts) == 0:
                urn_parts = "other"
                urn = "other:"+urn
        filename = filename[:-1]
        target = outputDir+"/"+path+"/"+filename
        if(not os.path.exists(target+"_files")):
            if not os.path.exists(outputDir+"/"+path+"/"):
                os.makedirs(outputDir+"/"+path+"/")
            IC.create(inputDir+source, target)
            shutil.copy(inputDir+source, outputDir+"/"+path+"/"+filename+"_RAW.jpg")
            os.rename(target, target+".dzi")
        cur1.execute(stmt1, [urn])
        count = cur1.fetchone()[0]
        if(count == 0):
            cur2.execute(stmt2)
            new_id = cur2.fetchone()[0]
            cur3.execute(stmt3,[new_id, urn])
            db.commit()
