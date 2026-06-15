package com.tolga.customer.service;

import com.tolga.customer.model.TrSanctionEntity;
import com.tolga.customer.repository.TrSanctionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrAmlScreeningService {

    private final TrSanctionRepository trSanctionRepository;

    public TrAmlScreeningService(TrSanctionRepository trSanctionRepository) {
        this.trSanctionRepository = trSanctionRepository;
    }

    // Personel paneli veya mobil onboarding bu metodun kapısını çalacak
    public TrScreeningResult screenCustomer(String firstName, String lastName, LocalDate dob, String identityNumber) {
        String searchName = (firstName + " " + lastName).trim();

        // --- 1. AŞAMA: T.C. KİMLİK NUMARASI İLE NOKTA ATIŞI KONTROL ---
        if (identityNumber != null && !identityNumber.trim().isEmpty()) {
            // Pasaport alanını boş geçerek sadece TCKN aratıyoruz
            List<TrSanctionEntity> tcMatches = trSanctionRepository.findByIdentityNumberOrPassportNumber(identityNumber.trim(), null);

            if (!tcMatches.isEmpty()) {
                TrSanctionEntity match = tcMatches.get(0);
                return TrScreeningResult.builder()
                        .isBlocked(true)
                        .reason("TR-SANCTION-TCKN-MATCH")
                        .auditLogMessage("CRITICAL: Absolute identity match found on TR Sanction List via TCKN: " + identityNumber)
                        .matchedTargetName(match.getWholeName())
                        .trReferenceId(match.getTrReferenceId())
                        .motherName(match.getMotherName())
                        .fatherName(match.getFatherName())
                        .legalBasis(match.getLegalBasis())
                        .build();
            }
        }

        // --- 2. AŞAMA: İSİM + DOĞUM TARİHİ ÇAPRAZ KONTROLÜ ---
        List<TrSanctionEntity> nameAndDobMatches = trSanctionRepository.findByWholeNameAndDob(searchName, dob);
        if (!nameAndDobMatches.isEmpty()) {
            TrSanctionEntity match = nameAndDobMatches.get(0);
            return TrScreeningResult.builder()
                    .isBlocked(true)
                    .reason("TR-SANCTION-NAME-DOB-MATCH")
                    .auditLogMessage("WARNING: Name and Date of Birth match on TR Sanction List. Review Parents' names for verification.")
                    .matchedTargetName(match.getWholeName())
                    .trReferenceId(match.getTrReferenceId())
                    .motherName(match.getMotherName())
                    .fatherName(match.getFatherName())
                    .legalBasis(match.getLegalBasis())
                    .build();
        }

        // --- TEMİZ KULLANICI: HİÇBİR LİSTEYE TAKILMADI ---
        return TrScreeningResult.builder()
                .isBlocked(false)
                .reason("TR-CLEAR")
                .auditLogMessage("Automated TR compliance screening cleared. No matches found.")
                .build();
    }

    // --- SONUÇ RAPORU ŞABLONU (Frontend paneline fırlatılacak veri paketi) ---
    public static class TrScreeningResult {
        private boolean isBlocked;
        private String reason;
        private String auditLogMessage;
        private String matchedTargetName;
        private String trReferenceId;
        private String motherName;
        private String fatherName;
        private String legalBasis;

        public TrScreeningResult() {}

        public TrScreeningResult(boolean isBlocked, String reason, String auditLogMessage,
                                 String matchedTargetName, String trReferenceId,
                                 String motherName, String fatherName, String legalBasis) {
            this.isBlocked = isBlocked;
            this.reason = reason;
            this.auditLogMessage = auditLogMessage;
            this.matchedTargetName = matchedTargetName;
            this.trReferenceId = trReferenceId;
            this.motherName = motherName;
            this.fatherName = fatherName;
            this.legalBasis = legalBasis;
        }

        public static Builder builder() { return new Builder(); }

        public boolean getIsBlocked() { return isBlocked; }
        public String getReason() { return reason; }
        public String getAuditLogMessage() { return auditLogMessage; }
        public String getMatchedTargetName() { return matchedTargetName; }
        public String getTrReferenceId() { return trReferenceId; }
        public String getMotherName() { return motherName; }
        public String getFatherName() { return fatherName; }
        public String getLegalBasis() { return legalBasis; }

        public static class Builder {
            private boolean isBlocked;
            private String reason;
            private String auditLogMessage;
            private String matchedTargetName;
            private String trReferenceId;
            private String motherName;
            private String fatherName;
            private String legalBasis;

            public Builder isBlocked(boolean isBlocked) { this.isBlocked = isBlocked; return this; }
            public Builder reason(String reason) { this.reason = reason; return this; }
            public Builder auditLogMessage(String auditLogMessage) { this.auditLogMessage = auditLogMessage; return this; }
            public Builder matchedTargetName(String matchedTargetName) { this.matchedTargetName = matchedTargetName; return this; }
            public Builder trReferenceId(String trReferenceId) { this.trReferenceId = trReferenceId; return this; }
            public Builder motherName(String motherName) { this.motherName = motherName; return this; }
            public Builder fatherName(String fatherName) { this.fatherName = fatherName; return this; }
            public Builder legalBasis(String legalBasis) { this.legalBasis = legalBasis; return this; }

            public TrScreeningResult build() {
                return new TrScreeningResult(isBlocked, reason, auditLogMessage, matchedTargetName, trReferenceId, motherName, fatherName, legalBasis);
            }
        }
    }
}