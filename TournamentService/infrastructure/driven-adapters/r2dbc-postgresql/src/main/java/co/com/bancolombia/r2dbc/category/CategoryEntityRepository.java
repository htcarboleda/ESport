package co.com.bancolombia.r2dbc.category;

import co.com.bancolombia.r2dbc.entity.CategoryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CategoryEntityRepository extends ReactiveCrudRepository<CategoryEntity, Long> {
}
