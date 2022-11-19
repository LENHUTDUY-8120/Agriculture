package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Fertilizers;
import com.lnduy.agriculture.service.dto.FertilizersDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Fertilizers} and its DTO {@link FertilizersDTO}.
 */
@Mapper(componentModel = "spring")
public interface FertilizersMapper extends EntityMapper<FertilizersDTO, Fertilizers> {}
