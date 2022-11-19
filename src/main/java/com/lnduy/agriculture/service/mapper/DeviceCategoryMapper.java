package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.DeviceCategory;
import com.lnduy.agriculture.service.dto.DeviceCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DeviceCategory} and its DTO {@link DeviceCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeviceCategoryMapper extends EntityMapper<DeviceCategoryDTO, DeviceCategory> {}
