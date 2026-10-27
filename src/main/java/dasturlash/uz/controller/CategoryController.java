package dasturlash.uz.controller;

import dasturlash.uz.dto.CategoryDTO;
import dasturlash.uz.mapper.CategoryMapper;
import dasturlash.uz.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService service;
    private final CategoryMapper mapper;

    public CategoryController(CategoryService service, CategoryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO create(@Valid @RequestBody CategoryDTO item) {
        return mapper.toDto(service.create(mapper.toEntity(item)));
    }

    @GetMapping
    public Page<CategoryDTO> getAll(@PageableDefault(size = 20, sort = "orderNumber") Pageable pageable) {
        return service.getAll(pageable).map(mapper::toDto);
    }

    @PutMapping("/{id}")
    public CategoryDTO update(@PathVariable Long id, @Valid @RequestBody CategoryDTO item) {
        return mapper.toDto(service.update(id, mapper.toEntity(item)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
