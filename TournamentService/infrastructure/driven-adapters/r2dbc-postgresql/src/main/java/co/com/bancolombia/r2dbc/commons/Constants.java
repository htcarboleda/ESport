package co.com.bancolombia.r2dbc.commons;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String CATEGORY_ID = "id_category";
    public static final String GAMETIME_ID = "id_game_type";
    public static final String USER_ID = "id_user";
    public static final String TOURNAMENT_ID = "id_tournament";
    public static final String TOURNAMENT_NAME = "name";
    public static final String TOURNAMENT_START_DATE = "start_date";
    public static final String TOURNAMENT_END_DATE = "end_date";
    public static final String TOURNAMENT_ID_CATEGORY = "fk_id_category";
    public static final String TOURNAMENT_ID_GAME = "fk_id_game_type";
    public static final String TOURNAMENT_IS_FREE = "is_free";

}
