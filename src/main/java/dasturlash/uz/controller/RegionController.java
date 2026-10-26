package dasturlash.uz.controller;

import dasturlash.uz.entity.Region;
import dasturlash.uz.service.RegionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/regions")
public class RegionController {
    private final RegionService service;

    public RegionController(RegionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Region create(@Valid @RequestBody Region item) {
        return service.create(item);
    }

    @GetMapping
    public Page<Region> getAll(@PageableDefault(size = 20, sort = "orderNumber") Pageable pageable) {
        return service.getAll(pageable);
    }

    @PutMapping("/{id}")
    public Region update(@PathVariable Long id, @Valid @RequestBody Region item) {
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
