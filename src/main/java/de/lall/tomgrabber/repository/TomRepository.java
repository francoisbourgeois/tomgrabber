package de.lall.tomgrabber.repository;

import de.lall.tomgrabber.repository.entity.Tom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Repository
@Slf4j
public class TomRepository {
    private final ImageRepository imageRepository;

    public TomRepository(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public void create(Tom tom, InputStream inputStream) throws IOException {
        imageRepository.create(getFilename(tom), inputStream);
    }

    private String getFilename(Tom tom) {
        return String.format("tom %s (%s).gif", tom.getDate().format(ISO_LOCAL_DATE), tom.getId());
    }

    public Set<Tom> getKnownToms() {
        Set<Tom> toms = imageRepository.getFilenames()
                .filter(filename -> filename.endsWith(".gif"))
                .filter(filename -> filename.startsWith("tom "))
                .map(this::parseTom)
                .collect(Collectors.toUnmodifiableSet());
        log.info("Found {} already existing Toms in download directory", toms.size());
        return toms;
    }

    private Tom parseTom(String filename) {
        return new Tom(
                filename.substring(filename.indexOf("(") + 1, filename.indexOf(")")),
                LocalDate.parse(filename.substring(4, 14))
        );
    }
}
