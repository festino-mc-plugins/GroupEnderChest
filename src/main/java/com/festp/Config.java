package com.festp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.enderchest.ECLocale;

public class Config {
	
	private static JavaPlugin plugin;
	private static MemoryConfiguration c;
	public static String pluginName;
	
	public static List<ECLocale> locales = new ArrayList<>();
	public static ECLocale currentLocale;
	
	public Config(JavaPlugin jp) {
		this.plugin = jp;
		pluginName = plugin.getName();
		this.c = jp.getConfig();
	}
	
	public static void loadConfig()
	{
		List<String> l1 = new ArrayList<>();
		l1.add("accept");
		l1.add("create");
		l1.add("info");
		c.addDefault("enderchest.allungroupTips", l1);
		List<String> l2 = new ArrayList<>();
		l2.add("accept");
		l2.add("info");
		l2.add("leave");
		c.addDefault("enderchest.allgroupTips", l2);
		List<String> l3 = new ArrayList<>();
		l3.add("accept");
		l3.add("info");
		l3.add("leave");
		l3.add("kick");
		l3.add("invite");
		l3.add("changeowner");
		l3.add("delete");
		c.addDefault("enderchest.allownerTips", l3);
		c.options().copyDefaults(true);
		plugin.saveConfig();

		ECLocale eng = new ECLocale();
		eng.usage_create = ChatColor.GRAY + "Usage: \"/enderchest create <groupname>\" or \"/ec create <groupname>\"";
		eng.usage_acreate = ChatColor.GRAY + "Usage: \"/enderchest acreate <groupname>\" or \"/ec acreate <groupname>\"";
		eng.usage_invite = ChatColor.GRAY + "Usage: \"/enderchest invite <nickname>\" or \"/ec invite <nickname>\"";
		eng.usage_accept = ChatColor.GRAY + "Usage: \"/enderchest accept <groupname>\" or \"/ec accept <groupname>\"";
		eng.usage_kick = ChatColor.GRAY + "Usage: \"/enderchest kick <nickname>\" or \"/ec kick <nickname>\"";
		eng.usage_leaveowner = ChatColor.GRAY + "Usage: \"/enderchest leave <nickname>\" or \"/ec leave <nickname>\"";
		eng.help = "";
		currentLocale = eng;
		
		System.out.println("["+pluginName+"] Config Reloaded.");
	}
	
	public static void saveConfig()
	{
		plugin.saveConfig();
		
		System.out.println("["+pluginName+"] Config successfully saved.");
	}
	
	public static JavaPlugin plugin() {
		return plugin;
	}
}
