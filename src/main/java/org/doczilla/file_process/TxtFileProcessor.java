package org.doczilla.file_process;

import org.doczilla.dependency.DependencyManager;
import org.doczilla.sort.SortStrategy;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public class TxtFileProcessor implements FileProcessor {
    private String contextPath;
    private DependencyManager dependencyManager;
    private SortStrategy sortStrategy;
    private String outputFilePath;

    public TxtFileProcessor(String contextPath, DependencyManager dependencyManager, SortStrategy sortStrategy, String outputFilePath) {
        this.contextPath = contextPath;
        this.dependencyManager = dependencyManager;
        this.sortStrategy = sortStrategy;
        this.outputFilePath = outputFilePath;
    }

    @Override
    public void process() throws IOException {
        try {
            Map<String, List<String>> dependencyMap = dependencyManager.buildDependencyMap(contextPath, getFilterFunction());
            List<String> sortedFiles = sortStrategy.sortFiles(dependencyMap);
            concatenateFiles(sortedFiles, outputFilePath);
            printFiles(sortedFiles);
        } catch (IOException e){
            throw e;
        }
    }

    private static void concatenateFiles(List<String> fileList, String outputFilePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (String filePath : fileList) {
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.newLine();
                }
            }
        }
    }

    public void printFiles(List<String> files){
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(System.out));
        for (String file: files) {
            printWriter.print(file + "\n");
        }
        printWriter.flush();
    }

    public Predicate<Path> getFilterFunction() {
        return path -> Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".txt");
    }
}
