package dasturlash.uz.controller;

import dasturlash.uz.dto.RegionDTO;
import dasturlash.uz.mapper.RegionMapper;
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
    private final RegionMapper mapper;

    public RegionController(RegionService service, RegionMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegionDTO create(@Valid @RequestBody RegionDTO item) {
        return mapper.toDto(service.create(mapper.toEntity(item)));
    }

    @GetMapping
    public Page<RegionDTO> getAll(@PageableDefault(size = 20, sort = "orderNumber") Pageable pageable) {
        return service.getAll(pageable).map(mapper::toDto);
    }

    @PutMapping("/{id}")
    public RegionDTO update(@PathVariable Long id, @Valid @RequestBody RegionDTO item) {
        return mapper.toDto(service.update(id, mapper.toEntity(item)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
