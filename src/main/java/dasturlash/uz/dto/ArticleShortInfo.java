package dasturlash.uz.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleShortInfo {
    private String id;
    private String title;
    private String description;
    private LocalDateTime publishedDate;
}
