package me.dio.api;

import io.smallrye.mutiny.Uni;
import me.dio.api.dto.in.CreateCandidate;
import me.dio.api.dto.in.UpdateCandidate;
import me.dio.api.dto.out.Candidate;
import me.dio.domain.CandidateService;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CandidateApi {
    private final CandidateService service;

    public CandidateApi(CandidateService service) {
        this.service = service;
    }

    public Uni<Void> create(CreateCandidate dto) {
        return Uni.createFrom()
                  .item(() -> {
                      service.save(dto.toDomain());
                      return null;
                  });
    }

    public Uni<Candidate> update(String id, UpdateCandidate dto) {
        return Uni.createFrom()
                .item(() -> {
                    service.save(dto.toDomain(id));
                    return service.findById(id);
                })
                .onItem()
                .transform(Candidate::fromDomain);
    }

    public Uni<List<Candidate>> list() {
        return Uni.createFrom()
                  .item(service::findAll)
                  .onItem()
                  .transform(candidates -> candidates.stream()
                                                     .map(Candidate::fromDomain)
                                                     .toList());
    }

    public Uni<Candidate> get(String id) {
        return Uni.createFrom()
                  .item(() -> service.findById(id))
                  .onItem()
                  .transform(Candidate::fromDomain);
    }

    public Uni<Void> delete(String id) {
        return Uni.createFrom()
                  .item(() -> {
                      service.delete(id);
                      return null;
                  });
    }
}
