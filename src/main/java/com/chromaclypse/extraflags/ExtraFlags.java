package com.chromaclypse.extraflags;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class ExtraFlags extends JavaPlugin implements Listener {

	private static final StateFlag elytraUse = new StateFlag("elytra-use", true);
	private WorldGuardPlugin wg = null;
	private Object wgObject;
	
	@Override
	public void onLoad() {
		wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		FlagRegistry registry;
		
		try {
			wgObject = Class.forName("com.sk89q.worldguard.WorldGuard").getMethod("getInstance").invoke(null);
			
			registry = (FlagRegistry) wgObject.getClass().getMethod("getFlagRegistry").invoke(wgObject);
		}
		catch(Exception e) {
			registry = wg.getFlagRegistry();
		}
		
		try {
			registry.register(elytraUse);
		}
		catch(FlagConflictException e) {
			// HOW
		}
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
			
			RegionManager manager;
			try {
				Object wgp = wgObject.getClass().getMethod("getPlatform").invoke(wgObject);
				Object regionContainer = wgp.getClass().getMethod("getRegionContainer").invoke(wgp);
				manager = (RegionManager) regionContainer.getClass().getMethod("get", World.class).invoke(regionContainer, new BukkitWorld(loc.getWorld()));
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
			
			ApplicableRegionSet set = manager.getApplicableRegions(new Vector(loc.getX(), loc.getY(), loc.getZ()));
			
			if(!set.testState(lp, elytraUse)) {
				event.setCancelled(true);
			}
		}
	}
}
