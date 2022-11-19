package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Employee;
import com.lnduy.agriculture.service.dto.EmployeeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Employee} and its DTO {@link EmployeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {}
