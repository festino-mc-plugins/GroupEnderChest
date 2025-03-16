package com.festp.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.festp.Logger;

public class ReflectionUtils {
	
	private static class CacheEntry
	{
		public final Class<?> objClass;
		public final Class<?> fieldClass;
		public final Field field;
		
		public CacheEntry(Class<?> objClass, Class<?> fieldClass, Field field)
		{
			this.objClass = objClass;
			this.fieldClass = fieldClass;
			this.field = field;
		}
	}
	private static final List<CacheEntry> ObjectFieldCache = new ArrayList<>();
    
    public static <T> T findAndGetField(Object object, Class<T> fieldClass)
    {
    	Field field = findField(object.getClass(), fieldClass);
    	if (field == null) {
    		Logger.severe("ReflectionUtils: Couldn't find " + fieldClass.getSimpleName() + " in " + object.getClass().getSimpleName());
			return null;
    	}
    	
		try {
			return (T) field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    public static Field findField(Class<?> objClass, Class<?> fieldClass)
    {
    	Field field = null;
    	for (CacheEntry entry : ObjectFieldCache)
    	{
    		if (entry.objClass == objClass && entry.fieldClass == fieldClass)
    		{
    			field = entry.field;
    		}
    	}
    	
    	if (field == null)
    	{
    		if (objClass.getSuperclass() != null) {
    			field = findField(objClass.getSuperclass(), fieldClass);
    		}
        	for (Field f : objClass.getDeclaredFields())
        	{
        		if (f.getType() == fieldClass)
        		{
        			if (field != null) // at least two fields
        			{
        				Logger.severe("ReflectionUtils: Couldn't choose between " + fieldClass.getSimpleName() + " in " + objClass.getSimpleName());
    					return null;
        			}
        			field = f;
        		}
        	}
    		if (field == null) // no fields
    		{
    			return null;
    		}
    		field.setAccessible(true);
        	ObjectFieldCache.add(new CacheEntry(objClass, fieldClass, field));
    	}
    	
    	return field;
    }
	
	public static Method findMethod(Class<?> objClass, Class<?> returnClass, Class<?>... argClasses) {
		Method method = null;
    	
		if (returnClass == null) {
			returnClass = Void.TYPE;
		}
		
    	if (method == null)
    	{
    		if (objClass.getSuperclass() != null) {
    			method = findMethod(objClass.getSuperclass(), returnClass, argClasses);
    		}
        	for (Method m : objClass.getDeclaredMethods())
        	{
        		if (!m.getReturnType().equals(returnClass)) continue;
        		
        		Class<?>[] methodParameters = m.getParameterTypes();
        		if (methodParameters.length != argClasses.length) continue;
        		
        		boolean match = true;
        		for (int i = 0; i < argClasses.length; i++) {
        			if (!methodParameters[i].equals(argClasses[i])) {
        				match = false;
        				break;
        			}
        		}
        		if (!match) continue;
        		
    			if (method != null) // at least two such methods
    			{
    				//Logger.severe("ReflectionUtils: Couldn't choose between " + method.getName() + " and " + m.getName() + " in " + objClass.getCanonicalName());
					//return null;
    				Logger.severe("ReflectionUtils: choose " + method.getName() + " over " + m.getName() + " in " + objClass.getCanonicalName());
    			}
    			method = m;
        	}
    		if (method == null) // no methods
    		{
    			return null;
    		}
    		method.setAccessible(true);
    	}
    	
    	return method;
	}

	public static void printAllFields(Class<?> clazz) {
		Logger.severe(clazz.getName() + " has " + clazz.getDeclaredFields().length + " fields:");
    	for (Field f : clazz.getDeclaredFields())
    	{
    		Logger.severe("  " + f.getType().getName() + " " + f.getName());
    	}
    	if (clazz.getSuperclass() != null) {
    		printAllFields(clazz.getSuperclass());
        }
	}
}
