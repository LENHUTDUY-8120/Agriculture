package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.domain.SoilType;
import com.lnduy.agriculture.service.dto.FieldDTO;
import com.lnduy.agriculture.service.dto.SoilTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Field} and its DTO {@link FieldDTO}.
 */
@Mapper(componentModel = "spring")
public interface FieldMapper extends EntityMapper<FieldDTO, Field> {
    @Mapping(target = "soil", source = "soil", qualifiedByName = "soilTypeId")
    FieldDTO toDto(Field s);

    @Named("soilTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SoilTypeDTO toDtoSoilTypeId(SoilType soilType);
}
