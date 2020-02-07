package repository;

import java.util.List;
import model.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PageRepository extends JpaRepository<Page, Long> {
    List<Page> findByUrl(String url, Pageable pageable);
}
