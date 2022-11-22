package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Device;
import com.lnduy.agriculture.domain.DeviceCategory;
import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.service.dto.DeviceCategoryDTO;
import com.lnduy.agriculture.service.dto.DeviceDTO;
import com.lnduy.agriculture.service.dto.FieldDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Device} and its DTO {@link DeviceDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeviceMapper extends EntityMapper<DeviceDTO, Device> {
    @Mapping(target = "category", source = "category", qualifiedByName = "deviceCategoryId")
    @Mapping(target = "field", source = "field", qualifiedByName = "fieldId")
    DeviceDTO toDto(Device s);

    @Named("deviceCategoryId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    DeviceCategoryDTO toDtoDeviceCategoryId(DeviceCategory deviceCategory);

    @Named("fieldId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    FieldDTO toDtoFieldId(Field field);
}
