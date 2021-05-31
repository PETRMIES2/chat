package com.sope.domain.firebase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

// FIXME tämän on oltava synkassa android-formaatin kanssa
// Siirrä coreen
public class MessageToChat extends Message {

    private static final String SIMPLEDATEFORMAT_HH_MM = "HH:mm";
    private static final String SIMPLEDATEFORMAT_DATE = "dd.MM";
    private static final String MESSAGE_KEY = "message";
    private static final String AUTHOR_KEY = "author";
    private static final String SENDDATE_KEY = "senddate";
    private static final String CHAT_NUMBER = "chatNumber";
    private static final String HEADER = "header";

    private String message;
    private String author;
    private Date sendDate;
    private String header;

    // Kyseessä on keskustelun yksilöllinen numero
    private Long chatNumber;

    private MessageToChat() {
    }

    public MessageToChat(final String message, final String author, final Date sendDate, final Long chatNumber,
                         final String header) {
        this.message = message;
        this.author = author;
        this.sendDate = sendDate;
        this.chatNumber = chatNumber;
        this.header = header;
    }

    public MessageToChat(final Map<String, String> receivedMessage) {
        message = receivedMessage.get(MESSAGE_KEY);
        author = receivedMessage.get(AUTHOR_KEY);
        // sendDate = receivedMessage.get(SENDDATE_KEY);
        sendDate = new Date();
        chatNumber = Long.valueOf(receivedMessage.get(CHAT_NUMBER));
        header = receivedMessage.get(HEADER);
        setMessageType(MessageType.get(receivedMessage.get(MESSAGETYPE_TYPE)));
    }

    public String getMessage() {
        if (message != null) {
            if (message.length() > 2000) {
                return message.substring(0, 2000);
            } else {
                return message;
            }
        }
        return "- Don't hack!";
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getFormattedDate() {
        String time = new SimpleDateFormat(SIMPLEDATEFORMAT_HH_MM).format(sendDate);
        String date = new SimpleDateFormat(SIMPLEDATEFORMAT_DATE).format(sendDate);
        return String.format("%s | %s", date, time);
    }

    public Date getSendDate() {
        if (sendDate == null) {
            sendDate = new Date();
        }
        return sendDate;
    }

    @Override
    String getTarget() {
        return String.valueOf(chatNumber);
    }

    public String getHeader() {
        return header;
    }

    public Long getChatNumber() {
        return chatNumber;
    }
}
