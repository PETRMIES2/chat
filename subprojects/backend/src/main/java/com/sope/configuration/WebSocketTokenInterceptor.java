package com.sope.configuration;


import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class WebSocketTokenInterceptor extends ChannelInterceptorAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketTokenInterceptor.class);
    private final static String TOKEN_HEADER = "token";
	
	@Inject
	private PreAuthUserDetailsService validator;
	
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		handleEvent(message);
		return super.preSend(message, channel);
	}

	private void handleEvent(Message<?> message) {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
 
        // ignore non-STOMP messages like heartbeat messages
        if(sha.getCommand() == null) {
            return;
        }
 
        String sessionId = sha.getSessionId();
        
        switch(sha.getCommand()) {
            case CONNECT:
            	verifyToken(sha);
            	LOGGER.debug("STOMP Connect [sessionId: " + sessionId + "]");
                break;
            case CONNECTED:
                LOGGER.debug("STOMP Connected [sessionId: " + sessionId + "]");
                break;
            case DISCONNECT:
                LOGGER.debug("STOMP Disconnect [sessionId: " + sessionId + "]");
                break;
            default:
                break; 
        }
	}
	    
	private void verifyToken(StompHeaderAccessor sha) {
		String token = String.join("", sha.getNativeHeader(TOKEN_HEADER)
		        .stream()
		        .map(Object::toString)
		        .collect(Collectors.toList()));
		LOGGER.debug("Verifying token for socket: "  + token);
		Authentication auth = new PreAuthenticatedAuthenticationToken(token, token);
		
		validator.loadUserDetails(auth);
	}
	
}
