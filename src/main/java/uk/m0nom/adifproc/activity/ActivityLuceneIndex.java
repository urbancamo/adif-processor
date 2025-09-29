package uk.m0nom.adifproc.activity;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ngram.NGramTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.search.*;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.util.List;
import java.util.ArrayList;

public class ActivityLuceneIndex {
    private final ByteBuffersDirectory directory = new ByteBuffersDirectory();
    private final Analyzer analyzer;

    public ActivityLuceneIndex(List<Activity> activities) throws Exception {
        analyzer = CustomAnalyzer.builder()
                .withTokenizer(NGramTokenizerFactory.class, "minGramSize", "3", "maxGramSize", "10")
                .addTokenFilter(LowerCaseFilterFactory.class)
                .build();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        for (Activity activity : activities) {
            Document doc = new Document();
            doc.add(new TextField("ref", activity.getRef(), Field.Store.YES));
            doc.add(new TextField("name", activity.getName(), Field.Store.YES));
            doc.add(new TextField("type", activity.getType().getActivityName(), Field.Store.YES));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();
    }

    public List<String> searchByNameSubstring(String substring) throws Exception {
        return searchByField("name", substring, 10);
    }
    
    public List<String> searchByRefSubstring(String substring) throws Exception {
        return searchByField("ref", substring, 100);
    }
    
    private List<String> searchByField(String fieldName, String substring, int maxResults) throws Exception {
        List<String> results = new ArrayList<>();
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            // Escape special characters in the query string
            String escapedSubstring = QueryParser.escape(substring);
            Query query = new QueryParser(fieldName, analyzer).parse(escapedSubstring);
            TopDocs topDocs = searcher.search(query, maxResults);
            for (ScoreDoc sd : topDocs.scoreDocs) {
                Document d = searcher.storedFields().document(sd.doc);
                results.add(d.get("ref"));
            }
        }
        return results;
    }
}