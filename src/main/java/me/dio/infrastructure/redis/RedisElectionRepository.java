package me.dio.infrastructure.redis;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.redis.datasource.sortedset.ScoreRange;
import io.quarkus.redis.datasource.sortedset.SortedSetCommands;
import me.dio.domain.Candidate;
import me.dio.domain.Election;
import me.dio.domain.ElectionRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class RedisElectionRepository implements ElectionRepository {
    public static final String KEY = "election:";
    private final PubSubCommands<String> pubsub;
    private final SortedSetCommands<String, String> commands;

    public RedisElectionRepository(RedisDataSource dataSource) {
        commands = dataSource.sortedSet(String.class, String.class);
        pubsub = dataSource.pubsub(String.class);
    }

    @Override
    public void submit(Election election) {
        Map<String, Double> rank = election.votes()
                                           .entrySet()
                                           .stream()
                                           .collect(Collectors.toMap(entry -> entry.getKey().id(),
                                                                     entry -> entry.getValue().doubleValue()));

        commands.zadd(KEY + election.id(), rank);
        pubsub.publish("elections", election.id());
    }

    @Override
    public List<Election> findAll() {
        return List.of();
    }

    @Override
    public Election sync(Election election) {
        var candidatesMap = commands.zrangebyscoreWithScores(KEY + election.id(),
                        ScoreRange.from(Integer.MIN_VALUE, Integer.MAX_VALUE))
                .stream().map(scoredValue -> {
                    Candidate candidate = election.votes()
                            .keySet()
                            .stream()
                            .filter(c -> c.id().equals(scoredValue.value()))
                            .findFirst()
                            .orElseThrow();

                    return Map.entry(candidate, (int) scoredValue.score());
                });

        return new Election(election.id(), Map.ofEntries(candidatesMap.toArray(Map.Entry[]::new)));
    }
}
