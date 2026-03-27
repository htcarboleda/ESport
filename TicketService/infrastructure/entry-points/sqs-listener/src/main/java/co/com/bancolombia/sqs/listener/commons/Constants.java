package co.com.bancolombia.sqs.listener.commons;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String EVENT_TYPE = "eventType";
    public static final String P_EVENT_TYPE = "EventType";
    public static final String P_TOURNAMENT= "tournamentId";
    public static final String MESSAGES= "Message";
    public static final String ASYNC_OPERATION= "async_operation";
    public static final String OPERATION= "operation";

}

