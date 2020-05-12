![logo](https://tablemate.online/miai/images/logo_black3.png)

# Musical Image Artificial Intelligence
> **[Visit Website](https://tablemate.online/miai)**

> **[Watch Video Demonstrations on Youtube](https://www.youtube.com/watch?v=wucA0tWzhrE&list=PLtesPMCqCVApMJyWaUxOw4lY49zk2PwLG)**

Musical Image Artificial Intelligence (MiAI) is an application that takes an image and creates a musical composition from it. The composition is based on what objects are found in the image and photographic elements such as dimensions and colors occurring.

The program has been fine-tuned to detect certain patterns in images and will compose the pieces based on them, controlling the length of pieces, the tonality, the dynamics, rhythms, texture, and melodies.

The application involved interrelated use of computer science, probability and music theory to create the final product.

The main program is a Java application which integrates with a Python application for image analysis and a mobile application for Android and iOS to allow you to take a photo from your device and upload it to the program for analysis and composition.

# User Interface 

MiAI launches and displays the first image in our default image library with all the objects detected on it highlighted with colored rectangles. See Figure 4.

Play/Stop - To play/stop the piece we press the respective buttons. Pressing the play button, it will play the same piece, except for sound effects. If you reload the image, it composes a new composition

Analyze Images - To use a new set of images, by clicking on the "Analyze Images" button, which will open the window shown on Figure 1. Put new images in input folder.

View Objects/Data - A user can see images of individual objects detected in the photo by clicking the "View Objects" button. To view raw detection data, click on "View Data" button. 

To listen to what this piece would sounds like, goto [Visit Youtube Channel](https://www.youtube.com/watch?v=wucA0tWzhrE&list=PLtesPMCqCVApMJyWaUxOw4lY49zk2PwLG).

![Figure 4](https://tablemate.online/miai/images/figure4.jpg)

# Object Detection

MiAI objects are detected using an Artificial Intelligence model trained to detect several common objects i.e. cars, animals, people, food.

Image object detection library - Image AI, recommended by Dr Lisa Torey.

Image AI library (by Deep Quest AI) uses the Keras, Tensor Flow and OpenCV modules in Python 3.6.3.


Procedure:
1) Select input images and a model - used YoloV3 in MiAI.
2) Run object detection using trained model.

Results:
Objects are identified to user if detected along with their coordinates and percentage of confidence. See Figure 1.

Output:
Produces a file, *image name*.data, for all the images with detection data shown by Figure 1.
Each object detected is cropped and its background removed for object color analysis using OpenCV module
Output will be interpreted by our main Java application.

![Figure 1](https://tablemate.online/miai/images/figure1.jpg)

# Composition

MiAI composes algorithmic pieces of music and selects musical elements, such as melodies, texture, rhythms, tonality, length of piece, note spaces, note velocities, note lengths, syncopation, chromaticism, using the results of the object detection and image analysis.

Image Analysis Class - fetch associated image data object detection Python script. If no objects, color data used to compose. If objects detected, extract dominant color in objects detected, figure out dominant objects in the picture by both frequency and size and categorize them. Musical element, tempo, dynamics, tonality and mood, scoring system based on image analysis, refer Figure 5. for scoring sample code. Each of those will alter the compositional elements differently.

2) Composition Class - program tallies the scores and decides what key to compose in, tempo, spaces between notes, and what sound effects to play. Every note, including rests, is added to a list.
- Jsyn Audio Synthesis library reads the data of each note from the list and assigns the note to its corresponding frequencies, lengths and amplitudes and generates sine waves.

![Figure 5](https://tablemate.online/miai/images/figure5.jpg)

# Training

Tensor Flow & Google Colaboratory

The YoloV3 model is diverse but lacked some objects we wanted to detect, such as fires, tree, crowds and musical instruments, so we had to train it.

1) Preparing Dataset - Select atleast 1000 images of the object we wish to train the model to detect. We must then define the coordinates of that object (annotations) in every picture into an .xml file manually or using a program such as lableIMG. - See Figure 2.

2) Training Environment - We used Google Colaborotory, a cloud-based Python environment with T4 and P100 GPU support Capabilities and high memory amounts, because training was a very computationally expensive process and can take upwards of a week on a regular computer which is could cause overheating problems.
3)Training Program - we used Image AI script provided by and inputted the dataset we prepared from Step 1. Training used the Image AI and Tensor Flow GPU. We had to provide a list of training object and other important variables like batch-size (number of simultaneous training tasks). See Figure 3.

4) Model Evaluation - The program outputs a newly trained model and a percentage of uncertainty, reducing the longer it runs.

![Figure 2](https://tablemate.online/miai/images/figure2.jpg)


# Mobile Application

The mobile application developed for Android and iOS allows a user to upload a picture from their mobile device and send it to MiAI for instant analysis and composition. See Figure 6.

![Figure 6](https://tablemate.online/miai/images/figure6.jpg)
