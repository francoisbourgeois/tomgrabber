package de.lall.tomgrabber.service;

import de.lall.tomgrabber.client.TazHtmlClient;
import de.lall.tomgrabber.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Service
public class GrabberService {
    private final TazHtmlClient tazHtmlClient;
    private final ImageRepository imageRepository;

    public GrabberService(TazHtmlClient tazHtmlClient, ImageRepository imageRepository) {
        this.tazHtmlClient = tazHtmlClient;
        this.imageRepository = imageRepository;
    }

    public void test() throws IOException, InterruptedException {
        try (InputStream inputStream = tazHtmlClient.download(URI.create("https://taz.de/scripts/tom/gif.php?t=tom&d=1038783600"))) {
            imageRepository.saveFile("tom1038783600.gif", inputStream);
        }
    }
}
