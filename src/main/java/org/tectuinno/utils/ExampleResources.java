package org.tectuinno.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExampleResources {
	
	private static final String EXAMPLES_DIR = "org\\tectuinno\\examples\\";
	
	private ExampleResources() {}

    public static List<String> listAsm(){

		try {

			URL url = ExampleResources.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation();
            Path path = Paths.get(url.toURI());


            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                // Ejecución en IDE: ejemplo como directorio real
                Path dir = path.resolve(EXAMPLES_DIR);
                if(!Files.exists(dir))
                    return List.of();
                else {
                    try (Stream<Path> s = Files.list(dir)) {
                        return s.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().endsWith(".asm"))
                                .map(p -> p.getFileName().toString())
                                .sorted()
                                .collect(Collectors.toList());
                    }
                }
            }

            if("jar".equals(protocol)) {

                String prefix = EXAMPLES_DIR + "/";
                JarFile jar = new JarFile(path.toFile());

                 try(jar){
                	 List<String> out = new ArrayList<>();
                     Enumeration<JarEntry> en = jar.entries();
                     while (en.hasMoreElements()) {
                         JarEntry e = en.nextElement();
                         String name = e.getName();
                         if (!e.isDirectory() && name.startsWith(prefix) && name.endsWith(".asm")) {
                             out.add(name.substring(prefix.length()));
                         }
                     }
                     Collections.sort(out);
                     return out;
                 }
            }


			return List.of();

		}catch (Exception e) {

			e.printStackTrace();
			return List.of();

		}

	}

	/*public static List<String> listAsm(){
		
		try {
			
			URL url = ExampleResources.class.getClassLoader().getResource(EXAMPLES_DIR);
            if (url == null) return List.of();
			
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                // Ejecución en IDE: ejemplo como directorio real
                Path dir = Paths.get(url.toURI());
                try (Stream<Path> s = Files.list(dir)) {
                    return s.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().endsWith(".asm"))
                            .map(p -> p.getFileName().toString())
                            .sorted()
                            .collect(Collectors.toList());
                }
            }
            
            if("jar".equals(protocol)) {
            	
            	 String prefix = EXAMPLES_DIR + "/";
                 URLConnection conn = url.openConnection();
                 JarFile jar;
            	
                 if(conn instanceof JarURLConnection juc) {
                	 jar = juc.getJarFile();
                 }else {
                	 String spec = url.getFile(); // file:/path/app.jar!/org/tectuinno/examples
                     int sep = spec.indexOf("!/");
                     String jarPath = spec.substring("file:".length(), sep);
                     jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));
                 }
                 
                 try(jar){
                	 List<String> out = new ArrayList<>();
                     Enumeration<JarEntry> en = jar.entries();
                     while (en.hasMoreElements()) {
                         JarEntry e = en.nextElement();
                         String name = e.getName();
                         if (!e.isDirectory() && name.startsWith(prefix) && name.endsWith(".asm")) {
                             out.add(name.substring(prefix.length()));
                         }
                     }
                     Collections.sort(out);
                     return out;
                 }
            }
            
            
			return List.of();
			
		}catch (Exception e) {
			
			e.printStackTrace();
			return List.of();
			
		}				
		
	}*/
	
	/** Lee el contenido de un ejemplo .asm como texto UTF-8. */
    public static String readAsm(String fileName) throws IOException {
        String resourcePath = EXAMPLES_DIR + "/" + fileName;
        try (InputStream in = ExampleResources.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) throw new FileNotFoundException("Recurso no encontrado: " + resourcePath);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
	

}
