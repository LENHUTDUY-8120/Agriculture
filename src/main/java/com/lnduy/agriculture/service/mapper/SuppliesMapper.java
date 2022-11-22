package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Supplies;
import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.service.dto.SuppliesDTO;
import com.lnduy.agriculture.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Supplies} and its DTO {@link SuppliesDTO}.
 */
@Mapper(componentModel = "spring")
public interface SuppliesMapper extends EntityMapper<SuppliesDTO, Supplies> {
    @Mapping(target = "warehouse", source = "warehouse", qualifiedByName = "warehouseId")
    SuppliesDTO toDto(Supplies s);

    @Named("warehouseId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    WarehouseDTO toDtoWarehouseId(Warehouse warehouse);
}
