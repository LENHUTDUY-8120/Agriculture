package com.lnduy.agriculture.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A Device.
 */
@Entity
@Table(name = "device")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "ip")
    private String ip;

    @Column(name = "property")
    private String property;

    @Column(name = "type")
    private String type;

    @Column(name = "enable")
    private Integer enable;

    @ManyToOne
    private DeviceCategory category;

    @ManyToOne
    @JsonIgnoreProperties(value = { "seasons", "devices", "soil" }, allowSetters = true)
    private Field field;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Device id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Device name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public Device code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIp() {
        return this.ip;
    }

    public Device ip(String ip) {
        this.setIp(ip);
        return this;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getProperty() {
        return this.property;
    }

    public Device property(String property) {
        this.setProperty(property);
        return this;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getType() {
        return this.type;
    }

    public Device type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getEnable() {
        return this.enable;
    }

    public Device enable(Integer enable) {
        this.setEnable(enable);
        return this;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public DeviceCategory getCategory() {
        return this.category;
    }

    public void setCategory(DeviceCategory deviceCategory) {
        this.category = deviceCategory;
    }

    public Device category(DeviceCategory deviceCategory) {
        this.setCategory(deviceCategory);
        return this;
    }

    public Field getField() {
        return this.field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Device field(Field field) {
        this.setField(field);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Device)) {
            return false;
        }
        return id != null && id.equals(((Device) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Device{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", ip='" + getIp() + "'" +
            ", property='" + getProperty() + "'" +
            ", type='" + getType() + "'" +
            ", enable=" + getEnable() +
            "}";
    }
}
