package utils.restmock;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.sope.configuration.WebApplicationConfiguration;
import com.sope.configuration.WebSecurityConfiguration;

@Configuration
@Import({ WebApplicationConfiguration.class, WebSecurityConfiguration.class })
@ComponentScan(basePackages = { "utils.restmock", "utils.fixture" })
public class SopeApiTestConfig {

}
