import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.*;
import java.nio.file.Paths;

public class TantivyBoolSearchBenchmarkResultGenerator {
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

        TopScoreDocCollector collector = TopScoreDocCollector.create(topN, topN);
        searcher.search(query, collector);
        TopDocs topDocs = collector.topDocs();

        // 1. type : "term" or "must" or "should"
        // 2. num terms
        // 3. terms...
        // 4. total hits
        // 5. relation ord
        // 6. <doc id>|<score> pairs

        writer.write(queryType + "\n");
        writer.write(terms.length + "\n");
        for (String term : terms) {
            writer.write(term); writer.write('\n');
        }

        writer.write(String.format("%d\n", topDocs.totalHits.value));
        writer.write(String.format("%d\n", topDocs.totalHits.relation.ordinal()));

        writer.write(String.format("%d\n", topDocs.scoreDocs.length));
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            writer.write(String.format("%d|%.15f\n", scoreDoc.doc, scoreDoc.score));
        }
    }
}
