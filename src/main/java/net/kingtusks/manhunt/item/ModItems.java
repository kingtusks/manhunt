package net.kingtusks.manhunt.item;

import net.kingtusks.manhunt.Manhunt;
import net.kingtusks.manhunt.item.custom.PlayerCompassItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Manhunt.MODID);

    public static final DeferredItem<Item> PLAYER_COMPASS = ITEMS.register("player_compass",
            () -> new PlayerCompassItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
