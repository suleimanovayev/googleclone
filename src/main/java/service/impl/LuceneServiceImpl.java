package service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.LuceneService;

@Service
public class LuceneServiceImpl implements LuceneService {
    private Logger log = Logger.getLogger("LuceneServiceImpl.class");

    private StandardAnalyzer analyzer;
    private Directory memoryIndex;

    @Autowired
    public LuceneServiceImpl(StandardAnalyzer analyzer, Directory memoryIndex) {
        this.analyzer = analyzer;
        this.memoryIndex = memoryIndex;
    }

    @Override
    public void createIndex(String url, String title, String body) {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        Document document = new Document();
        document.add(new TextField("url", url, Field.Store.YES));
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new TextField("body", body, Field.Store.YES));
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(memoryIndex, indexWriterConfig);
        } catch (IOException e) {
            log.error("Cant create index", e);
        }
        try {
            writer.addDocument(document);
        } catch (IOException e) {
            log.error("Cant add document", e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            log.error("Writer not closed", e);
        }
    }

    @Override
    public List<Document> search(String inField, String queryString) {
        Query query = null;
        try {
            query = new QueryParser(inField, analyzer)
                    .parse(queryString);
        } catch (ParseException e) {
            log.warn("Cant get query");
        }

        IndexReader indexReader = null;
        try {
            indexReader = DirectoryReader.open(memoryIndex);
        } catch (IOException e) {
            log.error("Cant read index");
        }

        IndexSearcher searcher = new IndexSearcher(indexReader);

        TopDocs topDocs = null;
        try {
            topDocs = searcher.search(query, 10);
        } catch (IOException e) {
            log.warn("Cant find by query");
        }

        List<Document> documents = new ArrayList<>();

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            try {
                documents.add(searcher.doc(scoreDoc.doc));
            } catch (IOException e) {
                log.warn("Cant add document");
            }
        }
        return documents;
    }
}
