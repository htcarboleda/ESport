package co.com.bancolombia.r2dbc.config;

// TODO: Load properties from the application.yaml file or from secrets manager
// import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "r2dbc")
public class PostgresqlConnectionProperties {
    private String database;
    private String schema;
    private String username;
    private String password;
    private String host;
    private Integer port;


}