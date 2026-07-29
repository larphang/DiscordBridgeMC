package id.guglioisstup.discordbridgemc.mixin;

import id.guglioisstup.discordbridgemc.database.dao.PlayerDao;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Unique
    private static final Map<UUID, BlockPos> discordbridgemc$lastPosition = new HashMap<>();

    @Unique
    private static final Map<UUID, Long> discordbridgemc$lastTime = new HashMap<>();

    @Inject(
        method = "place",
        at = @At("RETURN")
    )
    private void discordbridgemc$afterPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.SUCCESS) {
            return;
        }

        if (context.getPlayer() == null) {
            return;
        }

        UUID uuid = context.getPlayer().getUUID();

        BlockPos pos = context.getClickedPos();

        long now = System.nanoTime();

        BlockPos lastPos = discordbridgemc$lastPosition.get(uuid);
        Long lastTime = discordbridgemc$lastTime.get(uuid);

        if (lastPos != null &&
            lastPos.equals(pos) &&
            lastTime != null &&
            now - lastTime < 50_000_000L) {
            return;
        }

        discordbridgemc$lastPosition.put(uuid, pos);
        discordbridgemc$lastTime.put(uuid, now);

        PlayerDao.addStat(
            uuid,
            "blocks_placed",
            1
        );
    }
}