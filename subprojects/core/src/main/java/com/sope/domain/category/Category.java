package com.sope.domain.category;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;

import com.sope.domain.CommonTableProperties;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
public class Category extends CommonTableProperties {

    @Column(nullable = false)
    @ColumnDefault("''")
    private String name = "";

    private Date startTime;
    private Date endTime;
    private Date validFrom;
    private Date validTo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType type;

    @Column(nullable = false)
    @ColumnDefault("''")
    private String iconName = "";

    @Column(nullable = false)
    @ColumnDefault("''")
    private String place = "";

    @Column(nullable = false)
    @ColumnDefault("''")
    private String timezone;

    private int ordinal;

    @Column(nullable = false)
    @ColumnDefault("''")
    private String company = "";

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(final String iconName) {
        this.iconName = iconName;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(final int ordinal) {
        this.ordinal = ordinal;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(final Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(final Date validTo) {
        this.validTo = validTo;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(final CategoryType type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }


}
