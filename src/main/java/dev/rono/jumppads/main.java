package dev.rono.jumppads;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class main extends JavaPlugin implements Listener {
    private static FileConfiguration config;

    private final Set<Material> JumpPadMaterials = new HashSet<>();
    private Effect JumpPadEffect;
    private Sound JumpPadSound;
    private double JumpPadMultiplier;

    public void onEnable() {
        this.getLogger().info("JumpPads activated");

        saveDefaultConfig();

        config = getConfig();
        loadConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public void loadConfig() {
        var jumpPads = config.getStringList("jump-pads");
        for (var jumpPad : jumpPads) {
            var material = Material.getMaterial(jumpPad);
            if (material == null) {
                getLogger().warning(String.format("Material '%s' is not valid!", jumpPad));
            } else {
                JumpPadMaterials.add(material);
            }
        }

        var soundString = config.getString("sound");
        try {
            JumpPadSound = Sound.valueOf(soundString);
        } catch (IllegalArgumentException e) {
            if (JumpPadSound == null) {
                getLogger().warning(String.format("Sound '%s' not valid! %s.", soundString, e.getMessage()));
            }
        }

        var effectString = config.getString("effect");
        try {
            JumpPadEffect = Effect.valueOf(effectString);
        } catch (IllegalArgumentException e) {
            if (JumpPadEffect == null) {
                getLogger().warning(String.format("Effect '%s' not valid! %s.", effectString, e.getMessage()));
            }
        }

        JumpPadMultiplier = config.getDouble("jump.multiplier");
        if (JumpPadMultiplier == 0) {
            getLogger().warning(String.format("Multiplier '%s' not valid! Defaulting to 3.0.", config.getString("jump.multiplier")));
            JumpPadMultiplier = 3.0;
        }
    }

    public void onDisable() {
        this.getLogger().info("JumpPads deactivated.");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        var player = e.getPlayer();

        if (JumpPadMaterials.contains(player.getLocation().getBlock().getType())) {
            var vector = player.getLocation().getDirection().multiply(JumpPadMultiplier).setY(1.0);

            player.setVelocity(vector);

            player.playEffect(player.getLocation(), JumpPadEffect, 3);
            player.playSound(player.getLocation(), JumpPadSound, 3.0F, 2.0F);
        }
    }
}
