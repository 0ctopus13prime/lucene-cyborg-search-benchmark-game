clean:
	@rm -fr idx

serve:
	@java -XX:+UseParallelGC -cp "${shell pwd}/../lucene-9.8.0-normal/build/libs/search-index-benchmark-game-lucene-1.0-SNAPSHOT.jar:${shell pwd}/../lucene-9.8.0-normal/build/dependencies/*" DoQuery idx

index: idx

compile:
	cd ${shell pwd}/../lucene-9.8.0-normal && make compile

idx: compile
	@echo "--- Reordering index with Lucene 9.8.0 ---"
	@java -server -cp "${shell pwd}/../lucene-9.8.0-normal/build/libs/search-index-benchmark-game-lucene-1.0-SNAPSHOT.jar:${shell pwd}/../lucene-9.8.0-normal/build/dependencies/*" ReorderIndex ${shell pwd}/../lucene-9.8.0-normal/idx idx
	@echo "--- Reordering index with Lucene 9.8.0 DOCS_FREQS ---"
	@java -server -cp "${shell pwd}/../lucene-9.8.0-normal/build/libs/search-index-benchmark-game-lucene-1.0-SNAPSHOT.jar:${shell pwd}/../lucene-9.8.0-normal/build/dependencies/*" ReorderIndex ${shell pwd}/../lucene-9.8.0-normal/idx_docs_freqs idx_docs_freqs
