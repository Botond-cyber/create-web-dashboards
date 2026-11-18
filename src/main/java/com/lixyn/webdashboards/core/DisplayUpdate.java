package com.lixyn.webdashboards.core;

/**
 * One line of text written to a console display. Plain data, no Minecraft types.
 *
 * @param pos       target block position, e.g. "12, 64, -30"
 * @param dimension dimension id, e.g. "minecraft:overworld"
 * @param line      1-based line number
 * @param text      the line's text
 * @param time      epoch millis when the update happened
 */
public record DisplayUpdate(String pos, String dimension, int line, String text, long time) {

    /** Key identifying which display line this update overwrites. */
    public String key() {
        return dimension + "@" + pos + "#" + line;
    }
}
