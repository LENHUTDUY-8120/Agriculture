package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.EventCategory;
import com.lnduy.agriculture.service.dto.EventCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventCategory} and its DTO {@link EventCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventCategoryMapper extends EntityMapper<EventCategoryDTO, EventCategory> {}
