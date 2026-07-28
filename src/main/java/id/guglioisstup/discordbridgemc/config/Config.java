package id.guglioisstup.discordbridgemc.config;

public class Config {

    public Discord discord = new Discord();

    public static class Discord {
        public String token = "";
        public String guildId = "";
        public String channelId = "";
    }

    public boolean relayMinecraftChat = true;
    public boolean relayDiscordChat = true;
    public boolean relayJoinLeave = true;
    public boolean relayDeaths = true;
    public boolean relayAdvancements = true;
    public boolean enableSlashCommands = true;
}
