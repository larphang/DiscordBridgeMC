package id.guglioisstup.discordbridgemc.database.model;

import java.util.UUID;

public class PlayerData {
    private final UUID uuid;

    private String username;
    private String discordId;

    private long playtime;

    private long firstJoin;
    private long lastSeen;

    private long playerKills;
    private long mobKills;
    private long blocksPlaced;
    private long blocksBroken;
    private long deaths;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public long getPlaytime() {
        return playtime;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public long getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(long firstJoin) {
        this.firstJoin = firstJoin;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getPlayerKills() {
        return playerKills;
    }

    public long getMobKills() {
        return mobKills;
    }

    public long getBlocksPlaced() {
        return blocksPlaced;
    }

    public long getBlocksBroken() {
        return blocksBroken;
    }

    public long getDeaths() {
        return deaths;
    }

    public void setPlayerKills(long playerKills) {
        this.playerKills = playerKills;
    }

    public void setMobKills(long mobKills) {
        this.mobKills = mobKills;
    }

    public void setBlocksPlaced(long blocksPlaced) {
        this.blocksPlaced = blocksPlaced;
    }

    public void setBlocksBroken(long blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public void setDeaths(long deaths) {
        this.deaths = deaths;
    }
}