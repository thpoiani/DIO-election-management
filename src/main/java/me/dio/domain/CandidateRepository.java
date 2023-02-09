package me.dio.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CandidateRepository {
    void save(Set<Candidate> candidates);
    void delete(CandidateQuery query);
    List<Candidate> find(CandidateQuery query);

    default void save(Candidate candidate) {
        save(Set.of(candidate));
    }

    default Optional<Candidate> findById(String candidateId) {
        return find(new CandidateQuery.Builder().build()).stream().findFirst();
    }
}
