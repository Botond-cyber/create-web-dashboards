package com.lixyn.webdashboards.registry;

import com.lixyn.webdashboards.CreateWebDashboards;
import com.lixyn.webdashboards.display.ConsoleDisplayTarget;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRegistry {
    private static final String MODID = CreateWebDashboards.MODID;

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<DisplayTarget> DISPLAY_TARGETS =
            DeferredRegister.create(CreateRegistries.DISPLAY_TARGET, MODID);

    public static final DeferredBlock<Block> CONSOLE_DISPLAY = BLOCKS.registerSimpleBlock("console_display",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(1.5f)
                    .sound(SoundType.METAL));

    public static final DeferredItem<BlockItem> CONSOLE_DISPLAY_ITEM =
            ITEMS.registerSimpleBlockItem(CONSOLE_DISPLAY);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MODID))
                    .icon(() -> new ItemStack(CONSOLE_DISPLAY_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(CONSOLE_DISPLAY_ITEM.get());
                    })
                    .build());

    public static final DeferredHolder<DisplayTarget, ConsoleDisplayTarget> CONSOLE_DISPLAY_TARGET =
            DISPLAY_TARGETS.register("console_display", ConsoleDisplayTarget::new);

    private ModRegistry() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        DISPLAY_TARGETS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        modEventBus.addListener(ModRegistry::commonSetup);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        // Tell Create's Display Link that our block is a valid target.
        event.enqueueWork(() -> DisplayTarget.BY_BLOCK.register(CONSOLE_DISPLAY.get(), CONSOLE_DISPLAY_TARGET.get()));
    }
}
