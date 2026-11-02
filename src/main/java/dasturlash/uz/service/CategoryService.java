package dasturlash.uz.service;

import dasturlash.uz.dto.LocalizedItemDTO;
import dasturlash.uz.entity.CategoryEntity;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.repository.CategoryRepository;
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
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public CategoryEntity create(CategoryEntity item) {
        ensureKeyAvailable(item.getCategoryKey(), null);
        item.setVisible(true);
        return repository.save(item);
    }

    @Transactional(readOnly = true)
    public Page<CategoryEntity> getAll(Pageable pageable) {
        return repository.findAllByVisibleTrue(pageable);
    }

    @Transactional(readOnly = true)
    public List<LocalizedItemDTO> getAllByLang(AppLanguageEnum language) {
        return repository.findAllByVisibleTrue(Pageable.unpaged()).getContent().stream()
                .sorted(Comparator.comparing(CategoryEntity::getOrderNumber))
                .map(item -> {
                    LocalizedItemDTO dto = new LocalizedItemDTO();
                    dto.setId(item.getId());
                    dto.setOrderNumber(item.getOrderNumber());
                    dto.setName(switch (language) {
                        case UZ -> item.getNameUz();
                        case RU -> item.getNameRu();
                        case EN -> item.getNameEn();
                    });
                    dto.setKey(item.getCategoryKey());
                    return dto;
                })
                .toList();
    }

    @Transactional
    public CategoryEntity update(Long id, CategoryEntity item) {
        CategoryEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category topilmadi"));
        if (!Boolean.TRUE.equals(existing.getVisible())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category topilmadi");
        }
        ensureKeyAvailable(item.getCategoryKey(), id);
        existing.setOrderNumber(item.getOrderNumber());
        existing.setNameUz(item.getNameUz());
        existing.setNameRu(item.getNameRu());
        existing.setNameEn(item.getNameEn());
        existing.setCategoryKey(item.getCategoryKey());
        if (item.getVisible() != null) {
            existing.setVisible(item.getVisible());
        }
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        CategoryEntity item = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category topilmadi"));
        if (!Boolean.TRUE.equals(item.getVisible())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category topilmadi");
        }
        item.setVisible(false);
    }

    @Transactional(readOnly = true)
    public CategoryEntity getById(Long id) {
        return repository.findById(id)
                .filter(item -> Boolean.TRUE.equals(item.getVisible()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category topilmadi"));
    }

    private void ensureKeyAvailable(String categoryKey, Long currentId) {
        repository.findByCategoryKey(categoryKey)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Category key already exists");
                });
    }
}
