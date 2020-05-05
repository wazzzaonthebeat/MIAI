from imageai.Detection.Custom import DetectionModelTrainer
import os
file = "Buildings"
trainer = DetectionModelTrainer()
trainer.setModelTypeAsYOLOv3()

trainer.setDataDirectory(data_directory="Training Data\\"+file+"\\")
trainer.setTrainConfig(object_names_array=["building"], batch_size=1, num_experiments=197, train_from_pretrained_model="model\\yolo_trained.h5")
trainer.trainModel()
