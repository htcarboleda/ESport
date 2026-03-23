package co.com.bancolombia.r2dbc.category;

import co.com.bancolombia.model.category.Category;
import co.com.bancolombia.model.category.gateways.CategoryRepository;
import co.com.bancolombia.r2dbc.entities.CategoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryEntityRepositoryAdapter implements CategoryRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final CategoryEntityRepository categoryEntityRepository;

    public Mono<Category> findById(Integer id) {

        return r2dbcEntityTemplate.select(CategoryEntity.class)
                .matching(Query.query(Criteria.where("id_category").is(id)))
                .one()
                .map(CategoryEntity::toDomain);
    }
}