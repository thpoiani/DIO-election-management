package me.dio.infrastructure.sql;

import io.quarkus.test.junit.QuarkusTest;
import me.dio.domain.CandidateRepository;
import me.dio.domain.CandidateRepositoryTest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;

import static org.jooq.generated.tables.Candidates.CANDIDATES;

@QuarkusTest
public class JooqCandidateRepositoryTest extends CandidateRepositoryTest {
    @Inject
    DSLContext context;

    @Inject
    JooqCandidateRepository repository;

    @Override
    public CandidateRepository repository() {
        return repository;
    }

    @BeforeEach
    void flush() {
        try (var step = context.deleteFrom(CANDIDATES)) {
            step.execute();
        }
    }
}