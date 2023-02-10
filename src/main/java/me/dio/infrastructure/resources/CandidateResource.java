package me.dio.infrastructure.resources;

import io.smallrye.mutiny.Uni;
import me.dio.api.CandidateApi;
import me.dio.api.dto.in.CreateCandidate;
import me.dio.api.dto.in.UpdateCandidate;
import me.dio.api.dto.out.Candidate;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestResponse;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/candidates")
public class CandidateResource {
    private final CandidateApi api;

    public CandidateResource(CandidateApi api) {
        this.api = api;
    }

    @POST
    @ResponseStatus(RestResponse.StatusCode.CREATED)
    @Transactional
    public Uni<Void> create(CreateCandidate dto) {
        return api.create(dto);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Uni<Candidate> update(@PathParam("id") String id, UpdateCandidate dto) {
        return api.update(id, dto);
    }

    @GET
    public Uni<List<Candidate>> list() {
        return api.list();
    }

    @GET
    @Path("/{id}")
    public Uni<Candidate> get(@PathParam("id") String id) {
        return api.get(id)
                  .onItem().ifNull().fail()
                  .onFailure().transform(throwable -> new NotFoundException());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> remove(@PathParam("id") String id) {
        return api.delete(id);
    }
}
