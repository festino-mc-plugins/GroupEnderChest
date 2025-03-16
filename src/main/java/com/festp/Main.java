package com.festp;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.enderchest.AdminChannelPlayer;
import com.festp.enderchest.ECCommandWorker;
import com.festp.enderchest.ECTabCompleter;
import com.festp.enderchest.EnderChestGroup;
import com.festp.enderchest.EnderChestHandler;
import com.festp.enderchest.EnderFileStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener
{
	public static final String enderdir = "EnderChestGroups";
	private static String PATH = "plugins" + System.getProperty("file.separator") + "NAME_ERROR__GROUP_ENDER_CHEST" + System.getProperty("file.separator");
	private static String pluginname;
	
	Config conf;

	public List<AdminChannelPlayer> admin_ecplayers = new ArrayList<>();
	public EnderChestGroup ecgroup = new EnderChestGroup(this);
	public EnderFileStorage ecFileStorage;
	private int saveTicks = 0;
	private int maxSaveTicks = 3*60*20; //3 minutes
	
	public static String getPath() {
		return PATH;
	}
	
	public void onEnable() {
		Logger.setLogger(getLogger());
		pluginname = getName();
		PATH = "plugins" + System.getProperty("file.separator") + pluginname + System.getProperty("file.separator");
    	PluginManager pm = getServer().getPluginManager();
    	
		getServer().getPluginManager().registerEvents(this, this);
		
		conf = new Config(this);
		Config.loadConfig();
		
    	File ecPluginFolder = new File(PATH + enderdir);
		if (ecPluginFolder.exists() == false) {
    		ecPluginFolder.mkdir();
    	}
		ecFileStorage = new EnderFileStorage(this);
    	
    	EnderChestHandler ecHandler = new EnderChestHandler(this);
    	pm.registerEvents(ecHandler, this);
    	
    	ECCommandWorker ecCommandWorker = new ECCommandWorker(this);
    	getCommand("enderchest").setExecutor(ecCommandWorker);
    	getCommand("ec").setExecutor(ecCommandWorker);
    	
    	ECTabCompleter ecTabCompleter = new ECTabCompleter(this);
    	getCommand("enderchest").setTabCompleter(ecTabCompleter);
    	getCommand("ec").setTabCompleter(ecTabCompleter);
    	ecgroup.loadEnderChests(ecFileStorage, ecPluginFolder);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
			new Runnable() {
				public void run() {
					saveTicks++;
					if (saveTicks >= maxSaveTicks) {
						ecgroup.saveEnderChests(ecFileStorage);
						saveTicks = 0;
					}
					ecHandler.tick();
				}
			}, 0L, 1L);
		
	}
	
	public void onDisable()
	{
		ecgroup.saveEnderChests(ecFileStorage);
	}
}
