package com.sope.domain.chat;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.sope.domain.IdEntity;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
public class ChatMessage extends IdEntity {

    private String message;
    private String username;
    @ManyToOne
    private Chat chat;
    private Date sendTime;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(final Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(final Chat chat) {
        this.chat = chat;
    }

}
