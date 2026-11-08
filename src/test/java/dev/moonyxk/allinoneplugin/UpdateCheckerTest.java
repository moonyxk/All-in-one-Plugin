package dev.moonyxk.allinoneplugin;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateCheckerTest {

    private HttpServer server;
    private Logger logger;
    private final List<LogRecord> messages = new ArrayList<>();

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                messages.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @ParameterizedTest
    @CsvSource({
        "v1.0.4, 1.0.3, true",
        "v1.0.10, 1.0.9, true",
        "v1.1.0, 1.0.99, true",
        "v2.0.0, 1.99.99, true",
        "v1.0.3, 1.0.3, false",
        "v1.0.2, 1.0.3, false",
        "v1.0.3, 1.0.3-rc.1, true",
        "v1.0.3-rc.1, 1.0.3, false",
        "v1.0.3-rc.10, 1.0.3-rc.9, true",
        "v1.0.3+build.2, 1.0.3+build.1, false"
    })
    void comparesVersionsNumerically(String latest, String current, boolean newer) {
        assertEquals(newer, UpdateChecker.isNewerVersion(latest, current));
    }

    @Test
    void reportsNewReleaseWithDownloadLink() {
        respond(302, "https://github.com/moonyxk/All-in-one-Plugin/releases/tag/v1.0.4");
        check();

        assertEquals(2, messages.size());
        assertEquals("Downloade hier die neuste: https://github.com/moonyxk/All-in-one-Plugin/releases/latest",
                messages.get(1).getMessage());
    }

    @Test
    void usesConfiguredMessagesForNewRelease() {
        respond(302, "https://github.com/moonyxk/All-in-one-Plugin/releases/tag/v1.0.4");
        URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/latest");
        new UpdateChecker("1.0.3", logger, uri,
                "Update %latest%; installed %current%", "Get it here: %url%").run();

        assertEquals("Update v1.0.4; installed 1.0.3", messages.get(0).getMessage());
        assertEquals("Get it here: https://github.com/moonyxk/All-in-one-Plugin/releases/latest",
                messages.get(1).getMessage());
    }

    @Test
    void doesNotAnnounceAnOlderRelease() {
        respond(302, "https://github.com/moonyxk/All-in-one-Plugin/releases/tag/v1.0.2");
        check();

        assertTrue(messages.isEmpty());
    }

    @Test
    void reportsGitHubErrorsWithoutDownloadMessage() {
        respond(404, null);
        check();

        assertEquals(1, messages.size());
        assertTrue(messages.getFirst().getMessage().contains("fehlgeschlagen"));
        assertTrue(messages.getFirst().getMessage().contains("HTTP 404"));
    }

    @Test
    void rejectsMissingReleaseLink() {
        respond(302, null);
        check();

        assertEquals(1, messages.size());
        assertTrue(messages.getFirst().getMessage().contains("keinen Release-Link"));
    }

    @Test
    void rejectsUnknownVersionFormats() {
        assertThrows(IllegalArgumentException.class,
                () -> UpdateChecker.isNewerVersion("not-a-version", "1.0.3"));
    }

    private void respond(int status, String location) {
        server.createContext("/latest", exchange -> {
            if (location != null) {
                exchange.getResponseHeaders().set("Location", location);
            }
            exchange.sendResponseHeaders(status, -1);
            exchange.close();
        });
        server.start();
    }

    private void check() {
        URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/latest");
        new UpdateChecker("1.0.3", logger, uri).run();
    }
}
