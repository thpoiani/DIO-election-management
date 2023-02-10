package me.dio.infrastructure.sql;

import me.dio.domain.Candidate;
import me.dio.domain.Election;
import me.dio.domain.ElectionRepository;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.ElectionCandidatesRecord;
import org.jooq.generated.tables.records.ElectionsRecord;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.stream.Stream;

import static org.jooq.generated.Tables.ELECTIONS;
import static org.jooq.generated.Tables.ELECTION_CANDIDATES;

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

    private ElectionsRecord serialize(Election election) {
        return new ElectionsRecord(election.id());
    }

    private ElectionCandidatesRecord serialize(Election election, Candidate candidate, int votes) {
        return new ElectionCandidatesRecord(election.id(), candidate.id(), votes);
    }
}
