package com.sope.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.ColumnDefault;

@MappedSuperclass
public class CommonTableProperties extends IdEntity {

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean active = false;

	private Date created;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
