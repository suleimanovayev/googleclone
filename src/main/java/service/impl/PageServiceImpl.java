package service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.Page;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import repository.PageRepository;
import service.LuceneService;
import service.PageService;
import thread.NextPage;

@Service
public class PageServiceImpl implements PageService {
    private static final ExecutorService EXECUTOR_SERVICE =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private int level = 2;

    private final Logger log = Logger.getLogger("PageServiceImpl.class");
    private Set<String> set = new HashSet<>();

    private PageRepository pageRepository;
    private LuceneService luceneService;

    @Autowired
    public PageServiceImpl(PageRepository pageRepository, LuceneService luceneService) {
        this.pageRepository = pageRepository;
        this.luceneService = luceneService;
    }

    @Override
    public void save(String url) {
        if (set.contains(url)) {
            return;

        }
        set.add(url);
        org.jsoup.nodes.Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.warn("Can't get document!");
            return;
        }

        String body = doc.body().text().toLowerCase();
        String title = doc.title();
        try {
            luceneService.createIndex(url, title, body);
        } catch (IOException e) {
            log.warn("Can't create index!");
            return;
        }

        pageRepository.save(new Page(url, title, body));

        Elements elements = doc.select("a[href]");

        if (level > 0) {
            level--;
            EXECUTOR_SERVICE.execute(new NextPage(elements, this));
        }
    }

    @Override
    public Set<Page> pagination(String sortOrder, String sortBy,
                                Integer pageNo, Integer pageSize, String query) {
        List<Document> documents;
        Set<Page> pages = new HashSet<>();
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));
        try {
            documents = luceneService.search("body", query);
            for (int i = 0; i < documents.size(); i++) {
                pages.addAll((pageRepository.findByUrl(documents.get(i).get("url"), paging)));
            }
        } catch (ParseException | IOException e) {
            log.error("Url is not found ", e);
        }
        return pages;
    }
}
