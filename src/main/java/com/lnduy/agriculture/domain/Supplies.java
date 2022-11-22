package com.lnduy.agriculture.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Supplies.
 */
@Entity
@Table(name = "supplies")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Supplies implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "property")
    private String property;

    @Column(name = "type")
    private String type;

    @Column(name = "volume")
    private Float volume;

    @Column(name = "unit")
    private String unit;

    @Column(name = "enable")
    private Integer enable;

    @ManyToOne
    @JsonIgnoreProperties(value = { "supplies", "protectionProducts", "fertilizers", "crops" }, allowSetters = true)
    private Warehouse warehouse;

    @ManyToMany(mappedBy = "supplies")
    @JsonIgnoreProperties(value = { "season", "employees", "supplies", "protectionproducts", "fertilizers" }, allowSetters = true)
    private Set<Task> tasks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Supplies id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Supplies name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty() {
        return this.property;
    }

    public Supplies property(String property) {
        this.setProperty(property);
        return this;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getType() {
        return this.type;
    }

    public Supplies type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getVolume() {
        return this.volume;
    }

    public Supplies volume(Float volume) {
        this.setVolume(volume);
        return this;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public String getUnit() {
        return this.unit;
    }

    public Supplies unit(String unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getEnable() {
        return this.enable;
    }

    public Supplies enable(Integer enable) {
        this.setEnable(enable);
        return this;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Warehouse getWarehouse() {
        return this.warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Supplies warehouse(Warehouse warehouse) {
        this.setWarehouse(warehouse);
        return this;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<Task> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.removeSupplies(this));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.addSupplies(this));
        }
        this.tasks = tasks;
    }

    public Supplies tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public Supplies addTask(Task task) {
        this.tasks.add(task);
        task.getSupplies().add(this);
        return this;
    }

    public Supplies removeTask(Task task) {
        this.tasks.remove(task);
        task.getSupplies().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Supplies)) {
            return false;
        }
        return id != null && id.equals(((Supplies) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Supplies{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", property='" + getProperty() + "'" +
            ", type='" + getType() + "'" +
            ", volume=" + getVolume() +
            ", unit='" + getUnit() + "'" +
            ", enable=" + getEnable() +
            "}";
    }
}
