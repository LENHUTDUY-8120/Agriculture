package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.ProtectionProduct;
import com.lnduy.agriculture.service.dto.ProtectionProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProtectionProduct} and its DTO {@link ProtectionProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProtectionProductMapper extends EntityMapper<ProtectionProductDTO, ProtectionProduct> {}
