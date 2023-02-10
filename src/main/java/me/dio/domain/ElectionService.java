package me.dio.domain;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;

@ApplicationScoped
public class ElectionService {
    private static final Logger LOGGER = Logger.getLogger(ElectionService.class);
    private final Instance<ElectionRepository> repositories;
    private final CandidateService service;

    public ElectionService(Instance<ElectionRepository> repositories, CandidateService service) {
        this.repositories = repositories;
        this.service = service;
    }

    public void submit() {
        Election election = Election.create(service.findAll());
        repositories.forEach(repository -> repository.submit(election));
        LOGGER.info("Election submitted: " + election.id());
    }
}
