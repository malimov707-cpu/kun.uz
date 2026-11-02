package dasturlash.uz.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleUpdateRequest {
    private String title;
    private String description;
    private String content;
    private Long sharedCount;
    private Long regionId;
    private Long categoryId;
    private List<Long> articleTypeIds;
    private List<String> tags;
}
