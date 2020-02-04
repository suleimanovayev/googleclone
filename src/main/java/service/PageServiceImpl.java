package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Page;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.PageRepository;

@Service
public class PageServiceImpl implements PageService {
    private static Set<String> set = new HashSet<>();
    private static int level = 2;

    @Autowired
    private PageRepository pageRepository;

    private StandardAnalyzer analyzer = new StandardAnalyzer();

    private Directory memoryIndex = new RAMDirectory();

    @Override
    public void save(String url) throws IOException {
        set.add(url);
        org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
        String body = doc.body().text().toLowerCase();
        String title = doc.title();
        createIndex(url, title, body);
        pageRepository.save(new Page(url, title, body));
        getPageLinks(doc);
    }

    public void createIndex(String url, String title, String body) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        Document document = new Document();

        document.add(new TextField("url", url, Field.Store.YES));
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new TextField("body", body, Field.Store.YES));

        IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);
        writer.updateDocument(new Term("url", "body"), document);
        writer.close();
    }

    @Override
    public List<Document> search(String inField, String queryString)
            throws ParseException, IOException {
        Query query = new QueryParser(inField, analyzer)
                .parse(queryString);

        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 10);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }
        return documents;
    }

    public void getPageLinks(org.jsoup.nodes.Document document) throws IOException {
        Elements elements = document.select("a[href]");
        level--;
        if (level < 1) {
            return;
        }

        for (Element elem : elements) {
            String absHref = elem.attr("abs:href");
            if (set.contains(absHref)) {
                continue;
            }
            save(absHref);
        }
    }
}
