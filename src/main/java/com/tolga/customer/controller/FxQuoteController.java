package com.tolga.customer.controller;

import com.tolga.customer.model.BidCacheModel;
import com.tolga.customer.model.BiddingProcess;
import com.tolga.customer.repository.BiddingProcessRepository;
import com.tolga.customer.service.FxCacheService;
import com.tolga.customer.service.FxRateService;
import com.tolga.customer.dto.QuoteRequestDto;
import com.tolga.customer.dto.TransferInstruction;
import com.tolga.customer.service.Pacs008GeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate; // Injected for system_config database queries
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/fx")
public class FxQuoteController {

    private final FxRateService fxRateService;
    private final FxCacheService fxCacheService;
    private final BiddingProcessRepository biddingProcessRepository;
    private final Pacs008GeneratorService pacsService;
    private final JdbcTemplate jdbcTemplate; // Database query engine execution property

    // Updated Constructor with JdbcTemplate injection
    public FxQuoteController(FxRateService fxRateService,
                             FxCacheService fxCacheService,
                             BiddingProcessRepository biddingProcessRepository,
                             Pacs008GeneratorService pacsService,
                             JdbcTemplate jdbcTemplate) {
        this.fxRateService = fxRateService;
        this.fxCacheService = fxCacheService;
        this.biddingProcessRepository = biddingProcessRepository;
        this.pacsService = pacsService;
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. Get Quote Endpoint (Fixed to apply inverse pricing matrix and database flat fee data rules)
    @PostMapping("/quote")
    public ResponseEntity<?> getQuote(@RequestBody QuoteRequestDto request) {
        try {
            // A. Fetch live mid-market reference exchange rate
            double midMarketRate = fxRateService.getLiveEurToTryRate();
            double tryAmt = request.getRequestedTryAmount();

            // B. Query database parameters for live margin spreads and fixed handling processing charges
            String sql = "SELECT spread_percentage, flat_fee_eur FROM system_config WHERE id = 1";
            Map<String, Object> config = jdbcTemplate.queryForMap(sql);
            double spreadPercentage = ((Number) config.get("spread_percentage")).doubleValue();
            double flatFeeEur = ((Number) config.get("flat_fee_eur")).doubleValue();

            // C. Inverted Math Engineering (Target TRY -> Required Grand Total EUR)
            double rateSpreadReduction = midMarketRate * (spreadPercentage / 100.0);
            double guaranteedRate = midMarketRate - rateSpreadReduction;

            // Net amount to convert before application of the processing fee ledger item
            double netConvertibleEur = tryAmt / guaranteedRate;
            double totalRequiredEur = netConvertibleEur + flatFeeEur;

            // D. Initialize internal mapping record cache states
            BidCacheModel cacheModel = fxCacheService.createInitialBid(request.getCustomerId(), tryAmt);

            // E. Store calculated state metadata blocks into the in-memory lookup system table
// Pass spreadPercentage as the fifth argument into the service call
            cacheModel = fxCacheService.updateBidWithRate(cacheModel.getBidId(), guaranteedRate, totalRequiredEur, flatFeeEur, spreadPercentage);
            return ResponseEntity.ok(cacheModel);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", "Failed to compile financial quote schema matrix parameters: " + e.getMessage()
            ));
        }
    }

    // 2. User Accepts Offer Endpoint (Persists to Ledger & Enqueues automated ISO 20022 Pacs.008 pool records)
    @PostMapping("/accept")
    public ResponseEntity<?> acceptQuote(@RequestBody Map<String, Object> body) {
        String bidId = (String) body.get("bidId");
        BidCacheModel cachedBid = fxCacheService.getBid(bidId);

        if (cachedBid == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Teklif süresi dolmuş veya geçersiz!"));
        }

        BiddingProcess permanentTransfer = new BiddingProcess(
                cachedBid.getCustomerId(),
                cachedBid.getRequestedTryAmount(),
                cachedBid.getCalculatedEurAmount(),
                cachedBid.getAppliedRate(),
                cachedBid.getFlatFeeEur(),        // Map flat fee from the cache
                cachedBid.getSpreadPercentage(),  // Map spread markup percentage from the cache
                "ACCEPTED"
        );

        BiddingProcess savedResult = biddingProcessRepository.save(permanentTransfer);

        // ==========================================
        // CEVDET2: PACS.008 AUTOMATED PROCESSING QUEUE ENGINE
        // ==========================================
        try {
            String dinamikMusteriAdi = "Musteri - " + cachedBid.getCustomerId();
            String geciciGonderenIban = "NL99ABNA0123456789";
            String geciciAliciIban = "TR560001100000000012345678";
            String testDebtorBic = "ABNANL2AXXX";
            String testCreditorBic = "TCZATR2AXXX";
            String randomE2EId = "E2E-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            TransferInstruction talimat = new TransferInstruction(
                    randomE2EId,
                    cachedBid.getRequestedTryAmount(),
                    dinamikMusteriAdi,
                    geciciGonderenIban,
                    geciciAliciIban,
                    testDebtorBic,
                    testCreditorBic
            );

            String pacsSonuc = pacsService.addTransferToQueue(talimat);
            System.out.println(">>> PACS.008 MOTOR DURUMU: " + pacsSonuc);

        } catch (Exception e) {
            System.out.println("Pacs havuzuna eklenirken hata: " + e.getMessage());
        }
        // ==========================================

        fxCacheService.removeBid(bidId);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Transfer başarıyla oluşturuldu. iDEAL ödemesine yönlendiriliyorsunuz.",
                "transferId", savedResult.getId().toString()
        ));
    }
}