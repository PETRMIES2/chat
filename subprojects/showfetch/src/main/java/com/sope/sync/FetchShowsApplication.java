package com.sope.sync;

import javax.inject.Inject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import com.sope.domain.SopeTransactionExecutor;
import com.sope.domain.SopeTransactionExecutor.TransactionConsumer;
import com.sope.sync.service.fi.elisa.ElisaApi;
import com.sope.sync.service.fi.mtv3.Mtv3TvGuide;

@SpringBootApplication
@EnableAutoConfiguration(exclude={FlywayAutoConfiguration.class})
@ComponentScan(basePackages = { "com.sope" })
public class FetchShowsApplication implements CommandLineRunner {

    private final Mtv3TvGuide mtv3TvOpas;
    private final ElisaApi elisaApi;
    private final SopeTransactionExecutor transactionExecutor;
    
    @Inject
    public FetchShowsApplication(Mtv3TvGuide mtv3TvOpas, ElisaApi elisaApi, SopeTransactionExecutor transactionExecutor) {
        this.mtv3TvOpas = mtv3TvOpas;
        this.elisaApi = elisaApi;
        this.transactionExecutor = transactionExecutor;
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(FetchShowsApplication.class).web(false).application();
        application.run(args);

    }

    @Override
    public void run(String... args) throws Exception {
        transactionExecutor.write(new TransactionConsumer() {

            @Override
            public void consumeTransaction() {
//                mtv3TvOpas.saveShows();
            }

        });
        transactionExecutor.write(new TransactionConsumer() {

            @Override
            public void consumeTransaction() {
                elisaApi.saveShows();
            }

        });

    }
}
