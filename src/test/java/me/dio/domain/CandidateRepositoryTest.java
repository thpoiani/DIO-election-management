package me.dio.domain;

import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class CandidateRepositoryTest {
    public abstract CandidateRepository repository();

    @Test
    public void save_saves() {
        Candidate candidate = Instancio.create(Candidate.class);

        repository().save(candidate);

        Optional<Candidate> result = repository().findById(candidate.id());

        assertTrue(result.isPresent());
        assertEquals(result.get(), candidate);
    }

    @Test
    public void find_findsByFamilyName() {
        Candidate candidate1 = Instancio.create(Candidate.class);
        Candidate candidate2 = Instancio.of(Candidate.class).set(field("familyName"), "TestString").create();

        repository().save(Set.of(candidate1, candidate2));

        CandidateQuery query = new CandidateQuery.Builder().familyName("Test").build();

        List<Candidate> results = repository().find(query);

        assertEquals(1, results.size());
        assertEquals(candidate2, results.get(0));
    }

    @Test
    public void delete_deletes() {
        Candidate candidate = Instancio.create(Candidate.class);

        repository().save(candidate);

        CandidateQuery query = new CandidateQuery.Builder().candidateIds(Set.of(candidate.id())).build();

        repository().delete(query);

        List<Candidate> results = repository().find(query);

        assertTrue(results.isEmpty());
    }
}
