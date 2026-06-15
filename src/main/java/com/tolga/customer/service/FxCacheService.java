package com.tolga.customer.service;

import com.tolga.customer.model.BidCacheModel;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FxCacheService {

    // Gerçek zamanlı geçici hafızamız (Redis yerine kullanacağımız güvenli harita)
    private final ConcurrentHashMap<String, BidCacheModel> inMemoryBidTable = new ConcurrentHashMap<>();

    /**
     * Akış şemasının 1. adımı: Satır oluşturur, bidId verir,
     * durumu PROCESSING yapar ve hafızaya kaydeder.
     */
    public BidCacheModel createInitialBid(Long customerId, Double requestedTryAmount) {
        // Benzersiz, tahmin edilemez bir Teklif ID üretiyoruz (Örn: "b9f2c3...")
        String uniqueBidId = UUID.randomUUID().toString();

        // Modeli oluşturuyoruz (Otomatik olarak PROCESSING durumunda başlar)
        BidCacheModel initialBid = new BidCacheModel(uniqueBidId, customerId, requestedTryAmount);

        // Akış şemanızdaki "Feed to Redis Bid table" adımı (Burada hafızaya atıyoruz)
        inMemoryBidTable.put(uniqueBidId, initialBid);

        return initialBid;
    }

    /**
     * Hafızadaki bir teklifi ID ile sorgulamak için
     */
    public BidCacheModel getBid(String bidId) {
        return inMemoryBidTable.get(bidId);
    }

    public BidCacheModel updateBidWithRate(String bidId, Double liveRate, Double eurAmount, Double flatFeeEur, Double spreadPercentage) {
        BidCacheModel bid = inMemoryBidTable.get(bidId);

        if (bid != null) {
            bid.setAppliedRate(liveRate);
            bid.setCalculatedEurAmount(eurAmount);
            bid.setFlatFeeEur(flatFeeEur);
            bid.setSpreadPercentage(spreadPercentage); // Save the spread value
            bid.setRateSource("FRANKFURTER");
            bid.setBiddingStatus("OFFERED_TO_CUSTOMER");

            inMemoryBidTable.put(bidId, bid);
        }
        return bid;
    }

    /**
     * İşlemi biten veya kabul edilen teklifi RAM hafızasından temizler.
     */
    public void removeBid(String bidId) {
        inMemoryBidTable.remove(bidId);
    }
}