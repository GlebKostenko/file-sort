package org.doczilla.sort;

import java.util.*;


public class SortByFileNameStrategy implements SortStrategy {
    Map<String, Boolean> processed;

    public SortByFileNameStrategy() {
        processed = new HashMap<>();
    }

    @Override
    public List<String> sortFiles(Map<String, List<String>> fileDependency) {
        List<String> rootFiles = new ArrayList<>(fileDependency.keySet());
        List<String> sortedFiles = new ArrayList<>();
        Collections.sort(rootFiles);
        for (String root : rootFiles) {
            sort(root, fileDependency, sortedFiles);
        }
        return sortedFiles;
    }

    private void sort(String root, Map<String, List<String>> fileDependency, List<String> sortedFiles) {
        Collections.sort(fileDependency.get(root));
        for (String child : fileDependency.get(root)) {
            if (!processed.getOrDefault(child, false)) sort(child, fileDependency, sortedFiles);
        }
        sortedFiles.add(root);
        processed.put(root, true);
    }
}
