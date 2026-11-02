package dasturlash.uz.dto;

import dasturlash.uz.enums.ArticleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleStatusRequest {
    @NotNull
    private ArticleStatus status;
    private Long publisherId;
}
