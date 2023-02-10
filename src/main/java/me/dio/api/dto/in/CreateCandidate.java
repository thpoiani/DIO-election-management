package me.dio.api.dto.in;

import me.dio.domain.Candidate;

import java.util.Optional;

public record CreateCandidate(Optional<String> photo,
                              String givenName,
                              String familyName,
                              String email,
                              Optional<String> phone,
                              Optional<String> jobTitle) {
    public Candidate toDomain() {
        return Candidate.create(photo(), givenName(), familyName(), email(), phone(), jobTitle());
    }
}
