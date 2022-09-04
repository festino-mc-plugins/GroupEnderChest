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
	public EnderFileStorage ecstorage;
	private int groupticks = 0;
	private int maxgroupticks = 3*60*20; //3 minutes
	
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
		
    	File ECpluginFolder = new File(PATH + enderdir);
		if (ECpluginFolder.exists() == false) {
    		ECpluginFolder.mkdir();
    	}
		ecstorage = new EnderFileStorage(this);
    	
    	EnderChestHandler ecH = new EnderChestHandler(this);
    	pm.registerEvents(ecH, this);
    	
    	ECCommandWorker ecCW = new ECCommandWorker(this);
    	getCommand("enderchest").setExecutor(ecCW);
    	getCommand("ec").setExecutor(ecCW);
    	
    	ECTabCompleter ectc = new ECTabCompleter(this);
    	getCommand("enderchest").setTabCompleter(ectc);
    	getCommand("ec").setTabCompleter(ectc);
    	ecgroup.loadEnderChests(ecstorage, ECpluginFolder);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
			new Runnable() {
				public void run() {
					groupticks++;
					if(groupticks >= maxgroupticks) {
						ecgroup.saveEnderChests(ecstorage);
						groupticks = 0;
					}
				}
			}, 0L, 1L);
		
	}
	
	public void onDisable()
	{
		ecgroup.saveEnderChests(ecstorage);
	}
}
