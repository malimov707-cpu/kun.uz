package dasturlash.uz.service;

import dasturlash.uz.dto.LocalizedItemDTO;
import dasturlash.uz.entity.Region;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.repository.RegionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class RegionService {
    private final RegionRepository repository;

    public RegionService(RegionRepository repository) {
        this.repository = repository;
    }

    public Region create(Region item) {
        ensureKeyAvailable(item.getItemKey(), null);
        item.setVisible(true);
        return repository.save(item);
    }

    @Transactional(readOnly = true)
    public Page<Region> getAll(Pageable pageable) {
        return repository.findAllByVisibleTrue(pageable);
    }

    @Transactional(readOnly = true)
    public List<LocalizedItemDTO> getAllByLang(AppLanguageEnum language) {
        return repository.findAllByVisibleTrue(Pageable.unpaged()).getContent().stream()
                .sorted(Comparator.comparing(Region::getOrderNumber))
                .map(item -> {
                    LocalizedItemDTO dto = new LocalizedItemDTO();
                    dto.setId(item.getId());
                    dto.setOrderNumber(item.getOrderNumber());
                    dto.setName(switch (language) {
                        case UZ -> item.getNameUz();
                        case RU -> item.getNameRu();
                        case EN -> item.getNameEn();
                    });
                    dto.setKey(item.getItemKey());
                    return dto;
                })
                .toList();
    }

    @Transactional
    public Region update(Long id, Region item) {
        Region existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region topilmadi"));
        if (!Boolean.TRUE.equals(existing.getVisible())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region topilmadi");
        }
        ensureKeyAvailable(item.getItemKey(), id);
        existing.setOrderNumber(item.getOrderNumber());
        existing.setNameUz(item.getNameUz());
        existing.setNameRu(item.getNameRu());
        existing.setNameEn(item.getNameEn());
        existing.setItemKey(item.getItemKey());
        if (item.getVisible() != null) {
            existing.setVisible(item.getVisible());
        }
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Region item = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region topilmadi"));
        if (!Boolean.TRUE.equals(item.getVisible())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region topilmadi");
        }
        item.setVisible(false);
    }

    @Transactional(readOnly = true)
    public Region getById(Long id) {
        return repository.findById(id)
                .filter(item -> Boolean.TRUE.equals(item.getVisible()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region topilmadi"));
    }

    private void ensureKeyAvailable(String itemKey, Long currentId) {
        repository.findByItemKey(itemKey)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Region key already exists");
                });
    }
}
