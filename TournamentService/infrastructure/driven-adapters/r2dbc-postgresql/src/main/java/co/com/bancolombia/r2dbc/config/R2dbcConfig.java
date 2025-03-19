package co.com.bancolombia.r2dbc.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import java.util.ArrayList;
import java.util.List;

//@Configuration
//public class R2dbcConfig extends AbstractR2dbcConfiguration {
//
//    private final ConnectionFactory connectionFactory;
//
//    public R2dbcConfig(ConnectionFactory connectionFactory) {
//        this.connectionFactory = connectionFactory;
//    }
//
//    @Override
//    public ConnectionFactory connectionFactory() {
//        return this.connectionFactory;
//    }
//
//    @Override
//    protected List<Object> getCustomConverters() {
//        List<Object> converters = new ArrayList<>();
//        converters.add(new TournamentFormatConverters.StringToTournamentFormatConverter());
//        converters.add(new TournamentFormatConverters.TournamentFormatToStringConverter());
//        return converters;
//    }
//}