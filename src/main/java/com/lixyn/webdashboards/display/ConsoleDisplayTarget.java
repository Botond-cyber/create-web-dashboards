package com.lixyn.webdashboards.display;

import java.util.List;
import java.util.stream.Collectors;

import com.lixyn.webdashboards.CreateWebDashboards;
import com.lixyn.webdashboards.core.DisplayUpdate;
import com.lixyn.webdashboards.mc.ServerEvents;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.MutableComponent;

/**
 * A Display Link target that writes everything it receives to the server log.
 */
public class ConsoleDisplayTarget extends DisplayTarget {

    public static final int MAX_ROWS = 16;
    public static final int MAX_COLUMNS = 256;

    @Override
    public void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context) {
        String lines = text.stream()
                .map(MutableComponent::getString)
                .collect(Collectors.joining(" | "));

        CreateWebDashboards.LOGGER.info("[Display Target @ {}] from link @ {} (source @ {}), line {}: {}",
                context.getTargetPos().toShortString(),
                context.blockEntity().getBlockPos().toShortString(),
                context.getSourcePos().toShortString(),
                line + 1,
                lines);

        ServerEvents.publish(new DisplayUpdate(
                context.getTargetPos().toShortString(),
                context.level().dimension().location().toString(),
                line + 1,
                lines,
                System.currentTimeMillis()));
    }

    @Override
    public DisplayTargetStats provideStats(DisplayLinkContext context) {
        return new DisplayTargetStats(MAX_ROWS, MAX_COLUMNS, this);
    }
}
