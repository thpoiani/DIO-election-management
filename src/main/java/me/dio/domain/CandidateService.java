package me.dio.domain;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class CandidateService {
    private final CandidateRepository repository;

    public CandidateService(CandidateRepository repository) {
        this.repository = repository;
    }

    public void save(Candidate candidate) {
        repository.save(candidate);
    }

    public List<Candidate> findAll() {
        return repository.find(new CandidateQuery.Builder().build());
    }

    public Candidate findById(String id) {
        return repository.findById(id).orElseThrow();
    }

    public void delete(String id) {
        var query = new CandidateQuery.Builder().candidateIds(Set.of(id)).build();
        repository.delete(query);
    }
}
