package com.investment.controller;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import com.investment.service.CryptoRecommendationService;
import com.investment.service.model.Crypto;
import com.investment.service.model.CryptoSymbol;
import com.investment.service.model.PriceOption;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CryptoController implements CryptoApi {

    private final CryptoRecommendationService cryptoRecommendationService;

    @Override
    public CryptoSymbol findMaxRangeCrypto(LocalDate date) {
        return cryptoRecommendationService.findMaxRangeCrypto(date);
    }

    @Override
    public List<CryptoSymbol> findNormalizedRangeSortedList() {
        return cryptoRecommendationService.findNormalizedRangeSortedList();
    }

    @Override
    public Crypto findCryptoByNameAndPriceOption(String cryptoName, String priceOption) {
        return cryptoRecommendationService.findCryptoByNameAndPriceOption(CryptoSymbol.fromValue(cryptoName), PriceOption.valueOf(priceOption));
    }
}
