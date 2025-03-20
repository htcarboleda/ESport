package co.com.bancolombia.r2dbc.entities;

import co.com.bancolombia.model.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("categories")
public class CategoryEntity {

    @Id
    @Column("id_category")
    private Integer id;

    @Column("code")
    private String code;

    @Column("alias")
    private String alias;

    @Column("description")
    private String description;

    @Column("max_free_participants")
    private Integer maxFreeParticipants;



    public static CategoryEntity fromDomain(Category category) {
        return CategoryEntity.builder()
                .id(category.getId())
                .code(category.getCode())
                .alias(category.getAlias())
                .description(category.getDescription())
                .maxFreeParticipants(category.getMaxFreeParticipants())
                .build();
    }

    public Category toDomain() {
        return Category.builder()
                .id(this.getId())
                .code(this.getCode())
                .alias(this.getAlias())
                .description(this.getDescription())
                .maxFreeParticipants(this.getMaxFreeParticipants())
                .build();
    }


}
