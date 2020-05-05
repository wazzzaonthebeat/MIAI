import shutil

from imageai.Detection import ObjectDetection
from imageai.Detection.Custom import CustomObjectDetection
from PIL import Image
import cv2
import numpy as np
from matplotlib import pyplot as plt
from os import listdir
from os.path import isfile, join
import os
import sys
import numpy as np
from matplotlib import pyplot as plt

#print ('Number of arguments:', len(sys.argv), 'arguments.')
#print ('Argument List:', str(sys.argv)) # foldername and modelpath #min prob

minimum_percentage_probability=90
multi=False
# variables to change

customObjName = "MassModel"
folderName = "MassModel"
modelName = "detection_model-ex-036--loss-0005.565"

if len(sys.argv) == 4: #only one model
    multi = True
    folderName = sys.argv[1]
    customObjName = sys.argv[1]
    modelName = sys.argv[2]
    #minimum_percentage_probability = 90
    minimum_percentage_probability = int(sys.argv[3])
    print("Multi-Model Mode")


detector = ObjectDetection()
dir_path = os.path.dirname(os.path.realpath(__file__))
#scan director and parse all files to list
onlyfiles = [f for f in listdir(dir_path+"\input") if isfile(join(dir_path+"\input", f))]
inputs = []
imageFilesWithDetections = list()
for n in onlyfiles:
    inputs.append(n.replace(".jpg",""))

#delete everything in output
folder= (dir_path+"\output\\")
for filename in os.listdir(folder):
    file_path = os.path.join(folder, filename)
    try:
        if os.path.isfile(file_path) or os.path.islink(file_path):
            os.unlink(file_path)
        elif os.path.isdir(file_path):
            shutil.rmtree(file_path)
    except Exception as e:
        print('Failed to delete %s. Reason: %s' % (file_path, e))

def crop(imagePath,objectName, dimensions,count,filename):
    im = Image.open(imagePath)
    im_crop = im.crop((dimensions[0], dimensions[1], dimensions[2], dimensions[3]))
    try:
        #print(filename)
        os.mkdir(dir_path+"\output\\"+filename)
    except:
        None
    im_crop.save(dir_path+"\output\\"+filename+'\\'+objectName+"_"+str(count)+".jpg", quality=95)


for eachItem in inputs:
    image = eachItem+".jpg"
    model_path = dir_path+"\model\\yolo_trained.h5"
    input_path = dir_path+"\input\\"+image
    output_path = dir_path+"\output\\"+eachItem+".jpg"

    detector.setModelTypeAsYOLOv3()
    #detector.setModelTypeAsRetinaNet()
    detector.setModelPath(model_path)
    detector.loadModel()
    detection = detector.detectObjectsFromImage(input_image=input_path, output_image_path=output_path)

    filename = dir_path+"\\data\\"+eachItem+".data"
    f = open(filename, "w")
    f.write("")
    f = open(filename, "a")
    print(" \n"+"Results for "+eachItem)
    count = 0
    for eachItemSub in detection:


        #for each object found also detect the dominant color
        print(eachItemSub["name"] , " : ", eachItemSub["percentage_probability"], " where ",eachItemSub["box_points"])
        result = eachItemSub["name"],":", str(eachItemSub["percentage_probability"]),":",str(eachItemSub["box_points"])
        f.write(str(result).replace("(","").replace(")",""))
        f.write("\n")

        #generate cropped segments of all objects
        crop(input_path,eachItemSub["name"] ,eachItemSub["box_points"],count,eachItem)
        count = count +1
        if imageFilesWithDetections.__contains__(eachItem):
            None
        else:
            imageFilesWithDetections.append(eachItem)
    f.close()
    if len(detection) == 0:
        print("no objects detected")
    #else:
    #print ("To store ",str(result))
    #write to file for java

if multi:
    #do second model

    print("---------------------------")
    print("Starting Secondary Model")
    print("Custom Folder Name", folderName)
    print("Custom Model Name", modelName)
    model_path = dir_path + "\model\\trained\\" + folderName + "\\" + modelName + ".h5"
    detector = CustomObjectDetection()
    detector.setModelTypeAsYOLOv3()
    detector.setModelPath(model_path)
    detector.setJsonPath(dir_path + "\model\\trained\\" + folderName + "\\detection_config.json")
    detector.loadModel()
    dir_path = os.path.dirname(os.path.realpath(__file__))
    # scan director and parse all files to list
    onlyfiles = [f for f in listdir(dir_path + "\output") if
                 isfile(join(dir_path + "\output", f))]
    inputs = []

    for n in onlyfiles:
        inputs.append(n)
    print(len(onlyfiles),"custom images")

    for eachItem in inputs:
        eachItem = eachItem.replace(".jpg","")
        input_path = dir_path + "\input\\" + eachItem + ".jpg"
        output_path = dir_path + "\output\\" + eachItem + ".jpg"
        print(output_path)
        try:
            detection = detector.detectObjectsFromImage(input_image=output_path, output_image_path=output_path,
                                                        minimum_percentage_probability=minimum_percentage_probability)
            #append to imageFile
            filename = dir_path + "\\data\\" + eachItem + ".data"
            f = open(filename, "a")
            #print("Writing data: "+filename)
            print(" \n" + "Results for " + eachItem)

            #count how many items ins the folder
            try:
                countfiles = [f for f in listdir(dir_path + "\output\\"+eachItem) if
                             isfile(join(dir_path + "\output\\"+eachItem, f))]
                count = len(countfiles)
            except:
                count = 0
            for eachItems in detection:
                # for each object found also detect the dominant color
                print(eachItems["name"], " : ", eachItems["percentage_probability"], " where ", eachItems["box_points"])
                result = eachItems["name"], ":", str(eachItems["percentage_probability"]), ":", str(eachItems["box_points"])
                f.write(str(result).replace("(", "").replace(")", ""))
                f.write("\n")

                # generate cropped segments of all objects
                crop(input_path, eachItems["name"], eachItems["box_points"], count, eachItem)
                count = count + 1
                if imageFilesWithDetections.__contains__(eachItems["name"]):
                    None
                else:
                    #print ("adding "+eachItems["name"]+" to detections")
                    imageFilesWithDetections.append(eachItem)
            f.close()
            if len(detection) == 0:
                print("no objects detected")
            # else:
            # print ("To store ",str(result))
            # write to file for java
        except Exception as inst :
            print(inst)

#gothrough all the individual scanned images and remove backgrounds
print("Removing backgrounds")
for each in imageFilesWithDetections:
    try:
        os.mkdir(dir_path + "\output\\" + each+"\\noback\\")
    except:
        None

    #onlyfiles = [f for f in listdir(dir_path+"\output\\"+filename) if isfile(join(dir_path+"\output\\"+filename, f))]
    for f in listdir(dir_path + "\output\\" + each):
        try:
            #print('removing backgrounds for: ' + each + ".jpg")
            image_bgr = cv2.imread(dir_path + "\output\\" + each+"\\"+f)

            # Convert to RGB
            image_rgb = cv2.cvtColor(image_bgr, cv2.COLOR_BGR2RGB)
            # Rectange values: start x, start y, width, height
            # get dimensions of image

            # height, width, number of channels in image
            height = image_bgr.shape[0]
            width = image_bgr.shape[1]

            rectangle = (0, 0, width - 1, height - 1)
            # Create initial mask
            mask = np.zeros(image_rgb.shape[:2], np.uint8)

            # Create temporary arrays used by grabCut
            bgdModel = np.zeros((1, 65), np.float64)
            fgdModel = np.zeros((1, 65), np.float64)

            # Run grabCut
            cv2.grabCut(image_rgb,  # Our image
                        mask,  # The Mask
                        rectangle,  # Our rectangle
                        bgdModel,  # Temporary array for background
                        fgdModel,  # Temporary array for background
                        5,  # Number of iterations
                        cv2.GC_INIT_WITH_RECT)  # Initiative using our rectangle

            # Create mask where sure and likely backgrounds set to 0, otherwise 1
            mask_2 = np.where((mask == 2) | (mask == 0), 0, 1).astype('uint8')

            # Multiply image with new mask to subtract background
            image_rgb_nobg = image_rgb * mask_2[:, :, np.newaxis]

            # Show image
            plt.imshow(image_rgb_nobg), plt.axis("off")
            plt.imsave(dir_path + "\output\\" + each+"\\noback\\"+f, image_rgb_nobg)

            # remove background
            file_name = dir_path + "\output\\" + each+"\\noback\\"+f

            src = cv2.imread(file_name, 1)
            tmp = cv2.cvtColor(src, cv2.COLOR_BGR2GRAY)
            _, alpha = cv2.threshold(tmp, 0, 255, cv2.THRESH_BINARY)
            b, g, r = cv2.split(src)
            rgba = [b, g, r, alpha]
            dst = cv2.merge(rgba, 4)
            cv2.imwrite(dir_path + "\output\\" + each+"\\noback\\"+f.replace(".jpg",".png"), dst)
            print("Successfully removed backgrounds: " + each+"\\"+f)
        except:
            None

print ("Analysis Complete")