package id.guglioisstup.discordbridgemc.commands;

import com.mojang.brigadier.CommandDispatcher;
import id.guglioisstup.discordbridgemc.link.LinkService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

public final class LinkCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("linkdiscord")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    String code = LinkService.createLinkCode(player.getUUID());

                    if (code == null) {
                        player.sendSystemMessage(
                            Component.literal("Your Minecraft account is already linked.")
                        );
                        return 0;
                    }

                    Component message = Component.literal("Your Discord link code is: ")
                        .append(
                            Component.literal(code)
                                .setStyle(
                                    Style.EMPTY
                                        .withClickEvent(new ClickEvent.CopyToClipboard(code))
                                        .withUnderlined(true)
                                )
                        )
                        .append(
                            Component.literal("\nClick the code to copy it and DM the bot.")
                        );

                    player.sendSystemMessage(message);

                    return 1;
                })
        );
    }
}