package gg.steve.skullwars.collectors.core;

import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityDeathEvent;

public interface CollectorEvents {

    void creatureDeath(EntityDeathEvent event);

    void cropBreak(Block block);
}
