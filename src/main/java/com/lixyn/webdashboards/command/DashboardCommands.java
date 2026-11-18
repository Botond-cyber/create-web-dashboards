package com.lixyn.webdashboards.command;

import com.lixyn.webdashboards.CreateWebDashboards;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.Minecart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

// Registered on the game event bus automatically via @EventBusSubscriber.
@EventBusSubscriber(modid = CreateWebDashboards.MODID)
public final class DashboardCommands {
    private DashboardCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("webdashboards")
                // /webdashboards ping
                .then(Commands.literal("ping").executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal("Pong from Create Web Dashboards!"), false);
                    return 1;
                }))
                // /webdashboards say <message>  (operators only)
                .then(Commands.literal("say").requires(src -> src.hasPermission(2)).then(Commands.argument("message", StringArgumentType.greedyString()).executes(ctx -> {
                    String msg = StringArgumentType.getString(ctx, "message");
                    ctx.getSource().sendSuccess(() -> Component.literal("Dashboard says: " + msg), true);
                    return 1;
                }))));
        dispatcher.register(Commands.literal("version").executes(ctx -> {
            ctx.getSource().sendSuccess(() -> Component.literal(Minecraft.getInstance().toString()), false);
            return 1;
        }));

//                .then(Commands.literal("version")
//                        .executes(ctx -> {
//                            ctx.getSource().sendSuccess(() -> Component.literal(Minecraft.getInstance().toString()), false);
//                            return 1;
//                        })));

    }
}
