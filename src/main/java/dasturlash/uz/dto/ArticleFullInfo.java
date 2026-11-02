package dasturlash.uz.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ArticleFullInfo {
    private String id;
    private String title;
    private String description;
    private String content;
    private Long sharedCount;
    private String regionKey;
    private String regionName;
    private String categoryKey;
    private String categoryName;
    private LocalDateTime publishedDate;
    private Integer viewCount;
    private Long likeCount;
    private List<String> tagList;
}
