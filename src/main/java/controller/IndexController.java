package controller;

import java.io.IOException;
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

    @Autowired
    private PageService pageService;

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/page2")
    public String submit(@RequestParam("q") String url) throws IOException {
        pageService.save(url);
        return "page2";
    }
}
