package org.fastcatsearch.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClassScanner<E> {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);
	
	public List<E> scanClass(String packageName, Object param) {
		List<E> ret = new ArrayList<E>();
		
		try {
			if(packageName==null || "".equals(packageName)) {
				packageName="";
			}
			
			String pathStr = "";
			pathStr = packageName.replace(".", "/");
			if (!pathStr.endsWith("/")) {
				pathStr = pathStr + "/";
			}
			
			String[] pathArray = pathStr.split(",");
			
			for(String path : pathArray) {
				Enumeration<URL> classEnumeration = null;
				logger.trace("find class from {}", path);
				classEnumeration = DynamicClassLoader.getResources(path);
				while(classEnumeration.hasMoreElements()) {
					String urlString = classEnumeration.nextElement().toString();
					if(urlString.startsWith("jar:file:")) {
						String jpath = urlString.substring(9);
						int st = jpath.indexOf("!/");
						String jarPath = jpath.substring(0, st);
						String entryPath = jpath.substring(st + 2);
						JarFile jarFile = new JarFile(jarPath);
						try {
							Enumeration<JarEntry>jee = jarFile.entries();
							while(jee.hasMoreElements()) {
								JarEntry jarEntry = jee.nextElement();
								String className = jarEntry.getName();
								if (className.startsWith(entryPath)) {
									if(className.endsWith(".class")) {
										className = className.substring(0,className.length() - 6);
										className = className.replaceAll("/", ".");
										E args = done(className, packageName, param);
										if(args!=null && !ret.contains(args)) { ret.add(args); }
									}
								}
							}
						} finally{
							jarFile.close();
						}
					} else  if(urlString.startsWith("file:")) {
						String rootPath = urlString.substring(5);
						int prefixLength = rootPath.indexOf(path);
						File baseFile = new File(rootPath);
						
						Set<File> fileSet = new HashSet<File>();
						if (baseFile.isDirectory()) {
							addDirectory(fileSet, baseFile);
						} else {
							fileSet.add(baseFile);
						}
						for (File file : fileSet) {
							String classPath = file.toURI().toURL().toString()
								.substring(5).substring(prefixLength);
							if(classPath.endsWith(".class")) {
								classPath = classPath.substring(0,classPath.length() - 6);
								classPath = classPath.replaceAll("/", ".");
								E args = done(classPath, packageName, param);
								if(args!=null && !ret.contains(args)) { ret.add(args); }
							}
						}
					}
				}
			}
			return ret;
		} catch (IOException e) { }
		return null;
	}
	
	private void addDirectory(Set<File> set, File d){
		File[] files = d.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()){
				addDirectory(set, files[i]);
			}else{
				set.add(files[i]);
			}
		}
	}
	protected abstract E done(String className, String packageName, Object param);
}