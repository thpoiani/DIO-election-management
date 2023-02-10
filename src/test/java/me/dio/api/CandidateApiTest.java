package me.dio.api;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import me.dio.api.dto.in.CreateCandidate;
import me.dio.api.dto.in.UpdateCandidate;
import me.dio.domain.Candidate;
import me.dio.domain.CandidateService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
class CandidateApiTest {
    @InjectMock
    CandidateService service;

    @Inject
    CandidateApi api;

    @Test
    void create_callsService() {
        CreateCandidate dto = Instancio.create(CreateCandidate.class);
        ArgumentCaptor<Candidate> captor = ArgumentCaptor.forClass(Candidate.class);

        api.create(dto)
           .await()
           .indefinitely();

        verify(service).save(captor.capture());
        verifyNoMoreInteractions(service);

        Candidate candidate = captor.getValue();

        assertEquals(candidate.photo(), dto.photo());
        assertEquals(candidate.givenName(), dto.givenName());
        assertEquals(candidate.familyName(), dto.familyName());
        assertEquals(candidate.email(), dto.email());
        assertEquals(candidate.phone(), dto.phone());
        assertEquals(candidate.jobTitle(), dto.jobTitle());
    }

    @Test
    void update_callsService() {
        String id = UUID.randomUUID().toString();
        UpdateCandidate dto = Instancio.create(UpdateCandidate.class);
        Candidate candidate = dto.toDomain(id);

        ArgumentCaptor<Candidate> captor = ArgumentCaptor.forClass(Candidate.class);

        when(service.findById(id)).thenReturn(candidate);

        me.dio.api.dto.out.Candidate response = api.update(id, dto)
                                                   .await()
                                                   .indefinitely();

        verify(service).save(captor.capture());
        verify(service).findById(id);
        verifyNoMoreInteractions(service);

        assertEquals(response, me.dio.api.dto.out.Candidate.fromDomain(candidate));
    }

    @Test
    void list_callsService() {
        List<Candidate> candidates = Instancio.stream(Candidate.class)
                                              .limit(10)
                                              .toList();

        when(service.findAll()).thenReturn(candidates);

        List<me.dio.api.dto.out.Candidate> response = api.list()
                                                         .await()
                                                        .indefinitely();

        verify(service).findAll();
        verifyNoMoreInteractions(service);

        assertEquals(response, candidates.stream().map(me.dio.api.dto.out.Candidate::fromDomain).toList());
    }

    @Test
    void get_callsService() {
        String id = UUID.randomUUID().toString();
        Candidate candidate = Instancio.create(Candidate.class);

        when(service.findById(id)).thenReturn(candidate);

        me.dio.api.dto.out.Candidate response = api.get(id)
                                                   .await()
                                                   .indefinitely();

        verify(service).findById(id);
        verifyNoMoreInteractions(service);

        assertEquals(response, me.dio.api.dto.out.Candidate.fromDomain(candidate));
    }

    @Test
    void delete_callsService() {
        String id = UUID.randomUUID().toString();

        api.delete(id)
           .await()
           .indefinitely();

        verify(service).delete(id);
        verifyNoMoreInteractions(service);
    }
}
