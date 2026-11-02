package dasturlash.uz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleCreateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String content;
    @NotNull
    private Long regionId;
    @NotNull
    private Long categoryId;
    @NotNull
    private List<Long> articleTypeIds;
    private List<String> tags;
    private Long moderatorId;
}
