package me.dio.infrastructure.resources;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.common.mapper.TypeRef;
import io.smallrye.mutiny.Uni;
import me.dio.api.CandidateApi;
import me.dio.api.dto.in.CreateCandidate;
import me.dio.api.dto.in.UpdateCandidate;
import me.dio.api.dto.out.Candidate;
import org.instancio.Instancio;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestHTTPEndpoint(CandidateResource.class)
class CandidateResourceTest {
    @InjectMock
    CandidateApi api;

    @Test
    void create_servesApi() {
        CreateCandidate dtoIn = Instancio.create(CreateCandidate.class);

        given().contentType(MediaType.APPLICATION_JSON).body(dtoIn)
               .when().post()
               .then().statusCode(RestResponse.StatusCode.CREATED);

        verify(api).create(dtoIn);
        verifyNoMoreInteractions(api);
    }

    @Test
    void update_servesApi() {
        UpdateCandidate dtoIn = Instancio.create(UpdateCandidate.class);
        Candidate dtoOut = Instancio.create(Candidate.class);

        when(api.update(dtoOut.id(), dtoIn)).thenReturn(Uni.createFrom().item(dtoOut));

        Candidate response = given().contentType(MediaType.APPLICATION_JSON).body(dtoIn)
                .when().put("/" + dtoOut.id())
                .then().statusCode(RestResponse.StatusCode.OK).extract().as(Candidate.class);

        verify(api).update(dtoOut.id(), dtoIn);
        verifyNoMoreInteractions(api);

        assertEquals(dtoOut, response);
    }

    @Test
    void list_servesApi() {
        List<Candidate> dtoOut = Instancio.stream(Candidate.class).limit(4).toList();

        when(api.list()).thenReturn(Uni.createFrom().item(dtoOut));

        List<Candidate> response = given()
                .when().get()
                .then().statusCode(RestResponse.StatusCode.OK).extract().as(new TypeRef<>() {});

        verify(api).list();
        verifyNoMoreInteractions(api);

        assertEquals(dtoOut, response);
    }

    @Test
    void get_whenResourceFound_servesApi() {
        Candidate dtoOut = Instancio.create(Candidate.class);

        when(api.get(dtoOut.id())).thenReturn(Uni.createFrom().item(dtoOut));

        Candidate response = given()
                .when().get("/" + dtoOut.id())
                .then().statusCode(RestResponse.StatusCode.OK).extract().as(Candidate.class);

        verify(api).get(dtoOut.id());
        verifyNoMoreInteractions(api);

        assertEquals(dtoOut, response);
    }

    @Test
    void get_whenResourceNotFound_servesApi() {
        String id = UUID.randomUUID().toString();

        when(api.get(id)).thenReturn(Uni.createFrom().nullItem());

        given().when().get("/" + id)
               .then().statusCode(RestResponse.StatusCode.NOT_FOUND);

        verify(api).get(id);
        verifyNoMoreInteractions(api);
    }

    @Test
    void remove_servesApi() {
        String id = UUID.randomUUID().toString();

        when(api.delete(id)).thenReturn(Uni.createFrom().voidItem());

        given().when().delete("/" + id)
               .then().statusCode(RestResponse.StatusCode.NO_CONTENT);

        verify(api).delete(id);
        verifyNoMoreInteractions(api);
    }
}