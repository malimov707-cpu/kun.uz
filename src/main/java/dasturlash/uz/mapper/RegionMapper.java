package dasturlash.uz.mapper;

import dasturlash.uz.dto.RegionDTO;
import dasturlash.uz.entity.Region;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RegionMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    Region toEntity(RegionDTO dto);

    RegionDTO toDto(Region region);
}
