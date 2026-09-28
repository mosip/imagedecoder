package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * Exercises equals/hashCode/toString (and no-arg construction) for all model
 * classes so Lombok-generated methods count toward JaCoCo when model/ is not
 * excluded.
 */
class ModelCoverageTest {

	@Test
	void allModelClassesEqualsHashCodeToString() throws Exception {
		List<Class<?>> classes = loadModelClasses();
		assertTrue(classes.size() > 40, "expected many model classes, found " + classes.size());

		int exercised = 0;
		for (Class<?> type : classes) {
			if (type.isEnum() || type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
				continue;
			}
			Object a;
			Object b;
			try {
				a = type.getDeclaredConstructor().newInstance();
				b = type.getDeclaredConstructor().newInstance();
			} catch (ReflectiveOperationException ex) {
				continue; // needs args — skip
			}
			assertEquals(a, b, () -> type.getName() + " default instances should be equal");
			assertEquals(a.hashCode(), b.hashCode());
			assertNotNull(a.toString());
			assertNotEquals(a, new Object());
			assertNotEquals(a, null);
			// invoke setters with zeroes / empty arrays where possible to hit branches
			for (Method m : type.getMethods()) {
				if (!m.getName().startsWith("set") || m.getParameterCount() != 1) {
					continue;
				}
				Class<?> p = m.getParameterTypes()[0];
				Object arg = sampleArg(p);
				if (arg == null) {
					continue;
				}
				assertDoesNotThrow(() -> m.invoke(a, arg), () -> type.getName() + "#" + m.getName());
			}
			exercised++;
		}
		assertTrue(exercised > 30, "exercised too few model types: " + exercised);
	}

	@Test
	void enumsValues() throws Exception {
		for (Class<?> type : loadModelClasses()) {
			if (!type.isEnum()) {
				continue;
			}
			Object[] constants = type.getEnumConstants();
			assertNotNull(constants);
			assertTrue(constants.length > 0);
			for (Object c : constants) {
				assertNotNull(c.toString());
				assertEquals(c, Enum.valueOf((Class) type, ((Enum<?>) c).name()));
			}
		}
	}

	private static Object sampleArg(Class<?> p) {
		if (p == boolean.class || p == Boolean.class) {
			return true;
		}
		if (p == byte.class || p == Byte.class) {
			return (byte) 1;
		}
		if (p == short.class || p == Short.class) {
			return (short) 1;
		}
		if (p == int.class || p == Integer.class) {
			return 1;
		}
		if (p == long.class || p == Long.class) {
			return 1L;
		}
		if (p == float.class || p == Float.class) {
			return 1.0f;
		}
		if (p == double.class || p == Double.class) {
			return 1.0d;
		}
		if (p == char.class || p == Character.class) {
			return 'a';
		}
		if (p == String.class) {
			return "x";
		}
		if (p == boolean[].class) {
			return new boolean[] { true };
		}
		if (p == byte[].class) {
			return new byte[] { 1 };
		}
		if (p == short[].class) {
			return new short[] { 1 };
		}
		if (p == int[].class) {
			return new int[] { 1 };
		}
		if (p == long[].class) {
			return new long[] { 1L };
		}
		if (p == float[].class) {
			return new float[] { 1f };
		}
		if (p == double[].class) {
			return new double[] { 1d };
		}
		if (p == char[].class) {
			return new char[] { 'a' };
		}
		if (p == String[].class) {
			return new String[] { "x" };
		}
		if (p.isArray()) {
			return java.lang.reflect.Array.newInstance(p.getComponentType(), 0);
		}
		if (p.isEnum()) {
			Object[] vals = p.getEnumConstants();
			return vals != null && vals.length > 0 ? vals[0] : null;
		}
		try {
			return p.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException ex) {
			return null;
		}
	}

	private static List<Class<?>> loadModelClasses() throws IOException, URISyntaxException, ClassNotFoundException {
		String pkg = "io.mosip.imagedecoder.model";
		String path = pkg.replace('.', '/');
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		List<Class<?>> out = new ArrayList<>();
		Enumeration<URL> roots = cl.getResources(path);
		while (roots.hasMoreElements()) {
			URL url = roots.nextElement();
			if ("file".equals(url.getProtocol())) {
				Path dir = Path.of(url.toURI());
				try (Stream<Path> walk = Files.walk(dir)) {
					walk.filter(p -> p.toString().endsWith(".class")).forEach(p -> {
						String rel = dir.relativize(p).toString().replace('\\', '/').replace('/', '.');
						String name = pkg + "." + rel.substring(0, rel.length() - 6);
						if (name.contains("$")) {
							return;
						}
						try {
							out.add(Class.forName(name));
						} catch (ClassNotFoundException ignored) {
							// skip
						}
					});
				}
			} else if ("jar".equals(url.getProtocol())) {
				String file = url.getFile();
				String jarPath = file.substring(5, file.indexOf('!'));
				try (JarFile jar = new JarFile(jarPath)) {
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						JarEntry e = entries.nextElement();
						String name = e.getName();
						if (name.startsWith(path) && name.endsWith(".class") && !name.contains("$")) {
							out.add(Class.forName(name.replace('/', '.').substring(0, name.length() - 6)));
						}
					}
				}
			}
		}
		return out;
	}
}
