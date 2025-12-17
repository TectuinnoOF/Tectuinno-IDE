package org.tectuinno.utils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class ExampleResources {

    private static final String EXAMPLES_DIR = "org/tectuinno/examples/";

    private ExampleResources() {}

    public static List<String> listAsm() {

        try {
            URL url = ExampleResources.class.getClassLoader().getResource(EXAMPLES_DIR);
            if (url == null) {
                return List.of();
            }

            // La URI del recurso
            URI uri = url.toURI();
            
            // Si el recurso está en un JAR (por ejemplo: jar:file:/path/app.jar!/org/tectuinno/examples/)
            if ("jar".equals(uri.getScheme())) {
                
                // Crea un FileSystem para el JAR, o usa uno existente
                // Esto permite tratar los contenidos del JAR como un sistema de archivos
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
                    
                    // Obtén la ruta dentro del JAR (sin duplicar el prefijo jar:file:...!)
                    Path root = fileSystem.getPath(EXAMPLES_DIR.startsWith("/") ? EXAMPLES_DIR : "/" + EXAMPLES_DIR);
                    
                    try (Stream<Path> s = Files.list(root)) {
                        return s.filter(Files::isRegularFile)
                                .map(Path::getFileName)
                                .map(Path::toString)
                                .filter(name -> name.endsWith(".asm"))
                                .sorted()
                                .collect(Collectors.toList());
                    }
                }
            } else {
                // Ejecución en IDE (protocolo "file")
                Path dir = Paths.get(uri);
                if (!Files.exists(dir))
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

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /** Lee el contenido de un ejemplo .asm como texto UTF-8. */
    public static String readAsm(String fileName) throws IOException {
        String resourcePath = EXAMPLES_DIR.replace("\\", "/") + fileName; 
        try (InputStream in = ExampleResources.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) throw new FileNotFoundException("Recurso no encontrado: " + resourcePath);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}