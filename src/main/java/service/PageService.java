package service;

import java.io.IOException;
import java.util.Set;
import model.Page;
import org.springframework.stereotype.Component;

@Component
public interface PageService {
    void save(String url) throws IOException;

    Set<Page> pagination(String sortOrder, String sortBy,
                         Integer pageNo, Integer pageSize, String query);

}
