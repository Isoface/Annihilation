package com.hotmail.AdrianSRJose.AnniPro.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.util.RandomUtils;
import com.hotmail.AdrianSR.core.util.version.ServerVersion;

/**
 * <b>ReflectionUtils</b>
 * <p>
 * This class provides useful methods which makes dealing with reflection much
 * easier, especially when working with Bukkit
 * <p>
 * You are welcome to use it, modify it and redistribute it under the following
 * conditions:
 * <ul>
 * <li>Don't claim this class as your own
 * <li>Don't remove this disclaimer
 * </ul>
 * <p>
 * <i>It would be nice if you provide credit to me if you use this class in a
 * published project</i>
 * 
 * @author DarkBlade12
 * @version 1.1
 */
public final class ReflectionUtils {
	// Prevent accidental construction
	private ReflectionUtils() {
	}

	/**
	 * Returns the constructor of a given class with the given parameter types
	 * 
	 * @param clazz
	 *            Target class
	 * @param parameterTypes
	 *            Parameter types of the desired constructor
	 * @return The constructor of the target class with the specified parameter
	 *         types
	 * @throws NoSuchMethodException
	 *             If the desired constructor with the specified parameter types
	 *             cannot be found
	 * @see DataType
	 * @see DataType#getPrimitive(Class[])
	 * @see DataType#compare(Class[], Class[])
	 */
	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
		for (Constructor<?> constructor : clazz.getConstructors()) {
			if (!DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
				continue;
			}
			return constructor;
		}
		throw new NoSuchMethodException(
				"There is no such constructor in this class with the specified parameter types");
	}

	/**
	 * Returns the constructor of a desired class with the given parameter types
	 * 
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param parameterTypes
	 *            Parameter types of the desired constructor
	 * @return The constructor of the desired target class with the specified
	 *         parameter types
	 * @throws NoSuchMethodException
	 *             If the desired constructor with the specified parameter types
	 *             cannot be found
	 * @throws ClassNotFoundException
	 *             ClassNotFoundException If the desired target class with the
	 *             specified name and package cannot be found
	 * @see #getClass(String, PackageType)
	 * @see #getConstructor(Class, Class...)
	 */
	public static Constructor<?> getConstructor(String className, PackageType packageType, Class<?>... parameterTypes)
			throws NoSuchMethodException, ClassNotFoundException {
		return getConstructor(packageType.getClass(className), parameterTypes);
	}

	/**
	 * Returns an instance of a class with the given arguments
	 * 
	 * @param clazz
	 *            Target class
	 * @param arguments
	 *            Arguments which are used to construct an object of the target
	 *            class
	 * @return The instance of the target class with the specified arguments
	 * @throws InstantiationException
	 *             If you cannot create an instance of the target class due to
	 *             certain circumstances
	 * @throws IllegalAccessException
	 *             If the desired constructor cannot be accessed due to certain
	 *             circumstances
	 * @throws IllegalArgumentException
	 *             If the types of the arguments do not match the parameter types of
	 *             the constructor (this should not occur since it searches for a
	 *             constructor with the types of the arguments)
	 * @throws InvocationTargetException
	 *             If the desired constructor cannot be invoked
	 * @throws NoSuchMethodException
	 *             If the desired constructor with the specified arguments cannot be
	 *             found
	 */
	public static Object instantiateObject(Class<?> clazz, Object... arguments) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		return getConstructor(clazz, DataType.getPrimitive(arguments)).newInstance(arguments);
	}

	/**
	 * Returns an instance of a desired class with the given arguments
	 * 
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param arguments
	 *            Arguments which are used to construct an object of the desired
	 *            target class
	 * @return The instance of the desired target class with the specified arguments
	 * @throws InstantiationException
	 *             If you cannot create an instance of the desired target class due
	 *             to certain circumstances
	 * @throws IllegalAccessException
	 *             If the desired constructor cannot be accessed due to certain
	 *             circumstances
	 * @throws IllegalArgumentException
	 *             If the types of the arguments do not match the parameter types of
	 *             the constructor (this should not occur since it searches for a
	 *             constructor with the types of the arguments)
	 * @throws InvocationTargetException
	 *             If the desired constructor cannot be invoked
	 * @throws NoSuchMethodException
	 *             If the desired constructor with the specified arguments cannot be
	 *             found
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #getClass(String, PackageType)
	 * @see #instantiateObject(Class, Object...)
	 */
	public static Object instantiateObject(String className, PackageType packageType, Object... arguments)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		return instantiateObject(packageType.getClass(className), arguments);
	}

	/**
	 * Returns a method of a class with the given parameter types
	 * 
	 * @param clazz
	 *            Target class
	 * @param methodName
	 *            Name of the desired method
	 * @param parameterTypes
	 *            Parameter types of the desired method
	 * @return The method of the target class with the specified name and parameter
	 *         types
	 * @throws NoSuchMethodException
	 *             If the desired method of the target class with the specified name
	 *             and parameter types cannot be found
	 * @see DataType#getPrimitive(Class[])
	 * @see DataType#compare(Class[], Class[])
	 */
	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
		for (Method method : clazz.getMethods()) {
			if (!method.getName().equals(methodName)
					|| !DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)) {
				continue;
			}
			return method;
		}
		throw new NoSuchMethodException(
				"There is no such method in this class with the specified name and parameter types");
	}

	/**
	 * Returns a method of a desired class with the given parameter types
	 * 
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param methodName
	 *            Name of the desired method
	 * @param parameterTypes
	 *            Parameter types of the desired method
	 * @return The method of the desired target class with the specified name and
	 *         parameter types
	 * @throws NoSuchMethodException
	 *             If the desired method of the desired target class with the
	 *             specified name and parameter types cannot be found
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #getClass(String, PackageType)
	 * @see #getMethod(Class, String, Class...)
	 */
	public static Method getMethod(String className, PackageType packageType, String methodName,
			Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
		return getMethod(packageType.getClass(className), methodName, parameterTypes);
	}

	/**
	 * Invokes a method on an object with the given arguments
	 * 
	 * @param instance
	 *            Target object
	 * @param methodName
	 *            Name of the desired method
	 * @param arguments
	 *            Arguments which are used to invoke the desired method
	 * @return The result of invoking the desired method on the target object
	 * @throws IllegalAccessException
	 *             If the desired method cannot be accessed due to certain
	 *             circumstances
	 * @throws IllegalArgumentException
	 *             If the types of the arguments do not match the parameter types of
	 *             the method (this should not occur since it searches for a method
	 *             with the types of the arguments)
	 * @throws InvocationTargetException
	 *             If the desired method cannot be invoked on the target object
	 * @throws NoSuchMethodException
	 *             If the desired method of the class of the target object with the
	 *             specified name and arguments cannot be found
	 * @see #getMethod(Class, String, Class...)
	 * @see DataType#getPrimitive(Object[])
	 */
	public static Object invokeMethod(Object instance, String methodName, Object... arguments)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		return getMethod(instance.getClass(), methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
	}

	/**
	 * Invokes a method of the target class on an object with the given arguments
	 * 
	 * @param instance
	 *            Target object
	 * @param clazz
	 *            Target class
	 * @param methodName
	 *            Name of the desired method
	 * @param arguments
	 *            Arguments which are used to invoke the desired method
	 * @return The result of invoking the desired method on the target object
	 * @throws IllegalAccessException
	 *             If the desired method cannot be accessed due to certain
	 *             circumstances
	 * @throws IllegalArgumentException
	 *             If the types of the arguments do not match the parameter types of
	 *             the method (this should not occur since it searches for a method
	 *             with the types of the arguments)
	 * @throws InvocationTargetException
	 *             If the desired method cannot be invoked on the target object
	 * @throws NoSuchMethodException
	 *             If the desired method of the target class with the specified name
	 *             and arguments cannot be found
	 * @see #getMethod(Class, String, Class...)
	 * @see DataType#getPrimitive(Object[])
	 */
	public static Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object... arguments)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		return getMethod(clazz, methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
	}

	/**
	 * Invokes a method of a desired class on an object with the given arguments
	 * 
	 * @param instance
	 *            Target object
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param methodName
	 *            Name of the desired method
	 * @param arguments
	 *            Arguments which are used to invoke the desired method
	 * @return The result of invoking the desired method on the target object
	 * @throws IllegalAccessException
	 *             If the desired method cannot be accessed due to certain
	 *             circumstances
	 * @throws IllegalArgumentException
	 *             If the types of the arguments do not match the parameter types of
	 *             the method (this should not occur since it searches for a method
	 *             with the types of the arguments)
	 * @throws InvocationTargetException
	 *             If the desired method cannot be invoked on the target object
	 * @throws NoSuchMethodException
	 *             If the desired method of the desired target class with the
	 *             specified name and arguments cannot be found
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #getClass(String, PackageType)
	 * @see #invokeMethod(Object, Class, String, Object...)
	 */
	public static Object invokeMethod(Object instance, String className, PackageType packageType, String methodName,
			Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		return invokeMethod(instance, packageType.getClass(className), methodName, arguments);
	}

	/**
	 * Returns a field of the target class with the given name
	 * 
	 * @param clazz
	 *            Target class
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @return The field of the target class with the specified name
	 * @throws NoSuchFieldException
	 *             If the desired field of the given class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 */
	public static Field getField(Class<?> clazz, boolean declared, String fieldName)
			throws NoSuchFieldException, SecurityException {
		Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
		field.setAccessible(true);
		return field;
	}

	/**
	 * Returns a field of a desired class with the given name
	 * 
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @return The field of the desired target class with the specified name
	 * @throws NoSuchFieldException
	 *             If the desired field of the desired class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #getField(Class, boolean, String)
	 */
	public static Field getField(String className, PackageType packageType, boolean declared, String fieldName)
			throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		return getField(packageType.getClass(className), declared, fieldName);
	}

	/**
	 * Returns the value of a field of the given class of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param clazz
	 *            Target class
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @return The value of field of the target object
	 * @throws IllegalArgumentException
	 *             If the target object does not feature the desired field
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the target class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @see #getField(Class, boolean, String)
	 */
	public static Object getValue(Object instance, Class<?> clazz, boolean declared, String fieldName)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		return getField(clazz, declared, fieldName).get(instance);
	}

	/**
	 * Returns the value of a field of a desired class of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @return The value of field of the target object
	 * @throws IllegalArgumentException
	 *             If the target object does not feature the desired field
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the desired class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #getValue(Object, Class, boolean, String)
	 */
	public static Object getValue(Object instance, String className, PackageType packageType, boolean declared,
			String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
			SecurityException, ClassNotFoundException {
		return getValue(instance, packageType.getClass(className), declared, fieldName);
	}

	/**
	 * Returns the value of a field with the given name of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @return The value of field of the target object
	 * @throws IllegalArgumentException
	 *             If the target object does not feature the desired field (should
	 *             not occur since it searches for a field with the given name in
	 *             the class of the object)
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the target object cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @see #getValue(Object, Class, boolean, String)
	 */
	public static Object getValue(Object instance, boolean declared, String fieldName)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		return getValue(instance, instance.getClass(), declared, fieldName);
	}

	/**
	 * Sets the value of a field of the given class of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param clazz
	 *            Target class
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @param value
	 *            New value
	 * @throws IllegalArgumentException
	 *             If the type of the value does not match the type of the desired
	 *             field
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the target class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @see #getField(Class, boolean, String)
	 */
	public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		getField(clazz, declared, fieldName).set(instance, value);
	}

	/**
	 * Sets the value of a field of a desired class of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @param value
	 *            New value
	 * @throws IllegalArgumentException
	 *             If the type of the value does not match the type of the desired
	 *             field
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the desired class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #setValue(Object, Class, boolean, String, Object)
	 */
	public static void setValue(Object instance, String className, PackageType packageType, boolean declared,
			String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException, ClassNotFoundException {
		setValue(instance, packageType.getClass(className), declared, fieldName, value);
	}

	/**
	 * Sets the value of a field with the given name of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @param value
	 *            New value
	 * @throws IllegalArgumentException
	 *             If the type of the value does not match the type of the desired
	 *             field
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the target object cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @see #setValue(Object, Class, boolean, String, Object)
	 */
	public static void setValue(Object instance, boolean declared, String fieldName, Object value)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		setValue(instance, instance.getClass(), declared, fieldName, value);
	}

	/**
	 * Represents an enumeration of dynamic packages of NMS and CraftBukkit
	 * <p>
	 * This class is part of the <b>ReflectionUtils</b> and follows the same usage
	 * conditions
	 * 
	 * @author DarkBlade12
	 * @since 1.0
	 */
	public enum PackageType {
		MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()), CRAFTBUKKIT("org.bukkit.craftbukkit."
				+ getServerVersion()), CRAFTBUKKIT_BLOCK(CRAFTBUKKIT, "block"), CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT,
						"chunkio"), CRAFTBUKKIT_COMMAND(CRAFTBUKKIT, "command"), CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT,
								"conversations"), CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT,
										"enchantments"), CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity"), CRAFTBUKKIT_EVENT(
												CRAFTBUKKIT, "event"), CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT,
														"generator"), CRAFTBUKKIT_HELP(CRAFTBUKKIT,
																"help"), CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT,
																		"inventory"), CRAFTBUKKIT_MAP(CRAFTBUKKIT,
																				"map"), CRAFTBUKKIT_METADATA(
																						CRAFTBUKKIT,
																						"metadata"), CRAFTBUKKIT_POTION(
																								CRAFTBUKKIT,
																								"potion"), CRAFTBUKKIT_PROJECTILES(
																										CRAFTBUKKIT,
																										"projectiles"), CRAFTBUKKIT_SCHEDULER(
																												CRAFTBUKKIT,
																												"scheduler"), CRAFTBUKKIT_SCOREBOARD(
																														CRAFTBUKKIT,
																														"scoreboard"), CRAFTBUKKIT_UPDATER(
																																CRAFTBUKKIT,
																																"updater"), CRAFTBUKKIT_UTIL(
																																		CRAFTBUKKIT,
																																		"util");

		private final String path;

		/**
		 * Construct a new package type
		 * 
		 * @param path
		 *            Path of the package
		 */
		private PackageType(String path) {
			this.path = path;
		}

		/**
		 * Construct a new package type
		 * 
		 * @param parent
		 *            Parent package of the package
		 * @param path
		 *            Path of the package
		 */
		private PackageType(PackageType parent, String path) {
			this(parent + "." + path);
		}

		/**
		 * Returns the path of this package type
		 * 
		 * @return The path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * Returns the class with the given name
		 * 
		 * @param className
		 *            Name of the desired class
		 * @return The class with the specified name
		 * @throws ClassNotFoundException
		 *             If the desired class with the specified name and package cannot
		 *             be found
		 */
		public Class<?> getClass(String className) throws ClassNotFoundException {
			return Class.forName(this + "." + className);
		}

		// Override for convenience
		@Override
		public String toString() {
			return path;
		}

		/**
		 * Returns the version of your server
		 * 
		 * @return The server version
		 */
		public static String getServerVersion() {
			return Bukkit.getServer().getClass().getPackage().getName().substring(23);
		}
	}

	/**
	 * Represents an enumeration of Java data types with corresponding classes
	 * <p>
	 * This class is part of the <b>ReflectionUtils</b> and follows the same usage
	 * conditions
	 * 
	 * @author DarkBlade12
	 * @since 1.0
	 */
	public enum DataType {
		BYTE(byte.class, Byte.class), SHORT(short.class, Short.class), INTEGER(int.class, Integer.class), LONG(
				long.class, Long.class), CHARACTER(char.class, Character.class), FLOAT(float.class,
						Float.class), DOUBLE(double.class, Double.class), BOOLEAN(boolean.class, Boolean.class);

		private static final Map<Class<?>, DataType> CLASS_MAP = new HashMap<Class<?>, DataType>();
		private final Class<?> primitive;
		private final Class<?> reference;

		// Initialize map for quick class lookup
		static {
			for (DataType type : values()) {
				CLASS_MAP.put(type.primitive, type);
				CLASS_MAP.put(type.reference, type);
			}
		}

		/**
		 * Construct a new data type
		 * 
		 * @param primitive
		 *            Primitive class of this data type
		 * @param reference
		 *            Reference class of this data type
		 */
		private DataType(Class<?> primitive, Class<?> reference) {
			this.primitive = primitive;
			this.reference = reference;
		}

		/**
		 * Returns the primitive class of this data type
		 * 
		 * @return The primitive class
		 */
		public Class<?> getPrimitive() {
			return primitive;
		}

		/**
		 * Returns the reference class of this data type
		 * 
		 * @return The reference class
		 */
		public Class<?> getReference() {
			return reference;
		}

		/**
		 * Returns the data type with the given primitive/reference class
		 * 
		 * @param clazz
		 *            Primitive/Reference class of the data type
		 * @return The data type
		 */
		public static DataType fromClass(Class<?> clazz) {
			return CLASS_MAP.get(clazz);
		}

		/**
		 * Returns the primitive class of the data type with the given reference class
		 * 
		 * @param clazz
		 *            Reference class of the data type
		 * @return The primitive class
		 */
		public static Class<?> getPrimitive(Class<?> clazz) {
			DataType type = fromClass(clazz);
			return type == null ? clazz : type.getPrimitive();
		}

		/**
		 * Returns the reference class of the data type with the given primitive class
		 * 
		 * @param clazz
		 *            Primitive class of the data type
		 * @return The reference class
		 */
		public static Class<?> getReference(Class<?> clazz) {
			DataType type = fromClass(clazz);
			return type == null ? clazz : type.getReference();
		}

		/**
		 * Returns the primitive class array of the given class array
		 * 
		 * @param classes
		 *            Given class array
		 * @return The primitive class array
		 */
		public static Class<?>[] getPrimitive(Class<?>[] classes) {
			int length = classes == null ? 0 : classes.length;
			Class<?>[] types = new Class<?>[length];
			for (int index = 0; index < length; index++) {
				types[index] = getPrimitive(classes[index]);
			}
			return types;
		}

		/**
		 * Returns the reference class array of the given class array
		 * 
		 * @param classes
		 *            Given class array
		 * @return The reference class array
		 */
		public static Class<?>[] getReference(Class<?>[] classes) {
			int length = classes == null ? 0 : classes.length;
			Class<?>[] types = new Class<?>[length];
			for (int index = 0; index < length; index++) {
				types[index] = getReference(classes[index]);
			}
			return types;
		}

		/**
		 * Returns the primitive class array of the given object array
		 * 
		 * @param object
		 *            Given object array
		 * @return The primitive class array
		 */
		public static Class<?>[] getPrimitive(Object[] objects) {
			int length = objects == null ? 0 : objects.length;
			Class<?>[] types = new Class<?>[length];
			for (int index = 0; index < length; index++) {
				types[index] = getPrimitive(objects[index].getClass());
			}
			return types;
		}

		/**
		 * Returns the reference class array of the given object array
		 * 
		 * @param object
		 *            Given object array
		 * @return The reference class array
		 */
		public static Class<?>[] getReference(Object[] objects) {
			int length = objects == null ? 0 : objects.length;
			Class<?>[] types = new Class<?>[length];
			for (int index = 0; index < length; index++) {
				types[index] = getReference(objects[index].getClass());
			}
			return types;
		}

		/**
		 * Compares two class arrays on equivalence
		 * 
		 * @param primary
		 *            Primary class array
		 * @param secondary
		 *            Class array which is compared to the primary array
		 * @return Whether these arrays are equal or not
		 */
		public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
			if (primary == null || secondary == null || primary.length != secondary.length) {
				return false;
			}
			for (int index = 0; index < primary.length; index++) {
				Class<?> primaryClass = primary[index];
				Class<?> secondaryClass = secondary[index];
				if (primaryClass.equals(secondaryClass) || primaryClass.isAssignableFrom(secondaryClass)) {
					continue;
				}
				return false;
			}
			return true;
		}
	}

	public static void sendPacket(Player p, Object packet) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException, ClassNotFoundException {
		Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
		Object plrConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
		plrConnection.getClass().getMethod("sendPacket", Util.getCraftClass("Packet")).invoke(plrConnection, packet);
	}

	public static Class<?> getCraftClass(String className) {
		try {
			return Class.forName("net.minecraft.server." + ServerVersion.getVersion().name() + "." + className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Class reflection utils.
	 */
	// CRAFT:
	public static final String CRAFT_PREFIX = "org.bukkit.craftbukkit.";
	public static Class<?> CRAFT_ENTITY;      // 'CraftEntity' class.
	public static Method CRAFT_ENTITY_HANDLE; // 'getHandle()', 'CraftEntity' class, method.
	// NMS:
	public static final String NMS_PREFIX = "net.minecraft.server.";
	public static Class<?> NMS_ENTITY;                 // nms 'Entity' class.
	public static Field NMS_ENTITY_NOCLIP;             // nms 'Entity' class, 'noclip' field.
	public static Method NMS_ENTITY_SILENT;            // nms 'Entity' class, set silent method.
	public static Method NMS_ENTITY_IS_SILENT;         // nms 'Entity' class, is silent method.
	public static Class<?> NMS_ARMORSTAND;             // nms 'EntityAmorStand' class.
	public static Field NMS_ARMORSTAND_FIELD_H;        // nms 'EntityAmorStand' class, 'h' field.
	public static Class < ? > ENTITY_HUMAN_CLASS;      // nms 'EntityHuman' class.
	public static Field HUMAN_ENTITY_ACTIVE_CONTAINER; // nms 'EntityHuman' class, 'activeContainer' field.
	public static Class < ? > CONTAINER_ENCHANT_TABLE_CLASS; // nms 'ContainerEnchantTable' class.
	public static Field ENCHANT_CONTAINER_SEED_FIELD; // nms 'ContainerEnchantTable', 'f' field (container seed).
	// SPIGOT VERSION
	public static String VERSION;
	
	/**
	 * Check reflection is initialized.
	 */
	public static void checkIsInitializeReflection() {
		// check is not already initialized.
		if (NMS_ARMORSTAND != null) {
			return;
		}

		try {
			// get version.
			VERSION = ServerVersion.getVersion().name();

			// CRAFT:
			// get 'CraftEntity' class.
			CRAFT_ENTITY = Class.forName(CRAFT_PREFIX + VERSION + ".entity.CraftEntity");

			// get 'getHandle' Method getter.
			CRAFT_ENTITY_HANDLE = CRAFT_ENTITY.getMethod("getHandle", new Class[0]);
			
			// NMS: 
			// get 'Entity' class.
			NMS_ENTITY = Class.forName(NMS_PREFIX + VERSION + ".Entity");
			
			// get Field 'noclip'
			NMS_ENTITY_NOCLIP = NMS_ENTITY.getField("noclip");
			
			// get methods 'NMS_ENTITY_SILENT' and 'NMS_ENTITY_IS_SILENT'.
			{
				// get method dependig of spigot version.
				switch (VERSION) {
				case "v1_8_R1":
				case "v1_8_R2":
				case "v1_8_R3":
					// get a method called 'b'
					NMS_ENTITY_SILENT = NMS_ENTITY.getMethod("b", boolean.class);
					
					// get a method called 'R'
					NMS_ENTITY_IS_SILENT = NMS_ENTITY.getMethod("R");
					break;
					
				case "v1_9_R1":
				case "v1_9_R2":
					// get a method called 'c'
					NMS_ENTITY_SILENT = NMS_ENTITY.getMethod("b", boolean.class);
					
					// get a method called 'ad'
					NMS_ENTITY_IS_SILENT = NMS_ENTITY.getMethod("ad");
					break;
					
				default:
					// set null.
					NMS_ENTITY_SILENT    = null;
					NMS_ENTITY_IS_SILENT = null;
					break;
				}
			}
			
			// get 'EntityArmorStand' class.
			NMS_ARMORSTAND = Class.forName(NMS_PREFIX + VERSION + ".EntityArmorStand");

			// get Field 'h' in the 'EntityArmorStand' class.
			{
				// get field name.
				String h_field_name = "h";

				// get field name depeding of spigot version.
				switch (VERSION) {
				case "v1_9_R1":
					h_field_name = "by";
					break;

				case "v1_9_R2":
					h_field_name = "bz";
					break;

				case "v1_10_R1":
					h_field_name = "bA";
					break;

				case "v1_11_R1":
					h_field_name = "bz";
					break;

				case "v1_12_R1":
					h_field_name = "bA";
					break;
				}

				// get field.
				NMS_ARMORSTAND_FIELD_H = NMS_ARMORSTAND.getDeclaredField(h_field_name);
			}
			
			// get 'activeContainer' field from 'EntityHuman' class.
			ENTITY_HUMAN_CLASS            = Class.forName ( NMS_PREFIX + VERSION + ".EntityHuman" );
			HUMAN_ENTITY_ACTIVE_CONTAINER = ENTITY_HUMAN_CLASS.getField ( "activeContainer" );
			
			// get 'ContainerEnchantTable' class.
			CONTAINER_ENCHANT_TABLE_CLASS = Class.forName ( NMS_PREFIX + VERSION + ".ContainerEnchantTable" );
			
			// get 'f' field from 'ContainerEnchantTable' class (container seed).
			{
				String f_field_name = "f";
				
				switch ( VERSION ) {
				default:
					f_field_name = "f";
					break;
				}
				
				ENCHANT_CONTAINER_SEED_FIELD = CONTAINER_ENCHANT_TABLE_CLASS.getField ( f_field_name );
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Set an Entity location using the NMS method.
	 * <p>
	 * @param entity the {@link Entity} to set.
	 * @param x The new x-coordinate.
	 * @param y The new y-coordinate.
	 * @param z The new z-coordinate.
	 * @param yaw The absolute rotation on the x-plane, in degrees.
	 * @param pitch pitch The absolute rotation on the y-plane, in degrees.
	 */
	public static void setLocation(final Entity ent, double x, double y, double z, float yaw, float pitch) {
		// check reflection.
		checkIsInitializeReflection();
		
		// set location.
		try {
			// get set location method.
			Method NMS_ENTITY_SET_LOCATION = NMS_ENTITY.getMethod("setLocation",
					new Class[] { double.class, double.class, double.class, float.class, float.class });

			// get handle
			Object handle = NMS_ENTITY
					.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(ent)));

			// invoke method.
			NMS_ENTITY_SET_LOCATION.invoke(handle, x, y, z, yaw, pitch);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Sets whether the {@link Entity}
	 * should be visible or not.
	 * <p>
	 * @param entity The entity to set.
	 * @param tag - whether the entity is visible or not.
	 */
	public static void setVisible(final Entity entity, boolean tag) {
		// check reflection.
		checkIsInitializeReflection();
		
		// change visibility.
		try {
			// get set invisible method.
			Method NMS_ENTITY_SET_INVISIBLE = NMS_ENTITY.getMethod("setInvisible", boolean.class);

			// get handle
			Object handle = NMS_ENTITY
					.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity)));

			// invoke method.
			NMS_ENTITY_SET_INVISIBLE.invoke(handle, !tag);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Returns whether the {@link Entity}
	 * should be visible or not.
	 * <p>
	 * @param entity The entity to check.
	 * @return - whether the entity is visible or not.
	 */
	public static boolean isVisible(final Entity entity) {
		// check reflection.
		checkIsInitializeReflection();
		
		// get visibility.
		try {
			// get set location method.
			Method NMS_ENTITY_IS_INVISIBLE = NMS_ENTITY.getMethod("isInvisible");

			// get handle
			Object handle = NMS_ENTITY
					.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity)));

			// invoke method.
			return !((boolean)NMS_ENTITY_IS_INVISIBLE.invoke(handle));
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Sets whether the {@link Entity} 
	 * is silent or not.
	 * When an entity is silent it 
	 * will not produce any sound.
	 * <p>
	 * @param entity The entity to set.
	 * @param silent if the entity is silent.
	 */
	public static void setSilent(final Entity entity, final boolean silent) {
		// check reflection.
		checkIsInitializeReflection();
		
		// invoke method.
		if (NMS_ENTITY_SILENT == null) { // invoke methdo in normal API.
			entity.setSilent(silent);
		} else { // invoke method in the entity handle.
			try {
				// get handle
				Object handle = NMS_ENTITY
						.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity)));
				
				// invoke method.
				NMS_ENTITY_SILENT.invoke(handle, silent);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	/**
	 * Sets whether an entity 
	 * will have AI.
	 * <p>
	 * @param ent the LivingEntity to set.
	 * @param tag whether the entity will have AI or not.
	 */
	public static void setAI(final LivingEntity ent, final boolean tag) {
		// check is a LivingEntity.
		if (!(ent instanceof LivingEntity)) {
			throw new IllegalArgumentException("Invalid LivingEntity, This entity not is a LivingEntity");
		}
		
		// check reflection.
		checkIsInitializeReflection();
		
		// set 'AI' as tag in a new NBTTagCompound.
		try {
			// get handle.
			Object handle = ent.getClass().getDeclaredMethod("getHandle").invoke(ent);
			
			// get NBTTagCompound.
			final Object ntb = Class.forName(NMS_PREFIX + VERSION + ".NBTTagCompound").getConstructor().newInstance();
			
			// get and invoke method 'c' in handle.
			Method c = handle.getClass().getMethod("c", ntb.getClass());
			c.invoke(handle, ntb);
			
			// get and invoke method 'setInt' in NBTTagCompound.
			Method setInt = ntb.getClass().getMethod("setInt", String.class, int.class);
			setInt.invoke(ntb, "NoAI", (tag ? 0 : 1));
			
			// get and invoke method 'f' in handle.
			Method f = handle.getClass().getMethod("f", ntb.getClass());
			f.invoke(handle, ntb);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Get an Entity location from his 
	 * location in his NMS Class.
	 * <p>
	 * @param entity the {@link Entity} to get.
	 * @return the current Entity location.
	 */
	public static Location getLocation(final Entity entity) {
		// check reflection.
		checkIsInitializeReflection();
		
		try {
			// get methods.
			Field LOC_X     = NMS_ENTITY.getField("locX");
			Field LOC_Y     = NMS_ENTITY.getField("locY");
			Field LOC_Z     = NMS_ENTITY.getField("locZ");
			Field LOC_YAW   = NMS_ENTITY.getField("yaw");
			Field LOC_PITCH = NMS_ENTITY.getField("pitch");

			// get handle
			Object handle = NMS_ENTITY
					.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity)));

			// return location.
			return new Location(entity.getWorld(), LOC_X.getDouble(handle), LOC_Y.getDouble(handle),
					LOC_Z.getDouble(handle), LOC_YAW.getFloat(handle), LOC_PITCH.getFloat(handle));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	/**
	 * Set armor stand damagable 
	 * and clickable when its
	 * invisible.
	 * <p>
	 * @param armorStand the {@link ArmorStand} 
	 * to set damagable when is invisible.
	 */
	public static void h(final ArmorStand armorStand) {
		// check reflection.
		checkIsInitializeReflection();
		
		// change the Field 'h' value.
		try {
			// get handle
			Object handle = NMS_ARMORSTAND
					.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(armorStand)));

			// set.
			NMS_ARMORSTAND_FIELD_H.setAccessible(true);
			NMS_ARMORSTAND_FIELD_H.setBoolean(handle, false);
			NMS_ARMORSTAND_FIELD_H.setAccessible(false);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Gets whether the {@link Entity} 
	 * is silent or not.
	 * <p>
	 * @param entity The Entity to check.
	 * @return whether the entity is silent.
	 */
	public static boolean isSilent(final Entity entity) {
		// check reflection.
		checkIsInitializeReflection();
		
		// get boolean invoking method.
		if (NMS_ENTITY_IS_SILENT == null) { // invoke methdo in normal API.
			return entity.isSilent();
		} else { // invoke method in the entity handle.
			try {
				// get handle
				Object handle = NMS_ENTITY
						.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity)));
				
				// invoke method.
				return (boolean) NMS_ENTITY_IS_SILENT.invoke(handle);
			} catch(Throwable t) {
				t.printStackTrace();
				return false;
			}
		}
	}
	
	/**
	 * Gets player open container.
	 * <p>
	 * @param player the player to get from.
	 * @return the open container, or null if no container is open.
	 */
	public static Object getOpenContainer ( Player player ) {
		checkIsInitializeReflection ( );
		
		try {
			return HUMAN_ENTITY_ACTIVE_CONTAINER.get ( player.getClass ( ).getMethod ( "getHandle" ).invoke ( player ) );
		} catch ( IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| SecurityException ex ) {
			ex.printStackTrace ( );
			return null;
		}
	}
	
	/**
	 * Reset enchantment table enchantment offers.
	 * <p>
	 * @param container the enchantment table container.
	 */
	public static void resetOffers ( Object container ) {
		checkIsInitializeReflection ( );
		
		if ( CONTAINER_ENCHANT_TABLE_CLASS.isAssignableFrom ( container.getClass ( ) ) ) {
			try {
				boolean accessible = ENCHANT_CONTAINER_SEED_FIELD.isAccessible ( );
				
				ENCHANT_CONTAINER_SEED_FIELD.setAccessible ( true );
				ENCHANT_CONTAINER_SEED_FIELD.set ( container , RandomUtils.RANDOM.nextInt ( ) );
				ENCHANT_CONTAINER_SEED_FIELD.setAccessible ( accessible );
			} catch ( IllegalArgumentException | IllegalAccessException ex ) {
				ex.printStackTrace ( );
			}
		} else {
			throw new ClassCastException ( 
					"the provided container is not a valid instance of " + CONTAINER_ENCHANT_TABLE_CLASS.getName ( ) );
		}
	}
	
	/**
	 * Play a 3D named sound.
	 * <p>
	 * @param location the location.
	 * @param sound the sound name to play.
	 * @param volume the volume.
	 * @param pitch the pitch.
	 */
	public static void play3DNamedSound(final Location location, final String sound, final float volume, final float pitch) {
		// play 3D sound.
		try {
			// check location and sound.
			if (location == null || sound == null) {
				return;
			}

			// get location data.
			final World w  = location.getWorld();
			final double x = location.getX();
			final double y = location.getY();
			final double z = location.getZ();

			// get world handle.
			final Field wf = w.getClass().getDeclaredField("world");
			wf.setAccessible(true);
			final Object world_server = wf.get(w);

			// get packet constructor.
			final Constructor<?> packet_constructor = ReflectionUtils.getCraftClass("PacketPlayOutNamedSoundEffect")
					.getConstructor(String.class, double.class, double.class, double.class, float.class, float.class);

			// make packet.
			final Object packet = packet_constructor.newInstance(sound, x, y, z, volume, pitch);

			// get minecraft server.
			final Method mmc_server = world_server.getClass().getDeclaredMethod("getMinecraftServer");
			final Object mc_server  = mmc_server.invoke(world_server);

			// get minecraft server player list.
			final Method mp_list = mc_server.getClass().getMethod("getPlayerList");
			final Object pl_list = mp_list.invoke(mc_server);

			// get packet class.
			final Class<?> packet_class = ReflectionUtils.getCraftClass("Packet");

			// get dimension.
			final int dimension = world_server.getClass().getField("dimension").getInt(world_server);

			// get and invoke, send packet to nearby players method.
			final Method msend_packet = pl_list.getClass().getMethod("sendPacketNearby", double.class, double.class,
					double.class, double.class, int.class, packet_class);
			msend_packet.invoke(pl_list, x, y, z, (volume > 1.0F ? 16.0F * volume : 16.0D), dimension, packet);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}