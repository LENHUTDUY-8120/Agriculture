package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Crops;
import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.service.dto.CropsDTO;
import com.lnduy.agriculture.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Crops} and its DTO {@link CropsDTO}.
 */
@Mapper(componentModel = "spring")
public interface CropsMapper extends EntityMapper<CropsDTO, Crops> {
    @Mapping(target = "warehouse", source = "warehouse", qualifiedByName = "warehouseId")
    CropsDTO toDto(Crops s);

    @Named("warehouseId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    WarehouseDTO toDtoWarehouseId(Warehouse warehouse);
}
