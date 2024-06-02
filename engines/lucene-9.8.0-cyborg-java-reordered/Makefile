clean:
	@rm -fr idx &>/dev/null

serve:
	@java -XX:+UseParallelGC -cp "${LUCENE_CYBORG_JAVA_HOME}/build/libs/lucene-cyborg-java-0.0.1-SNAPSHOT.jar" DoQuery idx ${LUCENE_CYBORG_CPP_HOME}/build/libLuceneCyborgJNI.so

index: idx

compile:
	@echo '' > /dev/null

idx: compile clean
	@ln -s ${shell pwd}/../lucene-9.8.0-reordered/idx ${shell pwd}/idx
