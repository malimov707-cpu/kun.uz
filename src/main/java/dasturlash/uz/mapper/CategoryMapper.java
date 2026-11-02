package dasturlash.uz.mapper;

import dasturlash.uz.dto.CategoryDTO;
import dasturlash.uz.entity.CategoryEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "categoryKey", source = "key")
    CategoryEntity toEntity(CategoryDTO dto);

    @Mapping(target = "key", source = "categoryKey")
    CategoryDTO toDto(CategoryEntity category);
}
