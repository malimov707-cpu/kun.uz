package dasturlash.uz.service;

import dasturlash.uz.entity.Region;
import dasturlash.uz.repository.RegionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegionService {
    private final RegionRepository repository;

    public RegionService(RegionRepository repository) {
        this.repository = repository;
    }

    public Region create(Region item) {
        return repository.save(item);
    }

    public Page<Region> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Region update(Long id, Region item) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region topilmadi");
        }
        item.setId(id);
        return repository.save(item);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region topilmadi");
        }
        repository.deleteById(id);
    }
}
