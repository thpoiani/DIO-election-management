package me.dio.infrastructure.sql;

import me.dio.domain.Candidate;
import me.dio.domain.Election;
import me.dio.domain.ElectionRepository;
import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.tables.records.ElectionCandidatesRecord;
import org.jooq.generated.tables.records.ElectionsRecord;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.jooq.generated.Tables.*;

@ApplicationScoped
public class JooqElectionRepository implements ElectionRepository {
    private final DSLContext context;

    public JooqElectionRepository(DSLContext context) {
        this.context = context;
    }
    @Override
    @Transactional
    public void submit(Election election) {
        var queries = Stream.concat(
                Stream.of(context.insertInto(ELECTIONS).set(serialize(election))),
                election.votes()
                        .entrySet()
                        .stream()
                        .map(entry -> serialize(election, entry.getKey(), entry.getValue()))
                        .map(r -> context.insertInto(ELECTION_CANDIDATES).set(r)))
                .toList();

        context.batch(queries).execute();
    }

    @Override
    public List<Election> findAll() {
        var step = context.select()
                          .from(ELECTIONS)
                          .join(ELECTION_CANDIDATES).on(ELECTION_CANDIDATES.ELECTION_ID.eq(ELECTIONS.ELECTION_ID))
                          .join(CANDIDATES).on(ELECTION_CANDIDATES.CANDIDATE_ID.eq(CANDIDATES.CANDIDATE_ID));

        Map<String, Result<Record>> map = step.fetchGroups(ELECTIONS.ELECTION_ID);

        return map.entrySet().stream().map(entry -> {
            var candidatesMap = entry.getValue().stream().map(r -> {
                Candidate candidate = new Candidate(r.get(CANDIDATES.CANDIDATE_ID),
                        Optional.ofNullable(r.get(CANDIDATES.PHOTO)),
                        r.get(CANDIDATES.GIVEN_NAME),
                        r.get(CANDIDATES.FAMILY_NAME),
                        r.get(CANDIDATES.EMAIL),
                        Optional.ofNullable(r.get(CANDIDATES.PHONE)),
                        Optional.ofNullable(r.get(CANDIDATES.JOB_TITLE)));

                return Map.entry(candidate, r.get(ELECTION_CANDIDATES.VOTES));
            });

            return new Election(entry.getKey(), Map.ofEntries(candidatesMap.toArray(Map.Entry[]::new)));
        }).toList();
    }

    @Override
    public Election sync(Election election) {
        var queries = election.votes()
                              .entrySet()
                              .stream()
                              .map(entry -> serialize(election, entry.getKey(), entry.getValue()))
                              .map(r -> context.insertInto(ELECTION_CANDIDATES)
                                               .set(r)
                                               .onDuplicateKeyUpdate()
                                               .set(r))
                              .toList();

        context.batch(queries).execute();

        return election;
    }

    private ElectionsRecord serialize(Election election) {
        return new ElectionsRecord(election.id());
    }

    private ElectionCandidatesRecord serialize(Election election, Candidate candidate, int votes) {
        return new ElectionCandidatesRecord(election.id(), candidate.id(), votes);
    }
}
