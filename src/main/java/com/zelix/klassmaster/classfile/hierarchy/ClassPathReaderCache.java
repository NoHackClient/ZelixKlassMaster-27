package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.ObservableModel;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassPathReaderCache implements NodeVisitor {
    private Map classCache;
    public ClassPathResolver resolver;
    public boolean resolverRetained = false;
    public ZkmClasspath classpath;
    public final boolean caseSensitive;

    public static ClassPathResolver getResolver(ClassPathReaderCache classPathReaderCache) {
        return classPathReaderCache.resolver;
    }

    public ClassPathReaderCache(ZkmClasspath zkmClasspath, boolean caseSensitive) throws ZkmException, IOException {
        this.classpath = zkmClasspath;
        this.caseSensitive = caseSensitive;
        zkmClasspath.addObserver(this);
        this.reset();
    }

    public void close() {
        if (this.resolver != null) {
            this.resolver.close();
        }

        if (this.classpath != null) {
            this.classpath.closeJrtFileSystem();
        }

        this.classCache = null;
    }

    public static Map getClassCache(ClassPathReaderCache classPathReaderCache) {
        return classPathReaderCache.classCache;
    }

    public static boolean setResolverRetained(ClassPathReaderCache classPathReaderCache) {
        return classPathReaderCache.resolverRetained = true;
    }

    public void reset() throws ZkmException, IOException {
        if (!this.resolverRetained && this.resolver != null) {
            this.resolver.close();
        }

        this.resolver = new ClassPathResolver(this.classpath.getClasspath());
        this.resolverRetained = false;
        this.classCache = HiddenOptionFlags.USE_PARALLEL ? new ConcurrentHashMap() : ZkmUtils.createHashMap();
    }

    public Integer getClasspathRelease() {
        return this.classpath.getReleaseVersion();
    }

    public static void resetCache(ClassPathReaderCache classPathReaderCache) throws ZkmException, IOException {
        classPathReaderCache.reset();
    }

    public static ZkmClasspath getClasspath(ClassPathReaderCache classPathReaderCache) {
        return classPathReaderCache.classpath;
    }

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        this.reset();
    }
}
