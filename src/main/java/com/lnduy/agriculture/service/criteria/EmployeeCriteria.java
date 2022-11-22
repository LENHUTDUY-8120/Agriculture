package com.lnduy.agriculture.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lnduy.agriculture.domain.Employee} entity. This class is used
 * in {@link com.lnduy.agriculture.web.rest.EmployeeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /employees?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmployeeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter firstName;

    private StringFilter lastName;

    private StringFilter email;

    private StringFilter phoneNumber;

    private InstantFilter hireDate;

    private LongFilter salary;

    private LongFilter commissionPct;

    private IntegerFilter enable;

    private LongFilter taskId;

    private Boolean distinct;

    public EmployeeCriteria() {}

    public EmployeeCriteria(EmployeeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.firstName = other.firstName == null ? null : other.firstName.copy();
        this.lastName = other.lastName == null ? null : other.lastName.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.phoneNumber = other.phoneNumber == null ? null : other.phoneNumber.copy();
        this.hireDate = other.hireDate == null ? null : other.hireDate.copy();
        this.salary = other.salary == null ? null : other.salary.copy();
        this.commissionPct = other.commissionPct == null ? null : other.commissionPct.copy();
        this.enable = other.enable == null ? null : other.enable.copy();
        this.taskId = other.taskId == null ? null : other.taskId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EmployeeCriteria copy() {
        return new EmployeeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public StringFilter firstName() {
        if (firstName == null) {
            firstName = new StringFilter();
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
    }

    public StringFilter getLastName() {
        return lastName;
    }

    public StringFilter lastName() {
        if (lastName == null) {
            lastName = new StringFilter();
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = new StringFilter();
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public InstantFilter getHireDate() {
        return hireDate;
    }

    public InstantFilter hireDate() {
        if (hireDate == null) {
            hireDate = new InstantFilter();
        }
        return hireDate;
    }

    public void setHireDate(InstantFilter hireDate) {
        this.hireDate = hireDate;
    }

    public LongFilter getSalary() {
        return salary;
    }

    public LongFilter salary() {
        if (salary == null) {
            salary = new LongFilter();
        }
        return salary;
    }

    public void setSalary(LongFilter salary) {
        this.salary = salary;
    }

    public LongFilter getCommissionPct() {
        return commissionPct;
    }

    public LongFilter commissionPct() {
        if (commissionPct == null) {
            commissionPct = new LongFilter();
        }
        return commissionPct;
    }

    public void setCommissionPct(LongFilter commissionPct) {
        this.commissionPct = commissionPct;
    }

    public IntegerFilter getEnable() {
        return enable;
    }

    public IntegerFilter enable() {
        if (enable == null) {
            enable = new IntegerFilter();
        }
        return enable;
    }

    public void setEnable(IntegerFilter enable) {
        this.enable = enable;
    }

    public LongFilter getTaskId() {
        return taskId;
    }

    public LongFilter taskId() {
        if (taskId == null) {
            taskId = new LongFilter();
        }
        return taskId;
    }

    public void setTaskId(LongFilter taskId) {
        this.taskId = taskId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeeCriteria that = (EmployeeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(hireDate, that.hireDate) &&
            Objects.equals(salary, that.salary) &&
            Objects.equals(commissionPct, that.commissionPct) &&
            Objects.equals(enable, that.enable) &&
            Objects.equals(taskId, that.taskId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, phoneNumber, hireDate, salary, commissionPct, enable, taskId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (firstName != null ? "firstName=" + firstName + ", " : "") +
            (lastName != null ? "lastName=" + lastName + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
            (hireDate != null ? "hireDate=" + hireDate + ", " : "") +
            (salary != null ? "salary=" + salary + ", " : "") +
            (commissionPct != null ? "commissionPct=" + commissionPct + ", " : "") +
            (enable != null ? "enable=" + enable + ", " : "") +
            (taskId != null ? "taskId=" + taskId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
