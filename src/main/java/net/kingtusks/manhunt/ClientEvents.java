package net.kingtusks.manhunt;

import net.kingtusks.manhunt.item.ModItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import java.util.UUID;

@EventBusSubscriber(modid = Manhunt.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static double rotation = Math.random();
    private static double rota = Math.random();
    private static long lastUpdateTime = 0;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.PLAYER_COMPASS.get(),
                    ResourceLocation.withDefaultNamespace("angle"),
                    (stack, level, entity, seed) -> {
                        if (entity == null) return 0.0f;
                        UUID targetUUID = getTargetPlayerUUID(stack);
                        if (targetUUID == null || level == null) {
                            return wobble(level, Math.random());
                        }
                        Player targetPlayer = level.getPlayerByUUID(targetUUID);
                        if (targetPlayer == null) {
                            return wobble(level, Math.random());
                        }
                        return getAngleToPlayer(entity, targetPlayer, level);
                    });
        });
    }

    private static float wobble(ClientLevel level, double target) {
        long time = level.getGameTime();
        if (time != lastUpdateTime) {
            lastUpdateTime = time;
            double delta = target - rota;
            delta = Mth.positiveModulo(delta + 0.5, 1.0) - 0.5;
            rota = Mth.positiveModulo(rota + delta * 0.1, 1.0);
            rotation = Mth.lerp(0.5, rotation, rota);
        }
        return (float) rotation;
    }

    private static float getAngleToPlayer(Entity viewer, Player target, ClientLevel level) {
        double deltaX = target.getX() - viewer.getX();
        double deltaZ = target.getZ() - viewer.getZ();
        double angle = Math.atan2(deltaZ, deltaX) / Math.PI;
        double rotation = (angle + 1.0) * 0.5;
        double yaw = Mth.positiveModulo(viewer.getYRot() / 360.0, 1.0);
        rotation = Mth.positiveModulo(rotation - yaw, 1.0);
        System.out.println("Compass angle: " + rotation);
        return (float) rotation;
    }

    private static UUID getTargetPlayerUUID(ItemStack stack) {
        var customData = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY);
        var tag = customData.copyTag();

        if (tag.contains("TargetPlayerUUID")) {
            try {
                return UUID.fromString(tag.getString("TargetPlayerUUID"));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}