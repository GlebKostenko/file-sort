package org.doczilla.sort;

import java.util.List;
import java.util.Map;

public interface SortStrategy {
    List<String> sortFiles(Map<String, List<String>> fileDependency);
}
