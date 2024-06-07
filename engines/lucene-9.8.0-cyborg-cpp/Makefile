clean:
	@rm -f reordered_idx &>/dev/null

serve:
	@${LUCENE_CYBORG_CPP_HOME}/build/tantivy_bench_pisa_comparison reordered_idx

index: idx

compile:
	@echo '' > /dev/null

idx: clean
	@ln -s ${shell pwd}/../lucene-9.8.0-reordered/idx_docs_freqs ${shell pwd}/reordered_idx

