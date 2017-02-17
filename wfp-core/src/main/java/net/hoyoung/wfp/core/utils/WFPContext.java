package net.hoyoung.wfp.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class WFPContext {

	private static Properties p = new Properties();
	static {
		InputStream is = WFPContext.class.getClassLoader().getResourceAsStream("config.properties");
		try {
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getProperty(String key) {
		return p.getProperty(key);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getProperty(String key, Class<T> z) {
		String property = p.getProperty(key);
		if (z == String.class) {
			return (T) property;
		}
		try {
			Method method = z.getMethod("valueOf", String.class);
			Object rs = method.invoke(null, property);
			return (T) rs;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String port = WFPContext.getProperty("redis.host", String.class);
		System.out.println(port);
	}
}
