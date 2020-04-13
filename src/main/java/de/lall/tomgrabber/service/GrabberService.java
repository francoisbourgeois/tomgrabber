package de.lall.tomgrabber.service;

import de.lall.tomgrabber.client.TazHtmlClient;
import de.lall.tomgrabber.repository.TomRepository;
import de.lall.tomgrabber.repository.entity.Tom;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GrabberService {
    private final TazHtmlClient tazHtmlClient;
    private final TomRepository tomRepository;
    private Set<String> knownTomIds;

    @Value("${tomgrabber.first-tom-date}")
    private String startDate;

    public GrabberService(TazHtmlClient tazHtmlClient, TomRepository tomRepository) {
        this.tazHtmlClient = tazHtmlClient;
        this.tomRepository = tomRepository;
    }

    public void downloadAllMissingToms() {
        Set<Tom> knownToms = tomRepository.getKnownToms();
        Set<LocalDate> knownDates = getValuesFromSet(knownToms, Tom::getDate);
        knownTomIds = getValuesFromSet(knownToms, Tom::getId);

        LocalDate.parse(startDate).datesUntil(LocalDate.now())
                .filter((date -> !knownDates.contains(date)))
                .forEach(this::fetchTom);
    }

    private <T> Set<T> getValuesFromSet(Set<Tom> knownToms, Function<Tom, T> getDate) {
        return knownToms.stream().map(getDate).collect(Collectors.toSet());
    }

    private void fetchTom(LocalDate date) {
        try {
            Document document = tazHtmlClient.getTomOfTheDayPage(date);
            URI imageUri = parseImageUri(document);
            String tomId = parseTomId(imageUri);

            download(imageUri, new Tom(tomId, date));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void download(URI imageUri, Tom tom) throws IOException {
        if (knownTomIds.contains(tom.getId())) {
            log.debug("Skipping duplicate: {}", tom);
            return;
        }

        log.info("Downloading: {}", tom);
        try (InputStream inputStream = tazHtmlClient.download(imageUri)) {
            tomRepository.create(tom, inputStream);
        }

        knownTomIds.add(tom.getId());
    }

    private String parseTomId(URI imageUri) throws IOException {
        return Arrays.stream(imageUri.getQuery().split("&"))
                .filter(queryPart -> queryPart.startsWith("d="))
                .map(queryPart -> queryPart.substring(2))
                .findAny()
                .orElseThrow(() -> new IOException("Cannot find tomId from image URI: " + imageUri.toString()));
    }

    private URI parseImageUri(Document document) {
        String relativePath = document.select("div.tom_content a img[alt=tom]").first().attr("src");
        return URI.create("https://taz.de" + relativePath);
    }
}
