package thread;

import java.io.IOException;

import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import service.PageService;

public class NextPage implements Runnable {
    private Logger log = Logger.getLogger(NextPage.class);

    private PageService pageService;
    private Elements links;

    public NextPage(Elements links, PageService pageService) {
        this.links = links;
        this.pageService = pageService;
    }

    @SneakyThrows
    @Override
    public void run() {
        for (Element element : links) {
            try {
                pageService.save(element.attr("abs:href"));
            } catch (IOException e) {
                log.warn("Cant find URL ", e);
            }
        }
    }
}
