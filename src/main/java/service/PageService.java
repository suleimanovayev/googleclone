package service;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

public interface PageService {
    void save(String url) throws IOException;

    List<Document> search(String inField, String queryString) throws ParseException, IOException;

    void createIndex(String url, String title, String body) throws IOException;
}
