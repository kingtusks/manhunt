package net.kingtusks.manhunt.item;

import net.kingtusks.manhunt.Manhunt;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, Manhunt.MODID
    );

    public static final Supplier<CreativeModeTab> MANHUNT_ITEMS_TAB = CREATIVE_MODE_TAB.register(
            "manhunt_items_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.COMPASS))
                    .title(Component.translatable("creativetab.manhunt.manhunt_items"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.)//(add) item here
                    }))
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
