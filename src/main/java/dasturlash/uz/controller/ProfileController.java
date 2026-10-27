package dasturlash.uz.controller;

import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.mapper.ProfileMapper;
import dasturlash.uz.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {
    private final ProfileService service;
    private final ProfileMapper mapper;

    public ProfileController(ProfileService service, ProfileMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileDTO create(@Valid @RequestBody ProfileDTO profile) {
        return mapper.toDto(service.create(mapper.toEntity(profile)));
    }

    @GetMapping
    public Page<ProfileDTO> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return service.getAll(pageable).map(mapper::toDto);
    }

    @GetMapping("/{id}")
    public ProfileDTO getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }

    @PutMapping("/{id}")
    public ProfileDTO update(@PathVariable Long id, @Valid @RequestBody ProfileDTO profile) {
        return mapper.toDto(service.update(id, mapper.toEntity(profile)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
