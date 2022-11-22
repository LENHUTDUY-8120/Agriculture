package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Device;
import com.lnduy.agriculture.domain.Field;
import com.lnduy.agriculture.domain.Monitoring;
import com.lnduy.agriculture.service.dto.DeviceDTO;
import com.lnduy.agriculture.service.dto.FieldDTO;
import com.lnduy.agriculture.service.dto.MonitoringDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Monitoring} and its DTO {@link MonitoringDTO}.
 */
@Mapper(componentModel = "spring")
public interface MonitoringMapper extends EntityMapper<MonitoringDTO, Monitoring> {
    @Mapping(target = "field", source = "field", qualifiedByName = "fieldId")
    @Mapping(target = "device", source = "device", qualifiedByName = "deviceId")
    MonitoringDTO toDto(Monitoring s);

    @Named("fieldId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    FieldDTO toDtoFieldId(Field field);

    @Named("deviceId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    DeviceDTO toDtoDeviceId(Device device);
}
