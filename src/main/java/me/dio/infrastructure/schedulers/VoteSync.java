package me.dio.infrastructure.schedulers;

import io.quarkus.scheduler.Scheduled;
import me.dio.domain.Election;
import me.dio.infrastructure.redis.RedisElectionRepository;
import me.dio.infrastructure.sql.JooqElectionRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VoteSync {
    private final JooqElectionRepository jooqRepository;
    private final RedisElectionRepository redisRepository;

    public VoteSync(JooqElectionRepository jooqRepository, RedisElectionRepository redisRepository) {
        this.jooqRepository = jooqRepository;
        this.redisRepository = redisRepository;
    }

    @Scheduled(cron = "{cron.expr}")
    void syncVotesWorker() {
        jooqRepository.findAll().forEach(election -> {
            Election updated = redisRepository.sync(election);
            jooqRepository.sync(updated);
        });
    }
}
