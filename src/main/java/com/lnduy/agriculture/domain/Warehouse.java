package com.lnduy.agriculture.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Warehouse.
 */
@Entity
@Table(name = "warehouse")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Warehouse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "area")
    private Double area;

    @Column(name = "description")
    private String description;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "enable")
    private Integer enable;

    @OneToMany(mappedBy = "warehouse")
    @JsonIgnoreProperties(value = { "warehouse", "tasks" }, allowSetters = true)
    private Set<Supplies> supplies = new HashSet<>();

    @OneToMany(mappedBy = "warehouse")
    @JsonIgnoreProperties(value = { "warehouse", "tasks" }, allowSetters = true)
    private Set<ProtectionProduct> protectionProducts = new HashSet<>();

    @OneToMany(mappedBy = "warehouse")
    @JsonIgnoreProperties(value = { "warehouse", "tasks" }, allowSetters = true)
    private Set<Fertilizers> fertilizers = new HashSet<>();

    @OneToMany(mappedBy = "warehouse")
    @JsonIgnoreProperties(value = { "warehouse" }, allowSetters = true)
    private Set<Crops> crops = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Warehouse id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Warehouse name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getArea() {
        return this.area;
    }

    public Warehouse area(Double area) {
        this.setArea(area);
        return this;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getDescription() {
        return this.description;
    }

    public Warehouse description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Warehouse latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Warehouse longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getEnable() {
        return this.enable;
    }

    public Warehouse enable(Integer enable) {
        this.setEnable(enable);
        return this;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Set<Supplies> getSupplies() {
        return this.supplies;
    }

    public void setSupplies(Set<Supplies> supplies) {
        if (this.supplies != null) {
            this.supplies.forEach(i -> i.setWarehouse(null));
        }
        if (supplies != null) {
            supplies.forEach(i -> i.setWarehouse(this));
        }
        this.supplies = supplies;
    }

    public Warehouse supplies(Set<Supplies> supplies) {
        this.setSupplies(supplies);
        return this;
    }

    public Warehouse addSupplies(Supplies supplies) {
        this.supplies.add(supplies);
        supplies.setWarehouse(this);
        return this;
    }

    public Warehouse removeSupplies(Supplies supplies) {
        this.supplies.remove(supplies);
        supplies.setWarehouse(null);
        return this;
    }

    public Set<ProtectionProduct> getProtectionProducts() {
        return this.protectionProducts;
    }

    public void setProtectionProducts(Set<ProtectionProduct> protectionProducts) {
        if (this.protectionProducts != null) {
            this.protectionProducts.forEach(i -> i.setWarehouse(null));
        }
        if (protectionProducts != null) {
            protectionProducts.forEach(i -> i.setWarehouse(this));
        }
        this.protectionProducts = protectionProducts;
    }

    public Warehouse protectionProducts(Set<ProtectionProduct> protectionProducts) {
        this.setProtectionProducts(protectionProducts);
        return this;
    }

    public Warehouse addProtectionProduct(ProtectionProduct protectionProduct) {
        this.protectionProducts.add(protectionProduct);
        protectionProduct.setWarehouse(this);
        return this;
    }

    public Warehouse removeProtectionProduct(ProtectionProduct protectionProduct) {
        this.protectionProducts.remove(protectionProduct);
        protectionProduct.setWarehouse(null);
        return this;
    }

    public Set<Fertilizers> getFertilizers() {
        return this.fertilizers;
    }

    public void setFertilizers(Set<Fertilizers> fertilizers) {
        if (this.fertilizers != null) {
            this.fertilizers.forEach(i -> i.setWarehouse(null));
        }
        if (fertilizers != null) {
            fertilizers.forEach(i -> i.setWarehouse(this));
        }
        this.fertilizers = fertilizers;
    }

    public Warehouse fertilizers(Set<Fertilizers> fertilizers) {
        this.setFertilizers(fertilizers);
        return this;
    }

    public Warehouse addFertilizers(Fertilizers fertilizers) {
        this.fertilizers.add(fertilizers);
        fertilizers.setWarehouse(this);
        return this;
    }

    public Warehouse removeFertilizers(Fertilizers fertilizers) {
        this.fertilizers.remove(fertilizers);
        fertilizers.setWarehouse(null);
        return this;
    }

    public Set<Crops> getCrops() {
        return this.crops;
    }

    public void setCrops(Set<Crops> crops) {
        if (this.crops != null) {
            this.crops.forEach(i -> i.setWarehouse(null));
        }
        if (crops != null) {
            crops.forEach(i -> i.setWarehouse(this));
        }
        this.crops = crops;
    }

    public Warehouse crops(Set<Crops> crops) {
        this.setCrops(crops);
        return this;
    }

    public Warehouse addCrops(Crops crops) {
        this.crops.add(crops);
        crops.setWarehouse(this);
        return this;
    }

    public Warehouse removeCrops(Crops crops) {
        this.crops.remove(crops);
        crops.setWarehouse(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Warehouse)) {
            return false;
        }
        return id != null && id.equals(((Warehouse) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Warehouse{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", area=" + getArea() +
            ", description='" + getDescription() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", enable=" + getEnable() +
            "}";
    }
}
