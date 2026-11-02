package dasturlash.uz.mapper;

import dasturlash.uz.dto.ArticleTypeDTO;
import dasturlash.uz.entity.ArticleType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ArticleTypeMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "itemKey", source = "key")
    ArticleType toEntity(ArticleTypeDTO dto);

    @Mapping(target = "key", source = "itemKey")
    ArticleTypeDTO toDto(ArticleType articleType);
}
