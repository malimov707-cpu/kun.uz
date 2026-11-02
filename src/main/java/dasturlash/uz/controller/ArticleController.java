package dasturlash.uz.controller;

import dasturlash.uz.dto.ArticleDTO;
import dasturlash.uz.dto.ArticleCreateRequest;
import dasturlash.uz.dto.ArticleFullInfo;
import dasturlash.uz.dto.ArticleShortInfo;
import dasturlash.uz.dto.ArticleStatusRequest;
import dasturlash.uz.dto.ArticleUpdateRequest;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.enums.ArticleStatus;
import dasturlash.uz.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/moderator")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MODERATOR')")
    public ArticleDTO create(@RequestBody @Valid ArticleCreateRequest dto) {
        return articleService.create(dto);
    }

    @PutMapping("/moderator/{articleId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ArticleDTO update(@PathVariable("articleId") String articleId,
                             @RequestBody @Valid ArticleUpdateRequest dto) {
        return articleService.update(articleId, dto);
    }

    @DeleteMapping("/moderator/{articleId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Void> delete(@PathVariable("articleId") String articleId) {
        articleService.delete(articleId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/moderator/{articleId}/status")
    @PreAuthorize("hasRole('PUBLISH')")
    public ResponseEntity<String> changeStatus(@PathVariable("articleId") String articleId,
                                               @RequestBody @Valid ArticleStatusRequest dto) {
        articleService.changeStatus(articleId, dto.getStatus(), dto.getPublisherId());
        return ResponseEntity.ok("Article status updated");
    }

    @GetMapping("/{articleId}")
    public ArticleFullInfo getById(@PathVariable String articleId,
                                   @RequestHeader(name = "Accept-Language", defaultValue = "uz") String language) {
        return articleService.getFullInfo(articleId, AppLanguageEnum.from(language));
    }

    @GetMapping("/types/latest")
    public List<ArticleShortInfo> latestByTypes(@RequestParam List<Long> typeIds) {
        return articleService.latestByTypes(typeIds, 5);
    }

    @GetMapping("/types/latest-three")
    public List<ArticleShortInfo> latestThreeByTypes(@RequestParam List<Long> typeIds) {
        return articleService.latestByTypes(typeIds, 3);
    }

    @GetMapping("/latest-except")
    public List<ArticleShortInfo> latestExceptIds(@RequestParam(required = false) List<String> ids) {
        return articleService.latestExcludingIds(ids);
    }

    @GetMapping("/types/latest-except-article")
    public List<ArticleShortInfo> latestByTypesExceptArticle(@RequestParam List<Long> typeIds,
                                                             @RequestParam String articleId) {
        return articleService.latestByTypesExceptArticle(typeIds, articleId);
    }

    @GetMapping("/most-read")
    public List<ArticleShortInfo> mostRead() {
        return articleService.mostRead();
    }

    @GetMapping("/tags/{tagName}/latest")
    public List<ArticleShortInfo> latestByTag(@PathVariable String tagName,
                                              @RequestParam(required = false) String lastArticleId) {
        return articleService.latestByTag(tagName, lastArticleId);
    }

    @GetMapping("/types/latest-by-region")
    public List<ArticleShortInfo> latestByTypesAndRegion(@RequestParam List<Long> typeIds,
                                                          @RequestParam String regionKey) {
        return articleService.latestByTypesAndRegion(typeIds, regionKey);
    }

    @GetMapping("/regions/{regionKey}")
    public Page<ArticleShortInfo> byRegion(@PathVariable String regionKey,
                                           @PageableDefault(size = 20) Pageable pageable) {
        return articleService.byRegion(regionKey, pageable);
    }

    @GetMapping("/categories/{categoryKey}/latest")
    public List<ArticleShortInfo> latestByCategory(@PathVariable String categoryKey) {
        return articleService.latestByCategory(categoryKey);
    }

    @GetMapping("/categories/{categoryKey}")
    public Page<ArticleShortInfo> byCategory(@PathVariable String categoryKey,
                                             @PageableDefault(size = 20) Pageable pageable) {
        return articleService.byCategory(categoryKey, pageable);
    }

    @PostMapping("/{articleId}/view")
    public ResponseEntity<Void> increaseViewCount(@PathVariable String articleId) {
        articleService.increaseViewCount(articleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{articleId}/share")
    public ResponseEntity<Void> increaseSharedCount(@PathVariable String articleId) {
        articleService.increaseSharedCount(articleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('PUBLISH')")
    public Page<ArticleShortInfo> filter(@RequestParam(required = false) String id,
                                         @RequestParam(required = false) String title,
                                         @RequestParam(required = false) Long regionId,
                                         @RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) LocalDateTime createdDateFrom,
                                         @RequestParam(required = false) LocalDateTime createdDateTo,
                                         @RequestParam(required = false) LocalDateTime publishedDateFrom,
                                         @RequestParam(required = false) LocalDateTime publishedDateTo,
                                         @RequestParam(required = false) Long moderatorId,
                                         @RequestParam(required = false) Long publisherId,
                                         @RequestParam(required = false) ArticleStatus status,
                                         @PageableDefault(size = 20) Pageable pageable) {
        return articleService.filter(id, title, regionId, categoryId, createdDateFrom, createdDateTo,
                publishedDateFrom, publishedDateTo, moderatorId, publisherId, status, pageable);
    }
}
