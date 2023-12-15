import org.doczilla.dependency.DependencyManager;
import org.doczilla.dependency.TxtDependencyManager;
import org.doczilla.exception.CycleDependencyException;
import org.doczilla.exception.PathNotExistException;
import org.doczilla.file_process.FileProcessor;
import org.doczilla.file_process.TxtFileProcessor;
import org.doczilla.sort.SortByFileNameStrategy;
import org.doczilla.sort.SortStrategy;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TestFileProcessor {
    List<File> tempFiles = new ArrayList<>();
    List<Path> tempPaths = new ArrayList<>();
    String userPath = Paths.get(System.getProperty("user.dir")).toString();

    @Test
    public void process_whenCycleDependencyExists_thenExceptionShouldBeThrown() throws IOException {
        for (int i = 0; i < tempFiles.size(); i++) {
            String content = String.format("IT BELONGS TO %d TXT\n" +
                            "BUT YOU CAN SEE THIS IN RESULT TXT\n",
                    i + 1);
            writeContentToFile(tempFiles.get(i), content);
        }
        String requireOneByFour = String.format("require '%s'", getPathFromProjectRoot(tempFiles.get(0)));
        String requireFourBySix = String.format("require '%s'", getPathFromProjectRoot(tempFiles.get(3)));
        String requireSixByOne = String.format("require '%s'", getPathFromProjectRoot(tempFiles.get(6)));
        writeContentToFile(tempFiles.get(3), requireOneByFour);
        writeContentToFile(tempFiles.get(6), requireFourBySix);
        writeContentToFile(tempFiles.get(0), requireSixByOne);
        assertThrows(
                CycleDependencyException.class,
                this::doProcess,
                "Expected doProcess() to throw CycleDependencyException, but it didn't"
        );
    }

    @Test
    public void process_whenRequiredFileNotExist_thenPathNotExistExceptionShouldBeThrown() throws IOException {
        for (int i = 0; i < tempFiles.size(); i++) {
            String content = String.format("IT BELONGS TO %d TXT\n" +
                            "BUT YOU CAN SEE THIS IN RESULT TXT\n",
                    i + 1);
            writeContentToFile(tempFiles.get(i), content);
        }
        String requireOneByFour = String.format("require '%s'",  "random_12345.txt");
        writeContentToFile(tempFiles.get(3), requireOneByFour);
        assertThrows(
                PathNotExistException.class,
                this::doProcess,
                "Expected doProcess() to throw PathNotExistException, but it didn't"
        );
    }

    @Test
    public void process_whenAllIsOk_thenProperOutputShouldBeSeen() throws IOException {
        for (int i = 0; i < tempFiles.size(); i++) {
            String content = String.format("IT BELONGS TO %d TXT\n" +
                            "BUT YOU CAN SEE THIS IN RESULT TXT\n",
                    i + 1);
            writeContentToFile(tempFiles.get(i), content);
        }
        String requireOneByFour = String.format("require '%s'", getPathFromProjectRoot(tempFiles.get(0)));
        writeContentToFile(tempFiles.get(3), requireOneByFour);
        doProcess();
    }

    private void doProcess() throws IOException {
        Properties appProperties = getProperties();
        DependencyManager dependencyManager = new TxtDependencyManager();
        SortStrategy sortStrategy = new SortByFileNameStrategy();
        FileProcessor fileProcessor = new TxtFileProcessor(
                userPath,
                dependencyManager,
                sortStrategy,
                appProperties.getProperty("output.file.path")
        );
        fileProcessor.process();
    }

    public Properties getProperties() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "app.properties";
        Properties appProps = new Properties();
        appProps.load(Files.newInputStream(Paths.get(appConfigPath)));
        return appProps;
    }

    private void writeContentToFile(File file, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPathFromProjectRoot(File file) {
        String[] dirs = file.toString().split(File.separator);
        int index = 0;
        StringBuilder result = new StringBuilder();
        while (index < dirs.length) {
            if (result.toString().equals(userPath)) result.delete(0, result.length());
            if (!dirs[index].isEmpty()) result.append(File.separator).append(dirs[index]);
            index++;
        }
        return result.toString();
    }

    @BeforeEach
    private void createTempFiles() throws IOException {
        createTempDir();
        File file = File.createTempFile("one", ".txt", tempPaths.get(0).toFile());
        File file1 = File.createTempFile("two", ".txt", tempPaths.get(0).toFile());
        File file2 = File.createTempFile("third", ".txt", tempPaths.get(0).toFile());
        File file3 = File.createTempFile("fourth", ".txt", tempPaths.get(1).toFile());
        File file4 = File.createTempFile("fifth", ".txt", tempPaths.get(1).toFile());
        File file5 = File.createTempFile("illegal", ".csv", tempPaths.get(1).toFile());
        File file6 = File.createTempFile("sixth", ".txt", tempPaths.get(2).toFile());
        tempFiles.add(file);
        tempFiles.add(file1);
        tempFiles.add(file2);
        tempFiles.add(file3);
        tempFiles.add(file4);
        tempFiles.add(file5);
        tempFiles.add(file6);
    }

    private void createTempDir() throws IOException {
        Path dir = Files.createTempDirectory(Paths.get(userPath), "doczilla_txt");
        Path dir1 = Files.createTempDirectory(dir, "one");
        Path dir2 = Files.createTempDirectory(dir1, "two");
        tempPaths.add(dir);
        tempPaths.add(dir1);
        tempPaths.add(dir2);
    }

//    @AfterEach
    private void deleteTempFiles() throws IOException {
        Properties properties = getProperties();
        Path result = Paths.get(properties.getProperty("output.file.path"));
        if (Files.exists(result)) result.toFile().delete();
        for (File file : tempFiles) {
            file.delete();
        }
        tempFiles.clear();
        deleteDirs();
    }


    private void deleteDirs() {
        for (Path path : tempPaths) {
            path.toFile().delete();
        }
        tempPaths.clear();
    }
}
