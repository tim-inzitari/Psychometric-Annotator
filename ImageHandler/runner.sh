#!/bin/bash

#initialize the image archive\
. ~/anaconda2/bin/activate image_handler
cd dzp
python setup.py install
cd ..
python ./document_processor.py
while true; do
  inotifywait -e create -e modify -e moved_to /input/ /images/ | while read FILE
    do
      if [[ $FILE = *"input"* ]]; then
        #A new character image is submitted.
        python ./image_handler.py
      elif [[ $FILE = *"images"* ]]; then
        #A new image is added to the image archive
        python ./document_processor.py
      fi
    done
done
