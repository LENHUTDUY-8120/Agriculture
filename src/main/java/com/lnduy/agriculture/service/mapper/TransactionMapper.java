package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Season;
import com.lnduy.agriculture.domain.Transaction;
import com.lnduy.agriculture.service.dto.SeasonDTO;
import com.lnduy.agriculture.service.dto.TransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Transaction} and its DTO {@link TransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {
    @Mapping(target = "season", source = "season", qualifiedByName = "seasonId")
    TransactionDTO toDto(Transaction s);

    @Named("seasonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SeasonDTO toDtoSeasonId(Season season);
}
