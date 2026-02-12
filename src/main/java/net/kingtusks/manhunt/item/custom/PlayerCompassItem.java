package net.kingtusks.manhunt.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import java.util.UUID;

public class PlayerCompassItem extends Item {
    public PlayerCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide && entity instanceof Player) {
            UUID targetUUID = getTargetPlayerUUID(stack);
            if (targetUUID != null) {
                Player targetPlayer = level.getPlayerByUUID(targetUUID);
                if (targetPlayer != null) {}
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide && target instanceof Player targetPlayer) {
            var customData = stack.getOrDefault(DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.EMPTY);
            var tag = customData.copyTag();
            tag.putString("TargetPlayerUUID", targetPlayer.getUUID().toString());
            tag.putString("TargetPlayerName", targetPlayer.getName().getString());
            stack.set(DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.of(tag));
            player.displayClientMessage(
                    Component.literal("Now tracking: " + targetPlayer.getName().getString()),
                    true
            );

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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