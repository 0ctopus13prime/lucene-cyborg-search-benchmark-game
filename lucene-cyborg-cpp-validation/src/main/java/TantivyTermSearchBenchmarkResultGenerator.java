import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class TantivyTermSearchBenchmarkResultGenerator {
    public static void main(String... args) throws IOException {
        String taskPath = args[0];
        String destPath = args[1];
        String indexPath = args[2];

        int topN = 100;

        Set<String> uniqueTerms = new HashSet<>();
        final Directory dir = new MMapDirectory(Paths.get(indexPath));
        try (IndexReader indexReader = DirectoryReader.open(dir)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(destPath))) {
                try (BufferedReader reader = new BufferedReader(new FileReader(taskPath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.strip();
                        String[] booleanTerms = line.split(" ");

                        for (String term : booleanTerms) {
                            term = term.replaceAll("\\+", "");
                            writeResults(term.trim(), indexReader, writer, uniqueTerms, topN);
                        }
                    }  // End while
                }  // End try
            }  // End try
        }  // End try
    }

    static void writeResults(String term,
                             IndexReader indexReader,
                             BufferedWriter writer,
                             Set<String> uniqueTerms,
                             int topN) throws IOException {
        if (uniqueTerms.contains(term)) {
            return;
        }

        uniqueTerms.add(term);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(topN, topN);
        TermQuery termQuery = new TermQuery(new Term("text", term));
        indexSearcher.search(termQuery, collector);

        // 0. term
        // 1. total hits
        // 2. relation ord
        // 3. <doc id> - <score> pairs

        writer.write(term + "\n");
        TopDocs topDocs = collector.topDocs();
        writer.write(String.format("%d\n", topDocs.totalHits.value));
        writer.write(String.format("%d\n", topDocs.totalHits.relation.ordinal()));
        writer.write(String.format("%d\n", topDocs.scoreDocs.length));

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            writer.write(String.format("%d|%.15f\n", scoreDoc.doc, scoreDoc.score));
        }
    }
}

