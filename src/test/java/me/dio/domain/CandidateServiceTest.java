package me.dio.domain;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@QuarkusTest
class CandidateServiceTest {
    @InjectMock
    CandidateRepository repository;

    @Inject
    CandidateService service;

    @Test
    void save_callsRepository() {
        Candidate candidate = Instancio.create(Candidate.class);

        service.save(candidate);

        verify(repository).save(candidate);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll_callsRepository() {
        List<Candidate> candidates = Instancio.stream(Candidate.class).limit(10).toList();
        CandidateQuery query = new CandidateQuery.Builder().build();

        when(repository.find(query)).thenReturn(candidates);

        List<Candidate> result = service.findAll();

        verify(repository).find(query);
        verifyNoMoreInteractions(repository);

        assertEquals(result, candidates);
    }

    @Test
    void findById_whenCandidateIsFound_returnsCandidate() {
        Candidate candidate = Instancio.create(Candidate.class);

        when(repository.findById(candidate.id())).thenReturn(Optional.of(candidate));

        Candidate result = service.findById(candidate.id());

        verify(repository).findById(candidate.id());
        verifyNoMoreInteractions(repository);

        assertEquals(result, candidate);
    }

    @Test
    void findById_whenCandidateIsNotFound_throwsException() {
        Candidate candidate = Instancio.create(Candidate.class);

        when(repository.findById(candidate.id())).thenReturn(Optional.empty());

        assertThrows(Throwable.class, () -> service.findById(candidate.id()));

        verify(repository).findById(candidate.id());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void delete_callsRepository() {
        Candidate candidate = Instancio.create(Candidate.class);
        CandidateQuery query = new CandidateQuery.Builder().candidateIds(Optional.of(Set.of(candidate.id()))).build();

        service.delete(candidate.id());

        verify(repository).delete(query);
        verifyNoMoreInteractions(repository);
    }
}
