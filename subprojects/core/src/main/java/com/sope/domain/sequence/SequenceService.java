package com.sope.domain.sequence;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;
import org.springframework.stereotype.Service;

@Service
public class SequenceService {

    private static final String CHAT_SEQUENCE = "ChatSequence";
    
    private DataSource dataSource;

    @Inject
    public SequenceService(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public Long getNextChatNumber() {
        MySQLMaxValueIncrementer incrementer = new MySQLMaxValueIncrementer(dataSource, CHAT_SEQUENCE, "value");
        incrementer.setPaddingLength(6);
        incrementer.afterPropertiesSet();
        
        return incrementer.nextLongValue();
 
    }
    
    
}
