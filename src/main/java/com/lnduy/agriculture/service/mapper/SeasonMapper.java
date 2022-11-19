package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Crops;
import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.domain.Season;
import com.lnduy.agriculture.service.dto.CropsDTO;
import com.lnduy.agriculture.service.dto.FieldDTO;
import com.lnduy.agriculture.service.dto.SeasonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Season} and its DTO {@link SeasonDTO}.
 */
@Mapper(componentModel = "spring")
public interface SeasonMapper extends EntityMapper<SeasonDTO, Season> {
    @Mapping(target = "crop", source = "crop", qualifiedByName = "cropsId")
    @Mapping(target = "field", source = "field", qualifiedByName = "fieldId")
    SeasonDTO toDto(Season s);

    @Named("cropsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CropsDTO toDtoCropsId(Crops crops);

    @Named("fieldId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FieldDTO toDtoFieldId(Field field);
}
