package me.dio.infrastructure.sql;

import me.dio.domain.Candidate;
import me.dio.domain.CandidateQuery;
import me.dio.domain.CandidateRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class JooqCandidateRepository implements CandidateRepository {

    @Override
    public void save(Set<Candidate> candidates) {

    }

    @Override
    public void delete(CandidateQuery query) {

    }

    @Override
    public List<Candidate> find(CandidateQuery query) {
        return null;
    }
}
