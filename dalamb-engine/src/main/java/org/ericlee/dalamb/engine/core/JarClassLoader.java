package org.ericlee.dalamb.engine.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarClassLoader extends ClassLoader {
    private final List<Class<?>> classes = new ArrayList<>();

    public JarClassLoader(String jarPath) throws IOException {
        try (final ZipFile jar = new ZipFile(jarPath)) {
            Enumeration<? extends ZipEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    byte[] classFile = new byte[(int)entry.getSize()];
                    jar.getInputStream(entry).read(classFile, 0, (int)entry.getSize());

                    String className = entry.getName()
                            .replace(".class", "")
                            .replace("/", ".");
                    classes.add(defineClass(className, classFile, 0, classFile.length));
                }
            }
        }
    }

    public Class<?>[] getClasses() {
        return this.classes.toArray(new Class[0]);
    }
}
