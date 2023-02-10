package me.dio.infrastructure.sql;

import me.dio.domain.Candidate;
import me.dio.domain.CandidateQuery;
import me.dio.domain.CandidateRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.CandidatesRecord;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.jooq.generated.Tables.CANDIDATES;

@ApplicationScoped
public class JooqCandidateRepository implements CandidateRepository {

    private final DSLContext context;

    public JooqCandidateRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    @Transactional
    public void save(Set<Candidate> candidates) {
        var queries = candidates.stream()
                                .map(this::serialize)
                                .map(r -> context.insertInto(CANDIDATES)
                                                 .set(r)
                                                 .onDuplicateKeyUpdate()
                                                 .set(r))
                                .toList();

        context.batch(queries).execute();
    }

    @Override
    public void delete(CandidateQuery query) {
        try (var step = context.deleteFrom(CANDIDATES)) {
            step.where(conditions(query))
                .execute();
        }
    }

    @Override
    public List<Candidate> find(CandidateQuery query) {
        try (var step = context.selectFrom(CANDIDATES)) {
            return step.where(conditions(query))
                       .fetch()
                       .map(this::deserialize);
        }
    }

    private CandidatesRecord serialize(Candidate candidate) {
        CandidatesRecord candidatesRecord = new CandidatesRecord();

        candidatesRecord.setCandidateId(candidate.id());
        candidate.photo().ifPresent(candidatesRecord::setPhoto);
        candidatesRecord.setGivenName(candidate.givenName());
        candidatesRecord.setFamilyName(candidate.familyName());
        candidatesRecord.setEmail(candidate.email());
        candidate.phone().ifPresent(candidatesRecord::setPhone);
        candidate.jobTitle().ifPresent(candidatesRecord::setJobTitle);

        return candidatesRecord;
    }

    private Candidate deserialize(CandidatesRecord candidatesRecord) {
        return new Candidate(candidatesRecord.getCandidateId(),
                             Optional.ofNullable(candidatesRecord.getPhoto()),
                             candidatesRecord.getGivenName(),
                             candidatesRecord.getFamilyName(),
                             candidatesRecord.getEmail(),
                             Optional.ofNullable(candidatesRecord.getPhone()),
                             Optional.ofNullable(candidatesRecord.getJobTitle()));
    }

    private List<? extends Condition> conditions(CandidateQuery query) {
        return Stream.of(query.candidateIds().map(CANDIDATES.CANDIDATE_ID::in),
                         query.familyName().map(familyName -> CANDIDATES.FAMILY_NAME.like(familyName + "%")))
                     .flatMap(Optional::stream)
                     .toList();
    }
}
