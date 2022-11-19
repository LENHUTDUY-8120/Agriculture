package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.SoilType;
import com.lnduy.agriculture.service.dto.SoilTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SoilType} and its DTO {@link SoilTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface SoilTypeMapper extends EntityMapper<SoilTypeDTO, SoilType> {}
