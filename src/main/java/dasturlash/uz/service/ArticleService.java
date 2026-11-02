package dasturlash.uz.service;

import dasturlash.uz.dto.*;
import dasturlash.uz.entity.*;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.enums.ArticleStatus;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ArticleService {
    private static final ArticleStatus PUBLISHED = ArticleStatus.PUBLISHED;

    private final ArticleRepository articleRepository;
    private final ArticleTypeRepository articleTypeRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileRepository profileRepository;
    private final RegionRepository regionRepository;
    private final TagRepository tagRepository;
    private final ArticleLikeRepository articleLikeRepository;

    public ArticleService(ArticleRepository articleRepository, ArticleTypeRepository articleTypeRepository,
                         CategoryRepository categoryRepository, ProfileRepository profileRepository,
                         RegionRepository regionRepository, TagRepository tagRepository,
                         ArticleLikeRepository articleLikeRepository) {
        this.articleRepository = articleRepository;
        this.articleTypeRepository = articleTypeRepository;
        this.categoryRepository = categoryRepository;
        this.profileRepository = profileRepository;
        this.regionRepository = regionRepository;
        this.tagRepository = tagRepository;
        this.articleLikeRepository = articleLikeRepository;
    }

    @Transactional
    public ArticleDTO create(ArticleCreateRequest request) {
        ArticleEntity article = new ArticleEntity();
        article.setTitle(request.getTitle());
        article.setDescription(request.getDescription());
        article.setContent(request.getContent());
        article.setStatus(ArticleStatus.NOT_PUBLISHED);
        article.setRegion(getRegion(request.getRegionId()));
        article.setCategory(getCategory(request.getCategoryId()));
        if (request.getModeratorId() != null) {
            article.setModerator(getModerator(request.getModeratorId()));
        }
        article.setArticleTypes(resolveArticleTypes(request.getArticleTypeIds()));
        article.setTags(resolveTags(request.getTags()));
        return toDto(articleRepository.save(article));
    }

    @Transactional
    public ArticleDTO update(String articleId, ArticleUpdateRequest request) {
        ArticleEntity article = getVisibleArticle(articleId);
        if (request.getTitle() != null) article.setTitle(request.getTitle());
        if (request.getDescription() != null) article.setDescription(request.getDescription());
        if (request.getContent() != null) article.setContent(request.getContent());
        if (request.getSharedCount() != null) article.setSharedCount(request.getSharedCount());
        if (request.getRegionId() != null) article.setRegion(getRegion(request.getRegionId()));
        if (request.getCategoryId() != null) article.setCategory(getCategory(request.getCategoryId()));
        if (request.getArticleTypeIds() != null) {
            article.setArticleTypes(resolveArticleTypes(request.getArticleTypeIds()));
        }
        if (request.getTags() != null) article.setTags(resolveTags(request.getTags()));
        article.setStatus(ArticleStatus.NOT_PUBLISHED);
        article.setPublishedDate(null);
        article.setPublisher(null);
        return toDto(articleRepository.save(article));
    }

    @Transactional
    public void delete(String articleId) {
        getVisibleArticle(articleId).setVisible(false);
    }

    @Transactional
    public void changeStatus(String articleId, ArticleStatus status, Long publisherId) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        ArticleEntity article = getVisibleArticle(articleId);
        article.setStatus(status);
        if (status == PUBLISHED) {
            if (publisherId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "publisherId is required to publish");
            }
            Profile publisher = profileRepository.findById(publisherId)
                    .filter(Profile::isVisible)
                    .filter(profile -> profile.getRole() == ProfileRole.ROLE_PUBLISH)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
            article.setPublisher(publisher);
            article.setPublishedDate(LocalDateTime.now());
        } else {
            article.setPublisher(null);
            article.setPublishedDate(null);
        }
    }

    @Transactional(readOnly = true)
    public ArticleFullInfo getFullInfo(String articleId, AppLanguageEnum language) {
        ArticleEntity article = articleRepository.findById(articleId)
                .filter(item -> Boolean.TRUE.equals(item.getVisible()) && item.getStatus() == PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        ArticleFullInfo dto = new ArticleFullInfo();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setContent(article.getContent());
        dto.setSharedCount(article.getSharedCount());
        dto.setPublishedDate(article.getPublishedDate());
        dto.setViewCount(article.getViewCount());
        dto.setLikeCount(articleLikeRepository.countActiveByArticleId(articleId));
        if (article.getRegion() != null) {
            dto.setRegionKey(article.getRegion().getRegionKey());
            dto.setRegionName(localized(article.getRegion().getNameUz(),
                    article.getRegion().getNameRu(), article.getRegion().getNameEn(), language));
        }
        if (article.getCategory() != null) {
            dto.setCategoryKey(article.getCategory().getCategoryKey());
            dto.setCategoryName(localized(article.getCategory().getNameUz(),
                    article.getCategory().getNameRu(), article.getCategory().getNameEn(), language));
        }
        dto.setTagList(article.getTags().stream().map(TagEntity::getName).sorted().toList());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ArticleShortInfo> latestByTypes(List<Long> typeIds, int count) {
        requireIds(typeIds, "typeIds");
        return shortInfo(articleRepository.findLatestByTypes(typeIds, PUBLISHED, PageRequest.of(0, count)));
    }

    @Transactional(readOnly = true)
    public List<ArticleShortInfo> latestExcludingIds(List<String> excludedIds) {
        List<ArticleEntity> articles = excludedIds == null || excludedIds.isEmpty()
                ? articleRepository.findByVisibleTrueAndStatusOrderByCreatedDateDesc(PUBLISHED, PageRequest.of(0, 8))
                : articleRepository.findLatestExcludingIds(new ArrayList<>(new LinkedHashSet<>(excludedIds)),
                        PUBLISHED, PageRequest.of(0, 8));
        return shortInfo(articles);
    }

    @Transactional(readOnly = true)
    public List<ArticleShortInfo> latestByTypesExceptArticle(List<Long> typeIds, String articleId) {
        requireIds(typeIds, "typeIds");
        return shortInfo(articleRepository.findLatestByTypesExceptArticle(
                typeIds, articleId, PUBLISHED, PageRequest.of(0, 4)));
    }

    @Transactional(readOnly = true)
    public List<ArticleShortInfo> mostRead() {
        return shortInfo(articleRepository.findByVisibleTrueAndStatusOrderByViewCountDescCreatedDateDesc(
                PUBLISHED, PageRequest.of(0, 4)));
    }

    @Transactional(readOnly = true)
    public List<ArticleShortInfo> latestByTag(String tagName, String lastArticleId) {
        if (tagName == null || tagName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tagName is required");
        }
        List<ArticleEntity> articles = articleRepository.findByTagNameExceptArticle(
                tagName, lastArticleId == null ? "" : lastArticleId, PUBLISHED, PageRequest.of(0, 4));
        if (lastArticleId != null) {
            articleRepository.findByIdAndTagName(lastArticleId, tagName, PUBLISHED)
                    .ifPresent(articles::add);
        }
        return shortInfo(articles);
    }

    @Transactional(readOnly = true)
    public List<ArticleShortInfo> latestByTypesAndRegion(List<Long> typeIds, String regionKey) {
        requireIds(typeIds, "typeIds");
        return shortInfo(articleRepository.findLatestByTypesAndRegionKey(
                typeIds, regionKey, PUBLISHED, PageRequest.of(0, 5)));
    }

    @Transactional(readOnly = true)
    public Page<ArticleShortInfo> byRegion(String regionKey, Pageable pageable) {
        return articleRepository.findByRegionKey(regionKey, PUBLISHED, pageable).map(this::shortInfo);
    }

    @Transactional(readOnly = true)
    public List<ArticleShortInfo> latestByCategory(String categoryKey) {
        return shortInfo(articleRepository.findLatestByCategoryKey(
                categoryKey, PUBLISHED, PageRequest.of(0, 5)));
    }

    @Transactional(readOnly = true)
    public Page<ArticleShortInfo> byCategory(String categoryKey, Pageable pageable) {
        return articleRepository.findByCategoryKey(categoryKey, PUBLISHED, pageable).map(this::shortInfo);
    }

    @Transactional
    public void increaseViewCount(String articleId) {
        requireUpdated(articleRepository.incrementViewCount(articleId), articleId);
    }

    @Transactional
    public void increaseSharedCount(String articleId) {
        requireUpdated(articleRepository.incrementSharedCount(articleId), articleId);
    }

    @Transactional(readOnly = true)
    public Page<ArticleShortInfo> filter(String id, String title, Long regionId, Long categoryId,
                                         LocalDateTime createdFrom, LocalDateTime createdTo,
                                         LocalDateTime publishedFrom, LocalDateTime publishedTo,
                                         Long moderatorId, Long publisherId, ArticleStatus status,
                                         Pageable pageable) {
        validateDateRange(createdFrom, createdTo, "created");
        validateDateRange(publishedFrom, publishedTo, "published");
        return articleRepository.search(id, title, regionId, categoryId, createdFrom, createdTo,
                publishedFrom, publishedTo, moderatorId, publisherId, status, pageable).map(this::shortInfo);
    }

    private Region getRegion(Long id) {
        return regionRepository.findById(id)
                .filter(item -> Boolean.TRUE.equals(item.getVisible()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found"));
    }

    private CategoryEntity getCategory(Long id) {
        return categoryRepository.findById(id)
                .filter(item -> Boolean.TRUE.equals(item.getVisible()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private Profile getModerator(Long id) {
        return profileRepository.findById(id)
                .filter(Profile::isVisible)
                .filter(profile -> profile.getRole() == ProfileRole.ROLE_MODERATOR)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Moderator not found"));
    }

    private Set<ArticleType> resolveArticleTypes(List<Long> ids) {
        requireIds(ids, "articleTypeIds");
        List<Long> distinctIds = new ArrayList<>(new LinkedHashSet<>(ids));
        List<ArticleType> types = articleTypeRepository.findAllById(distinctIds).stream()
                .filter(type -> Boolean.TRUE.equals(type.getVisible()))
                .toList();
        if (types.size() != distinctIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more article types not found");
        }
        return new HashSet<>(types);
    }

    private Set<TagEntity> resolveTags(List<String> names) {
        if (names == null || names.isEmpty()) return new HashSet<>();
        Map<String, TagEntity> tags = new LinkedHashMap<>();
        for (String value : names) {
            if (value == null || value.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag names cannot be blank");
            }
            String name = value.trim();
            TagEntity tag = tagRepository.findByNameIgnoreCase(name).orElseGet(() -> {
                TagEntity created = new TagEntity();
                created.setName(name);
                return tagRepository.save(created);
            });
            tags.put(name.toLowerCase(Locale.ROOT), tag);
        }
        return new HashSet<>(tags.values());
    }

    private ArticleEntity getVisibleArticle(String articleId) {
        return articleRepository.findById(articleId)
                .filter(article -> Boolean.TRUE.equals(article.getVisible()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
    }

    private void requireUpdated(int updated, String articleId) {
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found");
        }
    }

    private void requireIds(List<Long> ids, String field) {
        if (ids == null || ids.isEmpty() || ids.stream().anyMatch(Objects::isNull)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must contain at least one id");
        }
    }

    private void validateDateRange(LocalDateTime from, LocalDateTime to, String name) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, name + " date range is invalid");
        }
    }

    private String localized(String uz, String ru, String en, AppLanguageEnum language) {
        return switch (language) {
            case UZ -> uz;
            case RU -> ru;
            case EN -> en;
        };
    }

    private List<ArticleShortInfo> shortInfo(List<ArticleEntity> articles) {
        return articles.stream().map(this::shortInfo).toList();
    }

    private ArticleShortInfo shortInfo(ArticleEntity article) {
        ArticleShortInfo dto = new ArticleShortInfo();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setPublishedDate(article.getPublishedDate());
        return dto;
    }

    private ArticleDTO toDto(ArticleEntity article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setContent(article.getContent());
        dto.setSharedCount(article.getSharedCount());
        dto.setRegionId(article.getRegion() == null ? null : article.getRegion().getId());
        dto.setCategoryId(article.getCategory() == null ? null : article.getCategory().getId());
        dto.setModeratorId(article.getModerator() == null ? null : article.getModerator().getId());
        dto.setPublisherId(article.getPublisher() == null ? null : article.getPublisher().getId());
        dto.setStatus(article.getStatus());
        dto.setViewCount(article.getViewCount());
        dto.setCreatedDate(article.getCreatedDate());
        dto.setPublishedDate(article.getPublishedDate());
        dto.setVisible(article.getVisible());
        dto.setArticleTypeIds(article.getArticleTypes().stream().map(ArticleType::getId).toList());
        return dto;
    }
}
