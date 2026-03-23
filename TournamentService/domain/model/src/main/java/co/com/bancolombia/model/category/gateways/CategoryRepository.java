package co.com.bancolombia.model.category.gateways;

import co.com.bancolombia.model.category.Category;
import reactor.core.publisher.Mono;

public interface CategoryRepository {

    Mono<Category> findById(Integer id);
}
