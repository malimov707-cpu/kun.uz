package dasturlash.uz.mapper;

import dasturlash.uz.dto.ProfileDTO;
import dasturlash.uz.entity.Profile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "status", defaultExpression = "java(dasturlash.uz.enums.ProfileStatus.ACTIVE)")
    @Mapping(target = "role", defaultExpression = "java(dasturlash.uz.enums.ProfileRole.ROLE_USER)")
    Profile toEntity(ProfileDTO dto);

    @Mapping(target = "password", ignore = true)
    ProfileDTO toDto(Profile profile);
}
