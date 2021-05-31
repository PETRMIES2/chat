package utils.database.memory;

import org.springframework.test.context.ContextConfiguration;

import utils.database.TestDatabase;

@ContextConfiguration(classes = { InMemoryDatabaseConfiguration.class })
public class InMemoryTestDatabase extends TestDatabase {

}
