package dasturlash.uz.controller;

import dasturlash.uz.entity.ArticleType;
import dasturlash.uz.service.ArticleTypeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/article-types")
public class ArticleTypeController {
    private final ArticleTypeService service;

    public ArticleTypeController(ArticleTypeService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleType create(@Valid @RequestBody ArticleType item) {
        return service.create(item);
    }

    @GetMapping
    public Page<ArticleType> getAll(@PageableDefault(size = 20, sort = "orderNumber") Pageable pageable) {
        return service.getAll(pageable);
    }

    @PutMapping("/{id}")
    public ArticleType update(@PathVariable Long id, @Valid @RequestBody ArticleType item) {
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
