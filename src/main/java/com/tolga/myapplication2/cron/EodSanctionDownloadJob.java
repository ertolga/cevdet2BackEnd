package com.tolga.myapplication2.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EodSanctionDownloadJob {

    private static final Logger log = LoggerFactory.getLogger(EodSanctionDownloadJob.class);

    // Replace the old webgate URL with this public open-data global mirror:
    private static final String EU_SANCTION_XML_URL = "https://ec.europa.eu/external_relations/cfsp/sanctions/list/version4/global/global.xml";

    // This maps directly to the persistent folder we set up in your docker-compose file
    private static final String AUDIT_STORAGE_PATH = "/app/sanction-audit-logs";

    private final EuSanctionParser sanctionParser;

    // Constructor injection so Spring hands us our StAX parser database engine seamlessly
    public EodSanctionDownloadJob(EuSanctionParser sanctionParser) {
        this.sanctionParser = sanctionParser;
    }

    // Zamanlayıcıyı her gece 02:00'de çalışacak şekilde güncelliyoruz
    @Scheduled(cron = "0 0 2 * * ?")
    public void executeNightlyDownload() {
        log.info("AML ENGINE: Starting automated EOD EU Sanction List download routine...");

        try {
            // 1. Verify that the storage folder exists inside the container environment
            File directory = new File(AUDIT_STORAGE_PATH);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                log.info("AML ENGINE: Storage folder did not exist. Creating it now: {} -> {}", AUDIT_STORAGE_PATH, created);
            }

            // 2. Generate an unrepeatable filename using the current date and time
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File targetAuditFile = new File(directory, "eu_sanctions_master_" + timestamp + ".xml");

            // 3. Initiate the secure network download
            log.info("AML ENGINE: Connecting to EU Portal to fetch fresh updates...");
            downloadAndSaveFile(EU_SANCTION_XML_URL, targetAuditFile);
            log.info("AML ENGINE: Success! Unaltered XML file safely archived at: {}", targetAuditFile.getAbsolutePath());

            // 4. Trigger the StAX pipeline to parse and populate PostgreSQL immediately
            log.info("AML ENGINE: Download successful. Triggering database injection...");
            sanctionParser.parseAndSave(targetAuditFile);
            log.info("AML ENGINE: Success! Daily data changes are fully indexed in your DB cache.");

        } catch (Exception e) {
            log.error("AML ENGINE CRITICAL ERROR: Failed to execute nightly EU Sanction archival. Reason: {}", e.getMessage(), e);
        }
    }

    /**
     * Streams data directly from the network socket to the hard drive.
     * This avoids choking the application's RAM if the XML file size grows very large.
     */
    private void downloadAndSaveFile(String targetUrl, File destinationFile) throws Exception {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(20000); // 20 Seconds Connection Timeout
        connection.setReadTimeout(30000);    // 30 Seconds Data Read Timeout

        // --- REALISTIC BROWSER HEADERS TO BYPASS GATEWAY 403 BLOCKS ---
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP failure response code received from EU server: " + responseCode);
        }

        // Stream the file data chunks directly into our persistent file volume
        try (InputStream inputStream = new BufferedInputStream(connection.getInputStream());
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] dataChunk = new byte[8192]; // Read window buffer (8KB chunks)
            int bytesRead;

            while ((bytesRead = inputStream.read(dataChunk, 0, dataChunk.length)) != -1) {
                outputStream.write(dataChunk, 0, bytesRead);
            }
            outputStream.flush();
        } finally {
            connection.disconnect();
        }
    }
}