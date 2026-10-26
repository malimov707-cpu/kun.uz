package dasturlash.uz.service;

import dasturlash.uz.entity.Category;
import dasturlash.uz.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category create(Category item) {
        return repository.save(item);
    }

    public Page<Category> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Category update(Long id, Category item) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category topilmadi");
        }
        item.setId(id);
        return repository.save(item);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category topilmadi");
        }
        repository.deleteById(id);
    }
}
