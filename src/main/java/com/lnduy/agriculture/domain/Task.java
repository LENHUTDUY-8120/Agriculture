package com.lnduy.agriculture.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * Task entity.\n@author The JHipster team.
 */
@Entity
@Table(name = "task")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private ZonedDateTime startDate;

    @Column(name = "end_date")
    private ZonedDateTime endDate;

    @ManyToOne
    @JsonIgnoreProperties(value = { "transactions", "crop", "field" }, allowSetters = true)
    private Season season;

    @ManyToMany
    @JoinTable(
        name = "rel_task__employee",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    @JsonIgnoreProperties(value = { "tasks" }, allowSetters = true)
    private Set<Employee> employees = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_task__supplies",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "supplies_id")
    )
    @JsonIgnoreProperties(value = { "tasks" }, allowSetters = true)
    private Set<Supplies> supplies = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_task__protectionproduct",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "protectionproduct_id")
    )
    @JsonIgnoreProperties(value = { "tasks" }, allowSetters = true)
    private Set<ProtectionProduct> protectionproducts = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_task__fertilizers",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "fertilizers_id")
    )
    @JsonIgnoreProperties(value = { "tasks" }, allowSetters = true)
    private Set<Fertilizers> fertilizers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Task id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Task title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Task description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getStartDate() {
        return this.startDate;
    }

    public Task startDate(ZonedDateTime startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return this.endDate;
    }

    public Task endDate(ZonedDateTime endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Season getSeason() {
        return this.season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Task season(Season season) {
        this.setSeason(season);
        return this;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public Task employees(Set<Employee> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Task addEmployee(Employee employee) {
        this.employees.add(employee);
        employee.getTasks().add(this);
        return this;
    }

    public Task removeEmployee(Employee employee) {
        this.employees.remove(employee);
        employee.getTasks().remove(this);
        return this;
    }

    public Set<Supplies> getSupplies() {
        return this.supplies;
    }

    public void setSupplies(Set<Supplies> supplies) {
        this.supplies = supplies;
    }

    public Task supplies(Set<Supplies> supplies) {
        this.setSupplies(supplies);
        return this;
    }

    public Task addSupplies(Supplies supplies) {
        this.supplies.add(supplies);
        supplies.getTasks().add(this);
        return this;
    }

    public Task removeSupplies(Supplies supplies) {
        this.supplies.remove(supplies);
        supplies.getTasks().remove(this);
        return this;
    }

    public Set<ProtectionProduct> getProtectionproducts() {
        return this.protectionproducts;
    }

    public void setProtectionproducts(Set<ProtectionProduct> protectionProducts) {
        this.protectionproducts = protectionProducts;
    }

    public Task protectionproducts(Set<ProtectionProduct> protectionProducts) {
        this.setProtectionproducts(protectionProducts);
        return this;
    }

    public Task addProtectionproduct(ProtectionProduct protectionProduct) {
        this.protectionproducts.add(protectionProduct);
        protectionProduct.getTasks().add(this);
        return this;
    }

    public Task removeProtectionproduct(ProtectionProduct protectionProduct) {
        this.protectionproducts.remove(protectionProduct);
        protectionProduct.getTasks().remove(this);
        return this;
    }

    public Set<Fertilizers> getFertilizers() {
        return this.fertilizers;
    }

    public void setFertilizers(Set<Fertilizers> fertilizers) {
        this.fertilizers = fertilizers;
    }

    public Task fertilizers(Set<Fertilizers> fertilizers) {
        this.setFertilizers(fertilizers);
        return this;
    }

    public Task addFertilizers(Fertilizers fertilizers) {
        this.fertilizers.add(fertilizers);
        fertilizers.getTasks().add(this);
        return this;
    }

    public Task removeFertilizers(Fertilizers fertilizers) {
        this.fertilizers.remove(fertilizers);
        fertilizers.getTasks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return id != null && id.equals(((Task) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
