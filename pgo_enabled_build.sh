#!/bin/bash

benchmark_home=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

# let's go our hatch
cd $LUCENE_CYBORG_CPP_HOME
mkdir build &>/dev/null
cd build

# prepare profile directory
lc_profile_path="/tmp/lc_pgo_172a6f618f8283ff31"
rm -rf "$lc_profile_path" &>/dev/null

# compile with profile enabled
rm libLuceneCyborg.a &>/dev/null

cmake .. \
	-DCMAKE_BUILD_TYPE:STRING=Release \
	-DLUCENE_CYBORG_BUILD_JNI:BOOL=ON \
	-DLUCENE_CYBORG_JNI_INCLUDE_DIRECTORIES:STRING="/usr/lib/jvm/java-21-openjdk-amd64/include;/usr/lib/jvm/java-21-openjdk-amd64/include/linux" \
	-DPGO_PROFILE="-fprofile-generate=$lc_profile_path" \
	-G Ninja

cmake --build . \
	--target tantivy_search_result_validation \
	-j 7 \
	--verbose

# trigger profiling
echo "Running benchmark"
./tantivy_search_result_validation ${LUCENE_CYBORG_CPP_VALIDATION_HOME}/src/main/resources ${benchmark_home}/engines/lucene-9.8.0-normal/idx


# remove static lib
rm libLuceneCyborg.a &>/dev/null

# populates make file
echo "Compiling again with profiles"
cmake .. \
	-DCMAKE_BUILD_TYPE:STRING=Release \
	-DLUCENE_CYBORG_BUILD_JNI:BOOL=ON \
	-DLUCENE_CYBORG_JNI_INCLUDE_DIRECTORIES:STRING="/usr/lib/jvm/java-21-openjdk-amd64/include;/usr/lib/jvm/java-21-openjdk-amd64/include/linux" \
	-DPGO_PROFILE="-fprofile-use=$lc_profile_path" \
	-G Ninja

# build jni lib
echo "Building SO file + other executables..."
cmake --build . -j 7 --verbose
