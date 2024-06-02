import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.*;
import java.nio.file.Paths;

public class TantivyBoolAggreggateBenchmarkResultGenerator {
    public static void main(String... args) throws IOException {
	String taskPath = args[0];
	String destPath = args[1];
	String indexPath = args[2];
	
        int topN = 100;

        final Directory dir = new MMapDirectory(Paths.get(indexPath));
        try (IndexReader indexReader = DirectoryReader.open(dir)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(destPath))) {
                try (BufferedReader reader = new BufferedReader(new FileReader(taskPath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.strip();
                        String queryType;
                        if (!line.contains(" ")) {
                            queryType = "Term";
                        } else if (line.contains("+")) {
                            queryType = "And";
                        } else {
                            queryType = "Or";
                        }
                        writeResults(line.split(" "), queryType, writer, indexReader, topN);
                    }  // End while
                }  // End try
            }  // End try
        }  // End try
    }

    static void writeResults(String[] terms,
                             String queryType,
                             BufferedWriter writer,
                             IndexReader indexReader,
                             int topN) throws IOException {
        if (terms.length <= 1) {
            return;
        }

        IndexSearcher searcher = new IndexSearcher(indexReader);
        Query query;
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        if (queryType.equals("Term")) {
            query = new TermQuery(new Term("text", terms[0]));
        } else if (queryType.startsWith("And")) {
            for (int i = 0 ; i < terms.length ; ++i) {
                terms[i] = terms[i].substring(1);
            }

            for (String term : terms) {
                // remove '+' using substring
                builder.add(new TermQuery(new Term("text", term)), BooleanClause.Occur.MUST);
            }
            query = builder.build();
        } else {
            for (String term : terms) {
                builder.add(new TermQuery(new Term("text", term)), BooleanClause.Occur.SHOULD);
            }
            query = builder.build();
        }

        CountCollector collector = new CountCollector();
        searcher.search(query, collector);

        // 1. type : "term" or "must" or "should"
        // 2. num terms
        // 3. terms...
        // 4. count

        writer.write(queryType + "\n");
        writer.write(terms.length + "\n");
        for (String term : terms) {
            writer.write(term); writer.write('\n');
        }
        writer.write("" + collector.count);
        writer.write('\n');
    }

    private static class CountCollector implements Collector {
        private int count = 0;

        @Override
        public LeafCollector getLeafCollector(LeafReaderContext leafReaderContext) throws IOException {
            return new LeafCollector() {
                @Override
                public void setScorer(Scorable scorable) throws IOException {
                }

                @Override
                public void collect(int i) throws IOException {
                    ++count;
                }
            };
        }

        @Override
        public ScoreMode scoreMode() {
            return ScoreMode.COMPLETE_NO_SCORES;
        }
    }
}
