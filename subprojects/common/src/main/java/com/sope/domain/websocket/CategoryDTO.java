package com.sope.domain.websocket;

import java.text.DecimalFormat;

import com.sope.domain.category.CategoryType;

public class CategoryDTO {
    private CategoryType categoryType;
    private Long id;
    private String name;
    private String icon = "unknow";
    private long users;

    private String place;
    private String startTime;
    private String endTime;
    private String company;

    public String getParticipants() {

        final double participants = users / 1000d;
        if (participants >= 1) {
            return new DecimalFormat("###.#k").format(participants);
        }
        return String.valueOf(users);
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public String getShowAirTime() {
        return startTime + "-" + endTime;
    }

    public void setCategoryType(final CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
    }

    public Long getUsers() {
        return users;
    }

    public void setUsers(final Long users) {
        this.users = users;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(final String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(final String endTime) {
        this.endTime = endTime;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

}
