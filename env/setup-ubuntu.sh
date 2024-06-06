#!/bin/bash

sudo apt update

script_dir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

# java
cd $script_dir
curl -O https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz
tar xvfz openjdk-21.0.2_linux-x64_bin.tar.gz
ln -s jdk-21.0.2 java
export JAVA_HOME=${script_dir}/java
export PATH=$JAVA_HOME/bin:$PATH

# gcc
sudo apt install gcc -y
sudo apt install g++ -y

# clang
sudo apt install clang

# cmake + ninja
sudo apt install cmake -y
sudo apt install ninja-build -y

# install bz2
sudo apt-get install bzip2 -y
