package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Event;
import com.lnduy.agriculture.domain.EventCategory;
import com.lnduy.agriculture.service.dto.EventCategoryDTO;
import com.lnduy.agriculture.service.dto.EventDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Event} and its DTO {@link EventDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventMapper extends EntityMapper<EventDTO, Event> {
    @Mapping(target = "category", source = "category", qualifiedByName = "eventCategoryId")
    EventDTO toDto(Event s);

    @Named("eventCategoryId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    EventCategoryDTO toDtoEventCategoryId(EventCategory eventCategory);
}
