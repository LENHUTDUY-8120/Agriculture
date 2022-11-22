package com.lnduy.agriculture.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Field.
 */
@Entity
@Table(name = "field")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Field implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "geo_json")
    private String geoJson;

    @Column(name = "area")
    private Double area;

    @Column(name = "descriptions")
    private String descriptions;

    @Column(name = "enable")
    private Integer enable;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @OneToMany(mappedBy = "field")
    @JsonIgnoreProperties(value = { "transactions", "crop", "field" }, allowSetters = true)
    private Set<Season> seasons = new HashSet<>();

    @OneToMany(mappedBy = "field")
    @JsonIgnoreProperties(value = { "category", "field" }, allowSetters = true)
    private Set<Device> devices = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private SoilType soil;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Field id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Field code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Field name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeoJson() {
        return this.geoJson;
    }

    public Field geoJson(String geoJson) {
        this.setGeoJson(geoJson);
        return this;
    }

    public void setGeoJson(String geoJson) {
        this.geoJson = geoJson;
    }

    public Double getArea() {
        return this.area;
    }

    public Field area(Double area) {
        this.setArea(area);
        return this;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getDescriptions() {
        return this.descriptions;
    }

    public Field descriptions(String descriptions) {
        this.setDescriptions(descriptions);
        return this;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public Integer getEnable() {
        return this.enable;
    }

    public Field enable(Integer enable) {
        this.setEnable(enable);
        return this;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Field latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Field longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Set<Season> getSeasons() {
        return this.seasons;
    }

    public void setSeasons(Set<Season> seasons) {
        if (this.seasons != null) {
            this.seasons.forEach(i -> i.setField(null));
        }
        if (seasons != null) {
            seasons.forEach(i -> i.setField(this));
        }
        this.seasons = seasons;
    }

    public Field seasons(Set<Season> seasons) {
        this.setSeasons(seasons);
        return this;
    }

    public Field addSeason(Season season) {
        this.seasons.add(season);
        season.setField(this);
        return this;
    }

    public Field removeSeason(Season season) {
        this.seasons.remove(season);
        season.setField(null);
        return this;
    }

    public Set<Device> getDevices() {
        return this.devices;
    }

    public void setDevices(Set<Device> devices) {
        if (this.devices != null) {
            this.devices.forEach(i -> i.setField(null));
        }
        if (devices != null) {
            devices.forEach(i -> i.setField(this));
        }
        this.devices = devices;
    }

    public Field devices(Set<Device> devices) {
        this.setDevices(devices);
        return this;
    }

    public Field addDevice(Device device) {
        this.devices.add(device);
        device.setField(this);
        return this;
    }

    public Field removeDevice(Device device) {
        this.devices.remove(device);
        device.setField(null);
        return this;
    }

    public SoilType getSoil() {
        return this.soil;
    }

    public void setSoil(SoilType soilType) {
        this.soil = soilType;
    }

    public Field soil(SoilType soilType) {
        this.setSoil(soilType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Field)) {
            return false;
        }
        return id != null && id.equals(((Field) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Field{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", geoJson='" + getGeoJson() + "'" +
            ", area=" + getArea() +
            ", descriptions='" + getDescriptions() + "'" +
            ", enable=" + getEnable() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            "}";
    }
}
