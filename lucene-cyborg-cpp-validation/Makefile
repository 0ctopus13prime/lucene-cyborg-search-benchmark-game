

compile:
	@./gradlew clean build copyDependencies


gen: compile
	@java -XX:+UseParallelGC -cp "build/libs/lucene-cyborg-cpp-validation-1.0-SNAPSHOT.jar:build/dependencies/*" TantivyBoolAggreggateBenchmarkResultGenerator ${shell pwd}/src/main/java/resources/tantivy_srch_bench_lc_queries.txt ${shell pwd}/src/main/java/resources/tantivy_srch_benchmark.bool_agg_results ${shell pwd}/../engines/lucene-9.8.0-normal/idx 
	@java -XX:+UseParallelGC -cp "build/libs/lucene-cyborg-cpp-validation-1.0-SNAPSHOT.jar:build/dependencies/*" TantivyBoolSearchBenchmarkResultGenerator ${shell pwd}/src/main/java/resources/tantivy_srch_bench_lc_queries.txt ${shell pwd}/src/main/java/resources/tantivy_srch_benchmark.bool_search_results ${shell pwd}/../engines/lucene-9.8.0-normal/idx 
	@java -XX:+UseParallelGC -cp "build/libs/lucene-cyborg-cpp-validation-1.0-SNAPSHOT.jar:build/dependencies/*" TantivyTermAggregateBenchmarkResultGenerator ${shell pwd}/src/main/java/resources/tantivy_srch_bench_lc_queries.txt ${shell pwd}/src/main/java/resources/tantivy_srch_benchmark.term_agg_results ${shell pwd}/../engines/lucene-9.8.0-normal/idx 
	@java -XX:+UseParallelGC -cp "build/libs/lucene-cyborg-cpp-validation-1.0-SNAPSHOT.jar:build/dependencies/*" TantivyTermSearchBenchmarkResultGenerator ${shell pwd}/src/main/java/resources/tantivy_srch_bench_lc_queries.txt ${shell pwd}/src/main/java/resources/tantivy_srch_benchmark.term_search_results ${shell pwd}/../engines/lucene-9.8.0-normal/idx 
