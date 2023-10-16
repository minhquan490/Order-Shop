package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.FullTextField;

@MappedSuperclass
public abstract class VnAddress<T> extends AbstractEntity<T> {

    @Column(name = "NAME", columnDefinition = "nvarchar(100)")
    @FullTextField
    @ActiveReflection
    private String name;

    @Column(name = "CODE")
    private Integer code;

    @Column(name = "DIVISION_TYPE", columnDefinition = "nvarchar(100)")
    private String divisionType;

    @Column(name = "CODE_NAME", length = 50)
    @FullTextField
    @ActiveReflection
    private String codeName;

    @ActiveReflection
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name, name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setCode(Integer code) {
        if (this.code != null && !this.code.equals(code)) {
            trackUpdatedField("CODE", this.code, code);
        }
        this.code = code;
    }

    @ActiveReflection
    public void setCodeName(String codeName) {
        if (this.codeName != null && !this.codeName.equals(codeName)) {
            trackUpdatedField("CODE_NAME", this.codeName, codeName);
        }
        this.codeName = codeName;
    }

    @ActiveReflection
    public void setDivisionType(String divisionType) {
        if (this.divisionType != null && !this.divisionType.equals(divisionType)) {
            trackUpdatedField("DIVISION_TYPE", this.divisionType, divisionType);
        }
        this.divisionType = divisionType;
    }

    public String getName() {
        return name;
    }

    public String getCodeName() {
        return codeName;
    }

    public Integer getCode() {
        return code;
    }

    public String getDivisionType() {
        return divisionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VnAddress<?> vnAddress = (VnAddress<?>) o;
        return Objects.equal(getName(), vnAddress.getName()) && Objects.equal(getCode(), vnAddress.getCode()) && Objects.equal(getDivisionType(), vnAddress.getDivisionType()) && Objects.equal(getCodeName(), vnAddress.getCodeName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getName(), getCode(), getDivisionType(), getCodeName());
    }
}
