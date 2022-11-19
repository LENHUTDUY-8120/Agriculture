package com.lnduy.agriculture.service.mapper;

import com.lnduy.agriculture.domain.Employee;
import com.lnduy.agriculture.domain.Fertilizers;
import com.lnduy.agriculture.domain.ProtectionProduct;
import com.lnduy.agriculture.domain.Season;
import com.lnduy.agriculture.domain.Supplies;
import com.lnduy.agriculture.domain.Task;
import com.lnduy.agriculture.service.dto.EmployeeDTO;
import com.lnduy.agriculture.service.dto.FertilizersDTO;
import com.lnduy.agriculture.service.dto.ProtectionProductDTO;
import com.lnduy.agriculture.service.dto.SeasonDTO;
import com.lnduy.agriculture.service.dto.SuppliesDTO;
import com.lnduy.agriculture.service.dto.TaskDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "season", source = "season", qualifiedByName = "seasonId")
    @Mapping(target = "employees", source = "employees", qualifiedByName = "employeeIdSet")
    @Mapping(target = "supplies", source = "supplies", qualifiedByName = "suppliesIdSet")
    @Mapping(target = "protectionproducts", source = "protectionproducts", qualifiedByName = "protectionProductIdSet")
    @Mapping(target = "fertilizers", source = "fertilizers", qualifiedByName = "fertilizersIdSet")
    TaskDTO toDto(Task s);

    @Mapping(target = "removeEmployee", ignore = true)
    @Mapping(target = "removeSupplies", ignore = true)
    @Mapping(target = "removeProtectionproduct", ignore = true)
    @Mapping(target = "removeFertilizers", ignore = true)
    Task toEntity(TaskDTO taskDTO);

    @Named("seasonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SeasonDTO toDtoSeasonId(Season season);

    @Named("employeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EmployeeDTO toDtoEmployeeId(Employee employee);

    @Named("employeeIdSet")
    default Set<EmployeeDTO> toDtoEmployeeIdSet(Set<Employee> employee) {
        return employee.stream().map(this::toDtoEmployeeId).collect(Collectors.toSet());
    }

    @Named("suppliesId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SuppliesDTO toDtoSuppliesId(Supplies supplies);

    @Named("suppliesIdSet")
    default Set<SuppliesDTO> toDtoSuppliesIdSet(Set<Supplies> supplies) {
        return supplies.stream().map(this::toDtoSuppliesId).collect(Collectors.toSet());
    }

    @Named("protectionProductId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProtectionProductDTO toDtoProtectionProductId(ProtectionProduct protectionProduct);

    @Named("protectionProductIdSet")
    default Set<ProtectionProductDTO> toDtoProtectionProductIdSet(Set<ProtectionProduct> protectionProduct) {
        return protectionProduct.stream().map(this::toDtoProtectionProductId).collect(Collectors.toSet());
    }

    @Named("fertilizersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FertilizersDTO toDtoFertilizersId(Fertilizers fertilizers);

    @Named("fertilizersIdSet")
    default Set<FertilizersDTO> toDtoFertilizersIdSet(Set<Fertilizers> fertilizers) {
        return fertilizers.stream().map(this::toDtoFertilizersId).collect(Collectors.toSet());
    }
}
