package controller;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.PageService;

@Controller
@RequestMapping("/index")
public class IndexController {
    private Logger log = Logger.getLogger("IndexController.class");
    private PageService pageService;

    @Autowired
    public IndexController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/page2")
    public String submit(@RequestParam("url") String url) {
        try {
            pageService.save(url);
        } catch (IOException e) {
            log.warn("URL not found");
        }
        return "page2";
    }
}
