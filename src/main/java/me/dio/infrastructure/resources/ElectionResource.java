package me.dio.infrastructure.resources;

import io.smallrye.mutiny.Uni;
import me.dio.api.ElectionApi;
import me.dio.api.dto.out.Election;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestResponse;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/elections")
public class ElectionResource {
    private final ElectionApi api;

    public ElectionResource(ElectionApi api) {
        this.api = api;
    }

    @POST
    @ResponseStatus(RestResponse.StatusCode.CREATED)
    @Transactional
    public Uni<Void> submit() {
        return api.submit();
    }

    @GET
    public Uni<List<Election>> list() {
        return api.list();
    }
}
