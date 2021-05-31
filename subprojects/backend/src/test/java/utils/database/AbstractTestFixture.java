package utils.database;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTestFixture implements DatabaseInitializer {

    private boolean initialized;

    private List<AbstractTestFixture> requiredTestFixtures = new ArrayList<>();

    public void require(AbstractTestFixture requiredTestFixture) {
        requiredTestFixture.initializeIfNotYetInitialized();
        requiredTestFixtures.add(requiredTestFixture);
    }

    public void initializeIfNotYetInitialized() {
        if (!initialized) {
            initialize();
            initialized = true;
        }
    }

    public void reset() {
        if (initialized) {
            this.initialized = false;
            for (AbstractTestFixture abstractTestFixture : requiredTestFixtures) {
                abstractTestFixture.reset();
            }
            requiredTestFixtures.clear();
        }
    }
}
