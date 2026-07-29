package id.guglioisstup.discordbridgemc.link;

import id.guglioisstup.discordbridgemc.database.dao.LinkCodeDao;
import id.guglioisstup.discordbridgemc.database.dao.PlayerDao;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class LinkService {
    private static final int CODE_LENGTH = 6;

    private LinkService() { }

    public static String createLinkCode(UUID playerUuid) {
        if (PlayerDao.getDiscordId(playerUuid) != null) {
            return null;
        }

        LinkCodeDao.deletePlayerCodes(
            playerUuid.toString()
        );

        String code = generateCode();

        LinkCodeDao.createCode(
            code,
            playerUuid.toString()
        );

        return code;
    }

    public static boolean redeemCode(String code, String discordId) {
        String uuid = LinkCodeDao.getPlayerUuid(code);

        if (uuid == null) {
            return false;
        }

        UUID playerUuid;

        try {
            playerUuid = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            return false;
        }

        PlayerDao.linkDiscord(playerUuid, discordId);

        LinkCodeDao.deleteCode(code);

        return true;
    }

    public static boolean isLinked(UUID playerUuid) {
        return PlayerDao.getDiscordId(playerUuid) != null;
    }

    public static void unlink(UUID playerUuid) {
        PlayerDao.unlinkDiscord(playerUuid);
    }

    private static String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = ThreadLocalRandom.current().nextInt(chars.length());

            builder.append(chars.charAt(index));
        }

        return builder.toString();
    }
}
