package dasturlash.uz.controller;

import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.mapper.ProfileMapper;
import dasturlash.uz.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    @GetMapping("/filter")
    public Page<ProfileDTO> filter(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String surname,
                                   @RequestParam(required = false) String phone,
                                   @RequestParam(required = false) ProfileRole role,
                                   @RequestParam(required = false) LocalDateTime createdDateFrom,
                                   @RequestParam(required = false) LocalDateTime createdDateTo,
                                   @PageableDefault(size = 20) Pageable pageable) {
        return service.filter(name, surname, phone, role, createdDateFrom, createdDateTo, pageable)
                .map(mapper::toDto);
    }

    @GetMapping("/{id}")
    public ProfileDTO getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }

    @PutMapping("/{id}")
    public ProfileDTO update(@PathVariable Long id, @Valid @RequestBody ProfileDTO profile) {
        return mapper.toDto(service.update(id, mapper.toUpdateEntity(profile)));
    }

    @PutMapping("/{id}/photo")
    public ProfileDTO updatePhoto(@PathVariable Long id, @RequestParam(required = false) Long photoId) {
        return mapper.toDto(service.updatePhoto(id, photoId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
