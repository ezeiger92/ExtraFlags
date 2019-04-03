package com.chromaclypse.extraflags;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class ExtraFlags extends JavaPlugin implements Listener {

	private static final StateFlag elytraUse = new StateFlag("elytra-use", true);
	private WorldGuardPlugin wg = null;
	
	@Override
	public void onLoad() {
		wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		
		WorldGuard.getInstance().getFlagRegistry().register(elytraUse);
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin) this);
	}
	
	@EventHandler
	public void elytra(EntityToggleGlideEvent event) {
		if(event.getEntity() instanceof Player) {
			LocalPlayer lp = wg.wrapPlayer((Player) event.getEntity());
			Location loc = event.getEntity().getLocation();
			
			RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(loc.getWorld()));
			
			ApplicableRegionSet set = manager.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));

			if(!set.testState(lp, elytraUse)) {
				event.setCancelled(true);
			}
		}
	}
}
