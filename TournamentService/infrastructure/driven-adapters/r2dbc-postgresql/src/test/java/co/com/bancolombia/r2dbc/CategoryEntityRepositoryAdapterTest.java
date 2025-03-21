package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.category.Category;
import co.com.bancolombia.model.gametype.GameType;
import co.com.bancolombia.r2dbc.category.CategoryEntityRepository;
import co.com.bancolombia.r2dbc.category.CategoryEntityRepositoryAdapter;
import co.com.bancolombia.r2dbc.entities.CategoryEntity;
import co.com.bancolombia.r2dbc.entities.GameTypeEntity;
import co.com.bancolombia.r2dbc.gametype.GameTypeEntityRepository;
import co.com.bancolombia.r2dbc.gametype.GameTypeEntityRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryEntityRepositoryAdapterTest {

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Mock
    private CategoryEntityRepository categoryEntityRepository;

    @InjectMocks
    private CategoryEntityRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_Success() {
        // Datos de prueba
        CategoryEntity categoryEntity = new CategoryEntity(1, "Category 1","Alias 1","Description 1", 10);
        Category expectedCategory = categoryEntity.toDomain();

        // Mock the complete chain
        ReactiveSelectOperation.ReactiveSelect<CategoryEntity> reactiveSelect = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<CategoryEntity> terminatingSelect = mock(ReactiveSelectOperation.TerminatingSelect.class);

        when(r2dbcEntityTemplate.select(CategoryEntity.class)).thenReturn(reactiveSelect);
        when(reactiveSelect.matching(any(Query.class))).thenReturn(terminatingSelect);
        when(terminatingSelect.one()).thenReturn(Mono.just(categoryEntity));

        // Ejecutamos la prueba
        StepVerifier.create(adapter.findById(1))
                .expectNextMatches(category -> category.getId().equals(expectedCategory.getId()) &&
                        category.getDescription().equals(expectedCategory.getDescription()))
                .verifyComplete();

        verify(r2dbcEntityTemplate, times(1)).select(CategoryEntity.class);
        verify(reactiveSelect, times(1)).matching(any(Query.class));
        verify(terminatingSelect, times(1)).one();
    }

    @Test
    void testFindById_NotFound() {
        // Simulamos que no se encuentra el game type en la base de datos
        ReactiveSelectOperation.ReactiveSelect<CategoryEntity> reactiveSelect = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<CategoryEntity> terminatingSelect = mock(ReactiveSelectOperation.TerminatingSelect.class);

        when(r2dbcEntityTemplate.select(CategoryEntity.class)).thenReturn(reactiveSelect);
        when(reactiveSelect.matching(any(Query.class))).thenReturn(terminatingSelect);
        when(terminatingSelect.one()).thenReturn(Mono.empty());

        // Ejecutamos la prueba
        StepVerifier.create(adapter.findById(1))
                .verifyComplete(); // Debe terminar sin emitir datos

        verify(r2dbcEntityTemplate, times(1)).select(CategoryEntity.class);
        verify(reactiveSelect, times(1)).matching(any(Query.class));
        verify(terminatingSelect, times(1)).one();
    }

    @Test
    void testFindById_Error() {
        // Mock the complete chain
        ReactiveSelectOperation.ReactiveSelect<CategoryEntity> reactiveSelect = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<CategoryEntity> terminatingSelect = mock(ReactiveSelectOperation.TerminatingSelect.class);

        when(r2dbcEntityTemplate.select(CategoryEntity.class)).thenReturn(reactiveSelect);
        when(reactiveSelect.matching(any(Query.class))).thenReturn(terminatingSelect);
        when(terminatingSelect.one()).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Ejecutamos la prueba
        StepVerifier.create(adapter.findById(1))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();

        verify(r2dbcEntityTemplate, times(1)).select(CategoryEntity.class);
        verify(reactiveSelect, times(1)).matching(any(Query.class));
        verify(terminatingSelect, times(1)).one();
    }
}
