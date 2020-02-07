package controller;

import java.util.Set;
import model.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import service.PageService;

@Controller
@RequestMapping("/")
public class SearchController {
    private PageService pageService;

    @Autowired
    public SearchController(PageService pageService) {
        this.pageService = pageService;
    }

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
        Set<Page> pages = pageService.pagination(sortOrder, sortBy, pageNo, pageSize, query);
        model.addAttribute("pages", pages);
        return "page4";
    }
}
