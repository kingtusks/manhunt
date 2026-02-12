package net.kingtusks.manhunt.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerCompassItem extends CompassItem {
    public PlayerCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof ServerPlayer) {
            String uuidString = stack.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty())
                    .getString();

            UUID targetUUID = getTargetPlayerUUID(stack);

            if (targetUUID != null) {
                Player targetPlayer = level.getPlayerByUUID(targetUUID);

                if (targetPlayer != null) {
                    BlockPos targetPos = targetPlayer.blockPosition();
                    GlobalPos globalPos = GlobalPos.of(level.dimension(), targetPos);

                    stack.set(DataComponents.LODESTONE_TRACKER,
                            new LodestoneTracker(Optional.of(globalPos), false));
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player.isShiftKeyDown()) {
            List<? extends Player> nearbyPlayers = level.players().stream()
                    .filter(p -> p != player)
                    .filter(p -> p.distanceTo(player) <= 100)
                    .toList();

            if (!nearbyPlayers.isEmpty()) {
                Player target = nearbyPlayers.getFirst();
                setTargetPlayerUUID(stack, target.getUUID());
                player.displayClientMessage(
                        Component.literal("Now tracking: " + target.getName().getString()),
                        true
                );

                return InteractionResultHolder.success(stack);
            } else {
                player.displayClientMessage(
                        Component.literal("No players nearby to track!"),
                        true
                );
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    private void setTargetPlayerUUID(ItemStack stack, UUID uuid) {
        var customData = stack.getOrDefault(DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY);
        var tag = customData.copyTag();
        tag.putString("TargetPlayerUUID", uuid.toString());
        stack.set(DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
    }

    private UUID getTargetPlayerUUID(ItemStack stack) {
        var customData = stack.getOrDefault(DataComponents.CUSTOM_DATA,
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

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}