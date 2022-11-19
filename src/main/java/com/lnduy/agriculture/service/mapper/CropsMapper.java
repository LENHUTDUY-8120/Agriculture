package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Crops;
import com.lnduy.agriculture.service.dto.CropsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Crops} and its DTO {@link CropsDTO}.
 */
@Mapper(componentModel = "spring")
public interface CropsMapper extends EntityMapper<CropsDTO, Crops> {}
