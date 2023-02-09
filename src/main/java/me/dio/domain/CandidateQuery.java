package me.dio.domain;

import org.inferred.freebuilder.FreeBuilder;

import java.util.Optional;
import java.util.Set;

@FreeBuilder
public interface CandidateQuery {
    Optional<Set<String>> candidateIds();
    Optional<String> familyName();

    class Builder extends CandidateQuery_Builder {}
}
