#!/bin/bash

################################################################################
# Script to convert yang modules into other formats
# Author: martin.skorupski@highstreet-technologies.com
# 
# Copyright 2018 higshtreet technologies GmbH
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# 

processor="./src/main/resources/lib/saxon9he.jar";
       in="./src/main/resources/TR-512_v1._3_Publish/OnfModel/CoreModel.uml";
     xslt="./src/main/prune-and-refactor/prune-and-refactor.xslt";
      out="./src/main/resources/EAGLE-Open-Model-Profile-and-Tools/UmlYangTools/xmi2yang/project/CoreModel.xml";

java -jar $processor -s:"$in" -xsl:"$xslt" -o:"$out";
