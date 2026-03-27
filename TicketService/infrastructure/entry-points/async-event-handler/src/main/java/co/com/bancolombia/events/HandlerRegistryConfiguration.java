package co.com.bancolombia.events;
import co.com.bancolombia.events.commons.Constants;
import co.com.bancolombia.events.handlers.EventsHandler;
import org.reactivecommons.async.api.HandlerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerRegistryConfiguration {

    @Bean
    public HandlerRegistry handlerRegistry(EventsHandler events) {
        return HandlerRegistry.register()
                .listenNotificationEvent(Constants.EVENT_NAME, events::handleEventA, Integer.class)
                .listenEvent(Constants.EVENT_NAME, events::handleEventA, Integer.class);
    }
}
