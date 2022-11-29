package com.lnduy.agriculture.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.lnduy.agriculture.domain.Monitoring} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MonitoringDTO implements Serializable {

    private Long id;

    private Double value;

    private LocalDate createdAt;

    private FieldDTO field;

    private DeviceDTO device;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public FieldDTO getField() {
        return field;
    }

    public void setField(FieldDTO field) {
        this.field = field;
    }

    public DeviceDTO getDevice() {
        return device;
    }

    public void setDevice(DeviceDTO device) {
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MonitoringDTO)) {
            return false;
        }

        MonitoringDTO monitoringDTO = (MonitoringDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, monitoringDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MonitoringDTO{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", field=" + getField() +
            ", device=" + getDevice() +
            "}";
    }
}
