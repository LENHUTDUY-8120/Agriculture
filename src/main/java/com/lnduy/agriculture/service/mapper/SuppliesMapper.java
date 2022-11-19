package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Supplies;
import com.lnduy.agriculture.service.dto.SuppliesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Supplies} and its DTO {@link SuppliesDTO}.
 */
@Mapper(componentModel = "spring")
public interface SuppliesMapper extends EntityMapper<SuppliesDTO, Supplies> {}
