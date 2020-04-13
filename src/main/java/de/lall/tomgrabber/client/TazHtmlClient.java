package de.lall.tomgrabber.client;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class TazHtmlClient {

    @Value("${tomgrabber.tazclient.useragent}")
    private String useragent;

    private final HttpClient httpClient;

    public TazHtmlClient() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public Document getTomOfTheDayPage(LocalDate date) throws IOException {
        Connection connection = Jsoup
                .connect(getTomOfTheDayRequestUrl(date))
                .userAgent(useragent);

        return connection.get();
    }

    public InputStream download(URI source) throws IOException {
        log.debug("Requesting {}", source.toString());

        try {
            HttpResponse<InputStream> response = httpClient.send(
                    getDownloadImageHttpRequest(source),
                    HttpResponse.BodyHandlers.ofInputStream()
            );

            if (response.statusCode() != 200) {
                throw new HttpStatusException("status not 200", response.statusCode(), source.toString());
            }

            return response.body();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    private HttpRequest getDownloadImageHttpRequest(URI source) {
        return HttpRequest.newBuilder()
                    .uri(source)
                    .timeout(Duration.ofSeconds(20))
                    .header("Referer", "https://taz.de")
                    .header("User-Agent", useragent)
                    .GET()
                    .build();
    }

    private String getTomOfTheDayRequestUrl(LocalDate day) {
        return "https://taz.de/scripts/tom/tomdestages.php?day="
                + day.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}
