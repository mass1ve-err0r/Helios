package software.baig.helios.utilities;

import java.util.Optional;
import java.util.function.Function;


public class EnvironmentUtil {
	
	public static <T> T getSystemProperty(String key, T defaultValue, Function<String, T> mappingFunction) {
		return Optional.ofNullable(System.getProperty(key))
				.map(mappingFunction)
				.orElse(defaultValue);
	}
	
	public static <T> String getSystemProperty(String key, String defaultValue) {
		return getSystemProperty(key, defaultValue, Function.identity());
	}
	
	public static String getSystemEnv(String key, String defaultValue) {
		final String env = System.getenv(key);
		return env == null ? defaultValue : env;
	}

	public static boolean getSystemEnv(String key, boolean defaultValue) {
		final String env = System.getenv(key);
		return env == null ? defaultValue : Boolean.parseBoolean(env);
	}

}
