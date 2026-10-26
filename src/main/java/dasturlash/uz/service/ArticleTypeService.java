package dasturlash.uz.service;

import dasturlash.uz.entity.ArticleType;
import dasturlash.uz.repository.ArticleTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArticleTypeService {
    private final ArticleTypeRepository repository;

    public ArticleTypeService(ArticleTypeRepository repository) {
        this.repository = repository;
    }

    public ArticleType create(ArticleType item) {
        return repository.save(item);
    }

    public Page<ArticleType> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public ArticleType update(Long id, ArticleType item) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ArticleType topilmadi");
        }
        item.setId(id);
        return repository.save(item);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ArticleType topilmadi");
        }
        repository.deleteById(id);
    }
}
