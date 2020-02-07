package service;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.stereotype.Component;

@Component
public interface LuceneService {

    void createIndex(String url, String title, String body) throws IOException;

    List<Document> search(String inField, String queryString) throws ParseException, IOException;
}
