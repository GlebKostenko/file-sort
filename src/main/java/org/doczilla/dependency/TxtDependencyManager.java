package org.doczilla.dependency;

import org.doczilla.exception.CycleDependencyException;
import org.doczilla.exception.PathNotExistException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class TxtDependencyManager implements DependencyManager {

    @Override
    public Map<String, List<String>> buildDependencyMap(String contextPath, Predicate<Path> filterFile) throws IOException {
        Map<String, List<String>> dependencyMap = new HashMap<>();
        try {
            List<String> fileList = findFiles(contextPath, filterFile);
            for (String filePath : fileList) {
                List<String> dependencies = findDependencies(filePath, contextPath);
                dependencyMap.put(filePath, dependencies);
            }
            List<String> visitedFiles = new ArrayList<>();
            for (String rootPath : dependencyMap.keySet()) {
                checkForCycleDependency(rootPath, visitedFiles, dependencyMap);
            }
        } catch (IOException | CycleDependencyException e) {
            throw e;
        }
        return dependencyMap;
    }

    @Override
    public List<String> findFiles(String contextPath, Predicate<Path> filter) throws IOException {
        return Files.walk(Paths.get(contextPath))
                .filter(filter)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findDependencies(String filePath, String contextPath) throws IOException {
        List<String> dependencies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("require")) {
                    String dependency = line.split("'")[1];
                    String separator = dependency.startsWith(File.separator) ? "" : File.separator;
                    String dependencyPath = contextPath + separator + dependency;
                    if (Files.exists(Paths.get(dependencyPath))){
                        dependencies.add(dependencyPath);
                    } else {
                        throw new PathNotExistException(String.format("%s it is required in ====> %s", dependencyPath, filePath));
                    }
                }
            }
        }
        return dependencies;
    }

    @Override
    public void checkForCycleDependency(String filePath, List<String> visitedFiles, Map<String, List<String>> dependencyMap) {
        if (visitedFiles.contains(filePath)) {
            throw new CycleDependencyException(String.format("%s ====> %s", visitedFiles, filePath));
        }
        visitedFiles.add(filePath);
        for (String child : dependencyMap.get(filePath)) {
            checkForCycleDependency(child, visitedFiles, dependencyMap);
        }
        visitedFiles.remove(filePath);
    }

}
