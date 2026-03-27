package co.com.bancolombia.redis.commons;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String CATEGORY_KEY_PREFIX = "category:exists:";
    public static final String GAMETYPE_KEY_PREFIX = "game_type:exists:";
    public static final String TOURNAMENT_KEY_PREFIX = "tournament:exists:";

}
