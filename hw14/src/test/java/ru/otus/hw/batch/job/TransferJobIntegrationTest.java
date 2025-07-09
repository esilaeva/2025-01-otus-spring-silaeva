package ru.otus.hw.batch.job;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.hw.mongo.repositories.MongoAuthorRepository;
import ru.otus.hw.mongo.repositories.MongoBookRepository;
import ru.otus.hw.mongo.repositories.MongoGenreRepository;
import ru.otus.hw.postgres.model.Author;
import ru.otus.hw.postgres.model.Book;
import ru.otus.hw.postgres.model.Genre;
import ru.otus.hw.postgres.repositories.PostgresAuthorRepository;
import ru.otus.hw.postgres.repositories.PostgresBookRepository;
import ru.otus.hw.postgres.repositories.PostgresGenreRepository;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest // Provides utilities like JobLauncherTestUtils, JobRepositoryTestUtils, etc.
@Testcontainers  // Enables JUnit 5 support for Testcontainers
@ContextConfiguration(initializers = TransferJobIntegrationTest.Initializer.class)
@DisplayName("Data transfer job: ")
class TransferJobIntegrationTest {

    private static final long EXPECTED_AUTHORS_COUNT = 2L;
    private static final long EXPECTED_GENRES_COUNT = 3L;
    private static final long EXPECTED_BOOKS_COUNT = 2L;

    // This container will be started before tests and stopped after
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    // This container will be started before tests and stopped after
    // https://stackoverflow.com/questions/68456504/mongo-driver-with-testcontainers-throws-exceptions-after-tests-run-successfull/74274007#74274007
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:6");

    static {
        mongo.start();
    }

    // This inner class dynamically sets the database connection properties
    // for Spring before the context is created.
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    // Point the PRIMARY datasource to the test container
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "spring.datasource.driver-class-name=org.postgresql.Driver",

                    // Nullify the secondary datasource config from the main application.yml
                    // to avoid any potential conflicts or confusion during the test run.
                    "datasource-postgres.url=",

                    // Configure MongoDB from its test container
                    "spring.data.mongodb.uri=" + mongo.getReplicaSetUrl()
            );
        }
    }

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private PostgresAuthorRepository postgresAuthorRepository;

    @Autowired
    private PostgresGenreRepository postgresGenreRepository;

    @Autowired
    private PostgresBookRepository postgresBookRepository;

    @Autowired
    private MongoAuthorRepository mongoAuthorRepository;

    @Autowired
    private MongoGenreRepository mongoGenreRepository;

    @Autowired
    private MongoBookRepository mongoBookRepository;

    @BeforeEach
    void clearMetaData() {
        // Clean up batch and target DB before each test
        jobRepositoryTestUtils.removeJobExecutions();
        mongoAuthorRepository.deleteAll();
        mongoGenreRepository.deleteAll();
        mongoBookRepository.deleteAll();
    }

    @Test
    @DisplayName("should correctly transfer all data from PostgreSQL to MongoDB")
    void testDataMigrationJob() throws Exception {
        // given: Prepare source data in PostgreSQL
        prepareSourceData();
        assertThat(postgresBookRepository.count()).isEqualTo(EXPECTED_BOOKS_COUNT);

        // when: Check and launch the job
        assertThat(jobLauncherTestUtils.getJob())
                .isNotNull()
                .extracting(Job::getName)
                .isEqualTo("transferJob");

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParametersBuilder()
                .addLong("timestamp", Instant.now().getEpochSecond())
                .toJobParameters());

        // then: Verify the job exit code
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        // and: Verify transferred data in MongoDB
        assertThat(mongoAuthorRepository.count()).isEqualTo(EXPECTED_AUTHORS_COUNT);
        assertThat(mongoGenreRepository.count()).isEqualTo(EXPECTED_GENRES_COUNT);
        assertThat(mongoBookRepository.count()).isEqualTo(EXPECTED_BOOKS_COUNT);

        // and: Verify one of the migrated books in detail
        var migratedBookOptional = mongoBookRepository.findByTitle("The Great Gatsby");

        assertThat(migratedBookOptional)
                .as("Check that 'The Great Gatsby' was migrated correctly")
                .isPresent()
                .hasValueSatisfying(migratedBook -> {
                            assertThat(migratedBook.getMongoAuthor()
                                    .getFullName())
                                    .isEqualTo("F. Scott Fitzgerald");

                            assertThat(migratedBook.getMongoGenres())
                                    .hasSize(2)
                                    .extracting("name")
                                    .containsExactlyInAnyOrder("Novel", "Tragedy");
                        }
                );
    }

    private void prepareSourceData() {
        var author1 = postgresAuthorRepository.save(new Author(0, "F. Scott Fitzgerald"));
        var author2 = postgresAuthorRepository.save(new Author(0, "George Orwell"));

        var genre1 = postgresGenreRepository.save(new Genre(0, "Novel"));
        var genre2 = postgresGenreRepository.save(new Genre(0, "Tragedy"));
        var genre3 = postgresGenreRepository.save(new Genre(0, "Dystopian"));

        postgresBookRepository.save(new Book(0, "The Great Gatsby", author1, Set.of(genre1, genre2)));
        postgresBookRepository.save(new Book(0, "1984", author2, Set.of(genre3)));
    }
}