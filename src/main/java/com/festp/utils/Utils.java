package com.festp.utils;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Utils {
	/** Can give items only to players.
	 * @return <b>null</b> if the <b>stack</b> was only given<br>
	 * <b>Item</b> if at least one item was dropped*/
	public static void giveOrDrop(Player player, ItemStack stack)
	{
		HashMap<Integer, ItemStack> res = player.getInventory().addItem(stack);
		if (res.isEmpty())
			return;
		dropUngiven(player.getLocation(), res.get(0));
	}
	private static Item dropUngiven(Location l, ItemStack stack) {
		Item item = l.getWorld().dropItem(l, stack);
		item.setVelocity(new Vector());
		item.setPickupDelay(0);
		return item;
	}
	
	public static void sendPacketPlayOutBlockAction(Player player, Block chest, boolean setOpen)
	{
		try  {
			int param = 0; // Action param, number of players (0 to close)
			if (setOpen) // Action param, number of players (> 0 to open)
				param = 1;
			int actionId = 1; // Action ID, always 1 to opening chests (https://wiki.vg/Block_Actions#Chest)

			// org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
			Class<?> magicClass = Utils.getCraftbukkitClass("util.CraftMagicNumbers");
			Method getBlockMethod = magicClass.getMethod("getBlock", Material.class);
			Object nmsBlock = getBlockMethod.invoke(null, chest.getType());
			Class<?> blockClass = Class.forName("net.minecraft.world.level.block.Block");
			
			// net.minecraft.core.BlockPosition;
			Class<?> blockPositionClass = Class.forName("net.minecraft.core.BlockPosition");
			Constructor<?> blockPositionConstructor = blockPositionClass.getConstructor(int.class, int.class, int.class);
			Object blockPosition = blockPositionConstructor.newInstance(chest.getX(), chest.getY(), chest.getZ());

			// net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
			Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutBlockAction");
			Constructor<?> packetConstructor = packetClass.getConstructor(blockPositionClass, blockClass, int.class, int.class);
			Object packet = packetConstructor.newInstance(blockPosition, nmsBlock, actionId, param);

			// ((CraftPlayer) player).getHandle().playerConnection.sendPacket
			// ((CraftPlayer) p).getHandle().b.a(packet);
			// org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
			Class<?> craftPlayerClass = Utils.getCraftbukkitClass("entity.CraftPlayer");
			Class<?> entityPlayerClass = Class.forName("net.minecraft.server.level.EntityPlayer");
			Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
			getHandleMethod.setAccessible(true);
			Class<?> playerConnectionClass = Class.forName("net.minecraft.server.network.PlayerConnection");
			
			Class<?> anyPacketClass = Class.forName("net.minecraft.network.protocol.Packet");
			Method sendPacketMethod = playerConnectionClass.getMethod("a", anyPacketClass);
			Object craftPlayer = craftPlayerClass.cast(player);
			Object entityPlayer = entityPlayerClass.cast(getHandleMethod.invoke(craftPlayer));
			Object playerConnection = ReflectionUtils.findAndGetField(entityPlayer, playerConnectionClass);
			sendPacketMethod.invoke(playerConnection, packet);
		} catch (Exception e) {
		    e.printStackTrace(System.err);
			player.sendMessage(ChatColor.RED + "Reflection error in \"" + Utils.class.getName() + ".sendPacketPlayOutBlockAction\". Please the contact administration.");
		}
	}
	
	/** Use for paths like "org.bukkit.craftbukkit.v1_19_R1". */
	public static Class<?> getCraftbukkitClass(String relativePath)
	{
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("org.bukkit.craftbukkit." + version + "." + relativePath);
		} catch (ClassNotFoundException e) { }
		return null;
	}
	public static void printConstructors(Class<?> clazz) {
		printConstructors(clazz, System.err);
	}
	public static void printConstructors(Class<?> clazz, PrintStream stream) {
		stream.println("Constructors of " + clazz.getName());
		for (Constructor<?> c : clazz.getConstructors())
		{
			stream.println("  " + c.getParameters().length);
			for (Parameter par : c.getParameters())
			{
				stream.println("    " + par.getName() + " " + par.getType().getName());
			}
		}
	}
	public static void printMethods(Class<?> clazz) {
		printMethods(clazz, System.err);
	}
	public static void printMethods(Class<?> clazz, PrintStream stream) {
		Method[] methods = clazz.getMethods();
		stream.println("Methods(" + methods.length + ") of " + clazz.getName());
		Comparator<Method> comp = new Comparator<Method>() {
			@Override
			public int compare(Method m1, Method m2) {
				int res = m1.getName().compareTo(m2.getName());
				if (res != 0)
					return res;
				res = m1.getParameterCount() - m2.getParameterCount();
				if (res != 0)
					return res;
				for (int i = 0; i < m1.getParameterCount(); i++)
				{
					String className1 = m1.getParameters()[i].getType().getName();
					String className2 = m2.getParameters()[i].getType().getName();
					res = className1.compareTo(className2);
					if (res != 0)
						break;
				}
				return res;
			}
		};
		Arrays.sort(methods, 0, methods.length, comp);
		int i = 0;
		for (Method m : methods)
		{
			i++;
			// there may be too many methods to print => the index helps catch it
			stream.println("  " + i + ") " + m.getName() + " " + m.getParameters().length + " " + m.getReturnType().toString());
			for (Parameter par : m.getParameters())
			{
				stream.println("    " + par.getName() + " " + par.getType().getName());
			}
		}
	}
}
