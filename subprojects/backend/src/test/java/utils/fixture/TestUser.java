package utils.fixture;

import org.springframework.stereotype.Component;

@Component
public class TestUser {

    private String authToken = "AUTHTOKEN";

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    
}
