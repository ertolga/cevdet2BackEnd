package com.tolga.myapplication2.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class EuSanctionParser {

    private static final Logger log = LoggerFactory.getLogger(EuSanctionParser.class);
    private final DataSource dataSource;

    public EuSanctionParser(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static class EntityData {
        long refId;
        String type;
        String legalBasis;
        List<String> names = new ArrayList<>();
        LocalDate dob;
        Integer birthYear;
        String nationality;
        String passport;
    }

    public void parseAndSave(File xmlFile) {
        log.info("AML ENGINE: Real XML Schema streaming initiated for: {}", xmlFile.getName());

        String deleteSql = "TRUNCATE TABLE public.sanction_list_eu";
        String insertSql = "INSERT INTO public.sanction_list_eu (eu_reference_id, entity_type, full_name, is_primary_name, date_of_birth, birth_year, nationality, passport_number, legal_basis) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);

        long totalRowsInserted = 0;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 BufferedInputStream bis = new BufferedInputStream(new FileInputStream(xmlFile))) {

                log.info("AML ENGINE: Flushing active database cache table...");
                deleteStmt.executeUpdate();

                XMLStreamReader reader = factory.createXMLStreamReader(bis);
                EntityData current = null;
                String currentElement = "";

                while (reader.hasNext()) {
                    int event = reader.next();

                    switch (event) {
                        case XMLStreamConstants.START_ELEMENT:
                            currentElement = reader.getLocalName();

                            // 1. Yeni bir yaptırım hedefi başlıyor: <ENTITY>
                            if ("ENTITY".equals(currentElement)) {
                                current = new EntityData();
                                String idStr = reader.getAttributeValue(null, "Id");
                                current.refId = idStr != null ? Long.parseLong(idStr) : 0L;

                                String codeType = reader.getAttributeValue(null, "Type");
                                // P = Person (Birey), E = Entity (Kurum/Şirket)
                                current.type = "P".equalsIgnoreCase(codeType) ? "individual" : "entity";
                                current.legalBasis = reader.getAttributeValue(null, "legal_basis");
                            }
                            break;

                        case XMLStreamConstants.CHARACTERS:
                            if (current != null && !reader.isWhiteSpace()) {
                                String text = reader.getText().trim();

                                // 2. İsmi <WHOLENAME> etiketinin içindeki metinden yakalıyoruz
                                if ("WHOLENAME".equals(currentElement)) {
                                    if (!current.names.contains(text)) {
                                        current.names.add(text);
                                    }
                                }
                                // 3. Doğum tarihlerini (Eğer varsa) yakalıyoruz
                                else if ("BIRTHDATE".equals(currentElement) || "DATE".equals(currentElement)) {
                                    try {
                                        // XML içinde genellikle YYYY-MM-DD formatında gelir
                                        current.dob = LocalDate.parse(text);
                                    } catch (Exception e) {
                                        try {
                                            current.birthYear = Integer.parseInt(text.substring(0, 4));
                                        } catch (Exception ignored) {}
                                    }
                                }
                                // 4. Vatandaşlık bilgisini yakalıyoruz
                                else if ("COUNTRY".equals(currentElement)) {
                                    current.nationality = text;
                                }
                                // 5. Pasaport veya Ulusal ID numarasını yakalıyoruz
                                else if ("NUMBER".equals(currentElement)) {
                                    current.passport = text;
                                }
                            }
                            break;

                        case XMLStreamConstants.END_ELEMENT:
                            // 6. Yaptırım hedefinin sonuna geldik: </ENTITY>
                            if ("ENTITY".equals(reader.getLocalName()) && current != null) {
                                if (!current.names.isEmpty()) {
                                    boolean isFirst = true;
                                    for (String name : current.names) {
                                        insertStmt.setLong(1, current.refId);
                                        insertStmt.setString(2, current.type);
                                        insertStmt.setString(3, name);
                                        insertStmt.setBoolean(4, isFirst); // İlk isim primary, diğerleri alias

                                        if (current.dob != null) {
                                            insertStmt.setDate(5, Date.valueOf(current.dob));
                                        } else {
                                            insertStmt.setNull(5, Types.DATE);
                                        }

                                        if (current.birthYear != null) {
                                            insertStmt.setInt(6, current.birthYear);
                                        } else {
                                            insertStmt.setNull(6, Types.INTEGER);
                                        }

                                        insertStmt.setString(7, current.nationality);
                                        insertStmt.setString(8, current.passport);
                                        insertStmt.setString(9, current.legalBasis);

                                        insertStmt.addBatch();
                                        totalRowsInserted++;
                                        isFirst = false;
                                    }
                                }
                                current = null;
                            }
                            currentElement = "";
                            break;
                    }
                }

                // Toplu veriyi gönder ve kaydet
                insertStmt.executeBatch();
                conn.commit();
                log.info("AML ENGINE: Core DB Synchronization finished successfully. Written row count: {}", totalRowsInserted);

            } catch (Exception e) {
                conn.rollback();
                log.error("AML ENGINE PARSER CRASH: Process failed. Reason: {}", e.getMessage(), e);
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Database connection error: ", e);
        }
    }
}