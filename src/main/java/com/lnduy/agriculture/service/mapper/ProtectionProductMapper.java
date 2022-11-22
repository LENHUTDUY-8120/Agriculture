package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.ProtectionProduct;
import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.service.dto.ProtectionProductDTO;
import com.lnduy.agriculture.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProtectionProduct} and its DTO {@link ProtectionProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProtectionProductMapper extends EntityMapper<ProtectionProductDTO, ProtectionProduct> {
    @Mapping(target = "warehouse", source = "warehouse", qualifiedByName = "warehouseId")
    ProtectionProductDTO toDto(ProtectionProduct s);

    @Named("warehouseId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    WarehouseDTO toDtoWarehouseId(Warehouse warehouse);
}
