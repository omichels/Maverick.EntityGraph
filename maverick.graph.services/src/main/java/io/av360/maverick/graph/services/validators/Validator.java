package io.av360.maverick.graph.services.validators;

import io.av360.maverick.graph.services.EntityServices;
import io.av360.maverick.graph.store.rdf.models.TripleModel;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface Validator {

    Mono<? extends TripleModel> handle(EntityServices entityServicesImpl, TripleModel model, Map<String, String> parameters);
}
