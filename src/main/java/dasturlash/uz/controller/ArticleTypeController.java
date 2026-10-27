package dasturlash.uz.controller;

import dasturlash.uz.dto.ArticleTypeDTO;
import dasturlash.uz.mapper.ArticleTypeMapper;
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
    private final ArticleTypeMapper mapper;

    public ArticleTypeController(ArticleTypeService service, ArticleTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleTypeDTO create(@Valid @RequestBody ArticleTypeDTO item) {
        return mapper.toDto(service.create(mapper.toEntity(item)));
    }

    @GetMapping
    public Page<ArticleTypeDTO> getAll(@PageableDefault(size = 20, sort = "orderNumber") Pageable pageable) {
        return service.getAll(pageable).map(mapper::toDto);
    }

    @PutMapping("/{id}")
    public ArticleTypeDTO update(@PathVariable Long id, @Valid @RequestBody ArticleTypeDTO item) {
        return mapper.toDto(service.update(id, mapper.toEntity(item)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
