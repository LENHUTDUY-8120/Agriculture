package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Warehouse} and its DTO {@link WarehouseDTO}.
 */
@Mapper(componentModel = "spring")
public interface WarehouseMapper extends EntityMapper<WarehouseDTO, Warehouse> {}
