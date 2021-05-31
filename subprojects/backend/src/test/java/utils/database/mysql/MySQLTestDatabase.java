package utils.database.mysql;

import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

import utils.database.CleanDatabase;
import utils.database.TestDatabase;

@ContextConfiguration(classes = { MySQLDatabaseConfiguration.class })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ComponentScan({ "utils" })
public class MySQLTestDatabase extends TestDatabase {

    TestRule cleanDatabase = new TestRule() {

        @Override
        public Statement apply(Statement base, Description description) {
            CleanDatabase cleaner = new CleanDatabase();
            cleaner.clearDatabase(getDataSource());
            return base;
        }
    };

    @Rule
    public TestRule ruleChain = RuleChain.outerRule(clearSpringSecurityContext).around(cleanDatabase).around(setContextForEntityBuilders);

}
