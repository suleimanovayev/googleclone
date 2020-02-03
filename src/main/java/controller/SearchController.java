package controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Page;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import repository.PageRepository;
import service.PageService;

@Controller
@RequestMapping("/")
public class SearchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private PageService pageService;

    @Autowired
    private PageRepository pageRepository;

    @GetMapping
    public String root() {
        return "page3";
    }

    @PostMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam(defaultValue = "0") Integer pageNo,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         @RequestParam(defaultValue = "title") String sortBy,
                         @RequestParam(value = "sortOrder", required = false,
                                 defaultValue = "asc") String sortOrder,
                         Model model) {

        List<Document> documents;
        Set<Page> pages = new HashSet<>();

        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));

        try {
            documents = pageService.search("body", query);

            for (int i = 0; i < documents.size(); i++) {
                pages.addAll((pageRepository.findByUrl(documents.get(i).get("url"), paging)));
            }
        } catch (ParseException | IOException e) {
            LOGGER.error("Url is not found ", e);
        }
        model.addAttribute("pages", pages);
        return "page4";
    }
}
