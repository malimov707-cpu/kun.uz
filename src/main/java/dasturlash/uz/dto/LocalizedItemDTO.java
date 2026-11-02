package dasturlash.uz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalizedItemDTO {
    private Long id;
    private Integer orderNumber;
    private String name;
    private String key;
}
