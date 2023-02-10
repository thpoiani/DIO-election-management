package me.dio.api;

import io.smallrye.mutiny.Uni;
import me.dio.api.dto.out.Election;
import me.dio.domain.ElectionService;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ElectionApi {
    ElectionService service;

    public ElectionApi(ElectionService service) {
        this.service = service;
    }

    public Uni<Void> submit() {
        return Uni.createFrom()
                .item(() -> {
                    service.submit();
                    return null;
                });
    }

    public Uni<List<Election>> list() {
        return Uni.createFrom()
                .item(service::findAll)
                .onItem()
                .transform(elections -> elections.stream()
                        .map(Election::fromDomain)
                        .toList());
    }
}
