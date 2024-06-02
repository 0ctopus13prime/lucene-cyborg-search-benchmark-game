#!/bin/bash

script_dir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

# java
cd $script_dir
curl -O https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz
tar xvfz openjdk-21.0.2_linux-x64_bin.tar.gz
ln -s jdk-21.0.2 java
export JAVA_HOME=./java
export PATH=$JAVA_HOME/bin:$PATH

# git
sudo yum install git -y

# gcc
sudo yum install gcc -y
sudo yum install g++ -y

# cmake + ninja
sudo yum install cmake -y
sudo yum install ninja-build -y

