import itumulator.executable.Program;
import programManagers.FileReaderUtil;

import java.nio.file.Path;
import java.util.List;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String directoryPath = "week-3";
        String specificFileName = "t3-2ab.txt";

        try {

            List<Path> txtFiles = FileReaderUtil.getTxtFilesFromDirectory(directoryPath);
            if (!txtFiles.isEmpty()) {
                Path selectedFile = null;
                for (Path file : txtFiles) {
                    if (file.getFileName().toString().equals(specificFileName)) {
                        selectedFile = file;
                        break;
                    }
                }

                if (selectedFile != null) {
                    Program p = FileReaderUtil.readFile(selectedFile);

                    if (p != null) {
                        p.show();
                    } else {
                        System.err.println("Could not initialize the program correctly from the file.");
                    }
                } else {
                    System.err.println("File " + specificFileName + " not found in folder: " + directoryPath);
                }
            } else {
                System.err.println("No .txt files found in folder: " + directoryPath);
            }
        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        }
    }
}