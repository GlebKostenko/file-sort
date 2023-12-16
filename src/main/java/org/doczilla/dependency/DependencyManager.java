package org.doczilla.dependency;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface DependencyManager {
    Map<String, List<String>> buildDependencyMap(String contextPath, Predicate<Path> filterFile) throws IOException;

    List<String> findFiles(String contextPath, Predicate<Path> filter) throws IOException;

    List<String> findDependencies(String filePath, String contextPath) throws IOException;

    void checkForCycleDependency(String filePath, List<String> visitedFiles, Map<String, List<String>> dependencyMap);
}
