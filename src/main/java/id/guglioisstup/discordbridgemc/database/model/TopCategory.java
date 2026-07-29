package id.guglioisstup.discordbridgemc.database.model;

public enum TopCategory {

    PLAYTIME(
        "playtime",
        "Playtime"
    ),

    PLAYER_KILLS(
        "player_kills",
        "Player Kills"
    ),

    MOB_KILLS(
        "mob_kills",
        "Mob Kills"
    ),

    DEATHS(
        "deaths",
        "Deaths"
    ),

    BLOCKS_BROKEN(
        "blocks_broken",
        "Blocks Broken"
    ),

    BLOCKS_PLACED(
        "blocks_placed",
        "Blocks Placed"
    );


    public final String column;
    public final String display;


    TopCategory(String column, String display) {
        this.column = column;
        this.display = display;
    }
}