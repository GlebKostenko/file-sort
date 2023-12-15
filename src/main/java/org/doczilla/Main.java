package org.doczilla;

import org.doczilla.dependency.DependencyManager;
import org.doczilla.dependency.TxtDependencyManager;
import org.doczilla.file_process.FileProcessor;
import org.doczilla.file_process.TxtFileProcessor;
import org.doczilla.sort.SortByFileNameStrategy;
import org.doczilla.sort.SortStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties appProperties = getProperties();
        DependencyManager dependencyManager = new TxtDependencyManager();
        SortStrategy sortStrategy = new SortByFileNameStrategy();
        FileProcessor fileProcessor = new TxtFileProcessor(
                System.getProperty("user.dir"),
                dependencyManager,
                sortStrategy,
                appProperties.getProperty("output.file.path")
        );
        fileProcessor.process();
    }

    public static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = cl.getResourceAsStream("app.properties")) {
            properties.load(stream);
        }
        return properties;
    }
}