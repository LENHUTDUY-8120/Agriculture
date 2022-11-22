package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Fertilizers;
import com.lnduy.agriculture.domain.Warehouse;
import com.lnduy.agriculture.service.dto.FertilizersDTO;
import com.lnduy.agriculture.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Fertilizers} and its DTO {@link FertilizersDTO}.
 */
@Mapper(componentModel = "spring")
public interface FertilizersMapper extends EntityMapper<FertilizersDTO, Fertilizers> {
    @Mapping(target = "warehouse", source = "warehouse", qualifiedByName = "warehouseId")
    FertilizersDTO toDto(Fertilizers s);

    @Named("warehouseId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    WarehouseDTO toDtoWarehouseId(Warehouse warehouse);
}
