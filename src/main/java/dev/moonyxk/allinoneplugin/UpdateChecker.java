package dev.moonyxk.allinoneplugin;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UpdateChecker implements Runnable {

    private static final String RELEASES_URL = "https://github.com/moonyxk/All-in-one-Plugin/releases/";
    private static final String DOWNLOAD_URL = RELEASES_URL + "latest";
    private static final Pattern VERSION = Pattern.compile(
            "^v?(\\d+)\\.(\\d+)\\.(\\d+)(?:-([0-9A-Za-z.-]+))?(?:\\+[0-9A-Za-z.-]+)?$");

    private final String currentVersion;
    private final Logger logger;
    private final URI latestRelease;

    public UpdateChecker(String currentVersion, Logger logger) {
        this(currentVersion, logger, URI.create(DOWNLOAD_URL));
    }

    UpdateChecker(String currentVersion, Logger logger, URI latestRelease) {
        this.currentVersion = currentVersion;
        this.logger = logger;
        this.latestRelease = latestRelease;
    }

    @Override
    public void run() {
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build()) {
            HttpRequest request = HttpRequest.newBuilder(latestRelease)
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "AllInOnePlugin-UpdateChecker")
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 302) {
                throw new IOException("GitHub antwortete mit HTTP " + response.statusCode());
            }
            String location = response.headers().firstValue("Location")
                    .orElseThrow(() -> new IOException("GitHub lieferte keinen Release-Link"));
            if (!location.startsWith(RELEASES_URL + "tag/")) {
                throw new IOException("GitHub lieferte einen unerwarteten Release-Link");
            }
            String latestVersion = location.substring((RELEASES_URL + "tag/").length());
            if (isNewerVersion(latestVersion, currentVersion)) {
                logger.info("Eine neuere Version ist verfügbar: " + latestVersion
                        + " (installiert: " + currentVersion + ").");
                logger.info("Downloade hier die neuste: " + DOWNLOAD_URL);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.warning("GitHub-Versionsprüfung wurde unterbrochen.");
        } catch (IOException | IllegalArgumentException exception) {
            logger.warning("GitHub-Versionsprüfung fehlgeschlagen: " + exception.getMessage());
        }
    }

    static boolean isNewerVersion(String latestVersion, String currentVersion) {
        Matcher latest = parseVersion(latestVersion);
        Matcher current = parseVersion(currentVersion);
        for (int part = 1; part <= 3; part++) {
            int comparison = new BigInteger(latest.group(part)).compareTo(new BigInteger(current.group(part)));
            if (comparison != 0) {
                return comparison > 0;
            }
        }
        String latestPrerelease = latest.group(4);
        String currentPrerelease = current.group(4);
        if (latestPrerelease == null || currentPrerelease == null) {
            return latestPrerelease == null && currentPrerelease != null;
        }
        String[] latestParts = latestPrerelease.split("\\.");
        String[] currentParts = currentPrerelease.split("\\.");
        for (int part = 0; part < Math.min(latestParts.length, currentParts.length); part++) {
            String left = latestParts[part];
            String right = currentParts[part];
            boolean leftNumeric = left.matches("\\d+");
            boolean rightNumeric = right.matches("\\d+");
            int comparison;
            if (leftNumeric && rightNumeric) {
                comparison = new BigInteger(left).compareTo(new BigInteger(right));
            } else if (leftNumeric != rightNumeric) {
                comparison = leftNumeric ? -1 : 1;
            } else {
                comparison = left.compareTo(right);
            }
            if (comparison != 0) {
                return comparison > 0;
            }
        }
        return latestParts.length > currentParts.length;
    }

    private static Matcher parseVersion(String version) {
        Matcher matcher = VERSION.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unbekanntes Versionsformat: " + version);
        }
        return matcher;
    }
}
