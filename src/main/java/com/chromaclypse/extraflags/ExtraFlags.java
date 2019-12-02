package com.chromaclypse.extraflags;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class ExtraFlags extends JavaPlugin implements Listener {

	private static final StateFlag elytraUse = new StateFlag("elytra-use", true);
	private static final StringFlag commandsOnEnter = new StringFlag("commands-on-enter");
	private WorldGuardPlugin wg;
	private RegionContainer container;
	
	@Override
	public void onLoad() {
		WorldGuard.getInstance().getFlagRegistry().register(elytraUse);
		WorldGuard.getInstance().getFlagRegistry().register(commandsOnEnter);
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		container = WorldGuard.getInstance().getPlatform().getRegionContainer();
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll((JavaPlugin) this);
		wg = null;
		container = null;
	}
	
	@EventHandler
	public void elytra(EntityToggleGlideEvent event) {
		if(event.getEntity() instanceof Player) {
			LocalPlayer lp = wg.wrapPlayer((Player) event.getEntity());
			Location loc = event.getEntity().getLocation();
			
			RegionQuery query = container.createQuery();

			if(!query.testState(BukkitAdapter.adapt(loc), lp, elytraUse)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		RegionQuery query = container.createQuery();
		Set<ProtectedRegion> regions = query.getApplicableRegions(BukkitAdapter.adapt(event.getFrom())).getRegions();
		
		for(ProtectedRegion region : query.getApplicableRegions(BukkitAdapter.adapt(event.getTo())).getRegions()) {
			if(!regions.contains(region)) {
				String command = region.getFlag(commandsOnEnter);
				
				if(command != null) {
					command = command.replace("@p", event.getPlayer().getName());
	
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				}
			}
		}
	}
}
