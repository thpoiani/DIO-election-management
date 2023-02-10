package me.dio.api;

import io.smallrye.mutiny.Uni;
import me.dio.domain.ElectionService;

import javax.enterprise.context.ApplicationScoped;

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
}
