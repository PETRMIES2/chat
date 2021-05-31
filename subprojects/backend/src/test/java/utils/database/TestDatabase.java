package utils.database;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sope.configuration.MailConfiguration;
import com.sope.configuration.PropertiesConfiguration;
import com.sope.configuration.SpringSecurityConfiguration;
import com.sope.domain.SopeTransactionExecutor;
import com.sope.domain.SopeTransactionExecutor.TransactionConsumer;

import utils.EntityBuilderSessionFactoryConfig;
import utils.restmock.SopeApiTestConfig;

@WebAppConfiguration
@ContextConfiguration(classes = { SopeApiTestConfig.class, EntityBuilderSessionFactoryConfig.class })
@ComponentScan(basePackages = { "com.sope" })
@Import({ PropertiesConfiguration.class, SpringSecurityConfiguration.class, MailConfiguration.class })
@DirtiesContext
public abstract class TestDatabase {

    private static final Logger logger = LoggerFactory.getLogger(TestDatabase.class);

    @Inject
    private SopeTransactionExecutor transactionExecutor;

    @Inject
    private ApplicationContext context;

    @Inject
    private DataSource dataSource;

    protected TestRule clearSpringSecurityContext = new TestRule() {

        @Override
        public Statement apply(Statement base, Description description) {
            SecurityContextHolder.getContext().setAuthentication(null);
            return base;
        }
    };

    protected TestRule setContextForEntityBuilders = new TestRule() {
        @Override
        public Statement apply(Statement base, Description description) {
            SpringContextHolder.setContext(context);
            return base;
        }
    };

    @Rule
    public TestRule ruleChain = RuleChain.outerRule(clearSpringSecurityContext).around(setContextForEntityBuilders);

    @Before
    public void setUpDatabase() throws Exception {
        logger.info("***************************************");
        logger.info("Creating memory database " + dataSource);
        logger.info("***************************************");
        Flyway flyway = new Flyway();
        flyway.setLocations("filesystem:db/migration/schema");
        flyway.setDataSource(dataSource);
        flyway.setBaselineOnMigrate(true);
        flyway.migrate();
    }

    public SopeTransactionExecutor getTransactionExecutor() {
        return transactionExecutor;
    }

    protected void initializeTestData(final AbstractTestFixture testFixture) {
        transactionExecutor.write(new TransactionConsumer() {

            @Override
            public void consumeTransaction() {
                testFixture.initializeIfNotYetInitialized();
            }
        });
    }

    protected void clearTestData(final AbstractTestFixture testFixture) {
        transactionExecutor.write(new TransactionConsumer() {

            @Override
            public void consumeTransaction() {
                testFixture.reset();
            }
        });
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}
