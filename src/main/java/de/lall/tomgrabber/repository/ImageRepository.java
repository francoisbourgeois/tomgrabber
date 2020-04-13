package de.lall.tomgrabber.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Repository
@Slf4j
public class ImageRepository {

    @Value("${tomgrabber.download-directory}")
    private String directoryPath;

    private File downloadDirectory;

    @PostConstruct
    public void init() {
        downloadDirectory = getDownloadDirectory();
    }

    public void saveFile(String filename, InputStream inputStream) throws IOException {
        log.debug("Creating image file: {}", filename);

        File file = createFile(filename);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            StreamUtils.copy(inputStream, outputStream);
        }
    }

    private File createFile(String filename) throws IOException {
        File file = downloadDirectory.toPath().resolve(filename).toFile();
        file.createNewFile();
        return file;
    }

    private File getDownloadDirectory() {
        File downloadDirectory = new File(directoryPath);

        if (downloadDirectory.exists()) {
            log.info("Download directory found: {}", directoryPath);
        } else {
            log.info("Creating download directory: {}", directoryPath);
            if (!downloadDirectory.mkdirs()) {
                throw new RuntimeException("Cannot create download directory '" + directoryPath + "'");
            }
        }

        return downloadDirectory;
    }

}
