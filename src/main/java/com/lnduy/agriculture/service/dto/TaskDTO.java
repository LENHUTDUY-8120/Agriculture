package com.lnduy.agriculture.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.lnduy.agriculture.domain.Task} entity.
 */
@Schema(description = "Task entity.\n@author The JHipster team.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskDTO implements Serializable {

    private Long id;

    private String title;

    private String description;

    private Integer enable;

    private String loop;

    private LocalDate startDate;

    private LocalDate endDate;

    private SeasonDTO season;

    private Set<EmployeeDTO> employees = new HashSet<>();

    private Set<SuppliesDTO> supplies = new HashSet<>();

    private Set<ProtectionProductDTO> protectionproducts = new HashSet<>();

    private Set<FertilizersDTO> fertilizers = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getLoop() {
        return loop;
    }

    public void setLoop(String loop) {
        this.loop = loop;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SeasonDTO getSeason() {
        return season;
    }

    public void setSeason(SeasonDTO season) {
        this.season = season;
    }

    public Set<EmployeeDTO> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<EmployeeDTO> employees) {
        this.employees = employees;
    }

    public Set<SuppliesDTO> getSupplies() {
        return supplies;
    }

    public void setSupplies(Set<SuppliesDTO> supplies) {
        this.supplies = supplies;
    }

    public Set<ProtectionProductDTO> getProtectionproducts() {
        return protectionproducts;
    }

    public void setProtectionproducts(Set<ProtectionProductDTO> protectionproducts) {
        this.protectionproducts = protectionproducts;
    }

    public Set<FertilizersDTO> getFertilizers() {
        return fertilizers;
    }

    public void setFertilizers(Set<FertilizersDTO> fertilizers) {
        this.fertilizers = fertilizers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskDTO)) {
            return false;
        }

        TaskDTO taskDTO = (TaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", enable=" + getEnable() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", season=" + getSeason() +
            ", employees=" + getEmployees() +
            ", supplies=" + getSupplies() +
            ", protectionproducts=" + getProtectionproducts() +
            ", fertilizers=" + getFertilizers() +
            "}";
    }
}
