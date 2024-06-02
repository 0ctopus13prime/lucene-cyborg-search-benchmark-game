CORPUS := $(shell pwd)/corpus.json
LUCENE_CYBORG_CPP_HOME := $(shell pwd)/lucene-cyborg-cpp
LUCENE_CYBORG_JAVA_HOME := $(shell pwd)/lucene-cyborg-java
LUCENE_CYBORG_CPP_VALIDATION_HOME := $(shell pwd)/lucene-cyborg-cpp-validation
JAVA_HOME := $(shell pwd)/env/java
export

WIKI_SRC = "https://www.dropbox.com/s/wwnfnu441w1ec9p/wiki-articles.json.bz2"

COMMANDS ?= TOP_100 TOP_100_COUNT COUNT
# COMMANDS ?= COUNT

BASELINE_ENGINES ?= lucene-9.8.0-normal lucene-9.8.0-reordered
ENGINES ?= $(BASELINE_ENGINES) lucene-9.8.0-cyborg-cpp lucene-9.8.0-cyborg-java-normal lucene-9.8.0-cyborg-java-reordered
# ENGINES ?= lucene-9.8.0-cyborg-java-normal lucene-9.8.0-cyborg-java-reordered
PORT ?= 8080

help:
	@grep '^[^#[:space:]].*:' Makefile

all: index

corpus:
	@echo "--- Downloading $(WIKI_SRC) ---"
	@curl -# -L "$(WIKI_SRC)" | bunzip2 -c | python3 corpus_transform.py > $(CORPUS)

clean:
	@echo "--- Cleaning directories ---"
	@rm -fr results
	@for engine in $(ENGINES); do cd ${shell pwd}/engines/$$engine && make clean ; done

bench:
	@echo "--- Benchmarking ---"
	@rm -fr results
	@mkdir results
	@python3 src/client.py queries.txt $(ENGINES)
	@python3 ${shell pwd}/merge_with_other_engines_results.py

index:
	@echo "--- Indexing corpus ---"
	@for engine in $(ENGINES); do cd ${shell pwd}/engines/$$engine && make index ; done

compile: index
	@echo "--- Compiling lucene-cyborg-cpp ---"
	@${shell pwd}/pgo_enabled_build.sh
	@echo "--- Compiling lucene-cyborg-jara ---"
	@cd $(LUCENE_CYBORG_JAVA_HOME) && ./gradlew jar

serve:
	@echo "--- Serving results ---"
	@cp results.json web/build/results.json
	@cd web/build && python3 -m http.server $(PORT)
