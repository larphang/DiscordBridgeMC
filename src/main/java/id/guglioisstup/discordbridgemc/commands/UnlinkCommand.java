package id.guglioisstup.discordbridgemc.commands;

import com.mojang.brigadier.CommandDispatcher;
import id.guglioisstup.discordbridgemc.database.dao.PlayerDao;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import id.guglioisstup.discordbridgemc.database.dao.LinkCodeDao;

public final class UnlinkCommand {
    private UnlinkCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("unlinkdiscord")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    if (PlayerDao.getDiscordId(player.getUUID()) == null) {
                        player.sendSystemMessage(Component.literal("Your Minecraft account is not linked."));

                        return 0;
                    }

                    PlayerDao.unlinkDiscord(
                        player.getUUID()
                    );

                    LinkCodeDao.deletePlayerCodes(
                        player.getUUID().toString()
                    );

                    player.sendSystemMessage(
                        Component.literal("Your Discord account has been unlinked.")
                    );

                    return 1;
                })
        );
    }
}
