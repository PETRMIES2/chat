package com.sope.domain.chat;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;

import com.sope.domain.CommonTableProperties;
import com.sope.domain.category.Category;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
public class Chat extends CommonTableProperties {

    @ManyToOne
    private Category category;

    @Column(nullable = false)
    @ColumnDefault("''")
    private String header;

    private Long chatNumber;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int userCount = 0;

    private Date lastMessageSent;

    public String getHeader() {
        return header;
    }

    public void setHeader(final String header) {
        this.header = header;
    }

    public Date getLastMessageSent() {
        return lastMessageSent;
    }

    public void setLastMessageSent(final Date lastMessageSent) {
        this.lastMessageSent = lastMessageSent;
    }



    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(final int userCount) {
        this.userCount = userCount;
    }

    public void addUser() {
        userCount++;
    }

    public void removeUser() {
        userCount--;
        if (userCount < 0) {
            userCount = 0;
        }
    }

    public boolean emptyChat() {
        return userCount <= 0;
    }

    public Long getChatNumber() {
        return chatNumber;
    }

    public void setChatNumber(final Long chatNumber) {
        this.chatNumber = chatNumber;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }


}
