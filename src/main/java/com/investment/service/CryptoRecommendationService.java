package com.investment.service;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import com.investment.dataprovider.CryptoDataProvider;
import com.investment.service.model.Crypto;
import com.investment.service.model.CryptoSymbol;
import com.investment.service.model.PriceOption;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class CryptoRecommendationService {

    private final CryptoDataProvider cryptoDataProvider;

    public List<CryptoSymbol> findNormalizedRangeSortedList() {
        return findCryptoToNormalizedRange()
                .sorted(getComparatorForNormalizedRate())
                .map(getCryptoName())
                .toList();
    }

    public Crypto findCryptoByNameAndPriceOption(@NonNull final CryptoSymbol symbol, @NonNull final PriceOption priceOption) {
        final List<Crypto> cryptoValues = cryptoDataProvider.findCryptoValues(symbol, null, null);
        return switch (priceOption) {
            case MAX -> findMaxCryptoValue(cryptoValues);
            case MIN -> findMinCryptoValue(cryptoValues);
            case NEWEST -> findNewestCrypto(cryptoValues);
            case OLDEST -> findOldestCrypto(cryptoValues);
        };
    }

    public CryptoSymbol findMaxRangeCrypto(@NonNull final LocalDate date) {
        Assert.notNull(date, "date must not be null.");
        return findCryptoToNormalizedRange(date)
                .max(getComparatorForNormalizedRate())
                .map(getCryptoName())
                .orElseThrow(() -> new IllegalArgumentException("There was not found the max range crypto"));
    }

    private Stream<Pair<CryptoSymbol, BigDecimal>> findCryptoToNormalizedRange(final LocalDate date) {
        return getCryptosValues(date).stream()
                .filter(isCryptoValuesFound())
                .map(cryptoResult -> calculateCryptoNormalizedRate(date, cryptoResult))
                .filter(Objects::nonNull);
    }

    private Stream<Pair<CryptoSymbol, BigDecimal>> findCryptoToNormalizedRange() {
        return findCryptoToNormalizedRange(null);
    }

    private List<Pair<CryptoSymbol, List<Crypto>>> getCryptosValues(final LocalDate date) {
        final Integer year = Optional.ofNullable(date).map(LocalDate::getYear).orElse(null);
        final Month month = Optional.ofNullable(date).map(LocalDate::getMonth).orElse(null);
        return cryptoDataProvider.findCryptosValues(year, month);
    }

    private Function<Pair<CryptoSymbol, BigDecimal>, CryptoSymbol> getCryptoName() {
        return Pair::getLeft;
    }

    private Comparator<Pair<CryptoSymbol, BigDecimal>> getComparatorForNormalizedRate() {
        return Comparator.comparing(Pair::getRight);
    }

    private Pair<CryptoSymbol, BigDecimal> calculateCryptoNormalizedRate(final LocalDate date,
                                                                         final Pair<CryptoSymbol, List<Crypto>> cryptoResult) {
        final CryptoSymbol cryptoName = cryptoResult.getLeft();
        List<Crypto> cryptoValues = cryptoResult.getRight();
        if (ObjectUtils.isNotEmpty(date)) {
            cryptoValues = findCryptoValuesByDay(date, cryptoValues);
        }
        if (CollectionUtils.isEmpty(cryptoValues)) {
            return null;
        }
        return Pair.of(cryptoName, findNormalizedRange(cryptoValues));
    }

    private Predicate<Pair<CryptoSymbol, List<Crypto>>> isCryptoValuesFound() {
        return cryptoResult -> CollectionUtils.isNotEmpty(cryptoResult.getRight());
    }

    private List<Crypto> findCryptoValuesByDay(final LocalDate date, final List<Crypto> cryptoValues) {
        return cryptoValues.stream()
                .filter(cryptoPriceModel -> isCryptoValueDayEquals(date, cryptoPriceModel))
                .toList();
    }

    private BigDecimal findNormalizedRange(final List<Crypto> cryptoValuesForSpecificDay) {
        final BigDecimal maxCrypto = findMaxCryptoValue(cryptoValuesForSpecificDay).getPrice();
        final BigDecimal minCrypto = findMinCryptoValue(cryptoValuesForSpecificDay).getPrice();
        return maxCrypto.subtract(minCrypto).divide(minCrypto, NumberUtils.INTEGER_TWO, RoundingMode.HALF_UP);
    }

    private boolean isCryptoValueDayEquals(LocalDate date, Crypto cryptoPriceModel) {
        return cryptoPriceModel.getTimestamp().toLocalDate().isEqual(date);
    }

    private Crypto findOldestCrypto(final List<Crypto> cryptoValues) {
        return cryptoValues.get(cryptoValues.size() - 1);
    }

    private Crypto findNewestCrypto(final List<Crypto> cryptoValues) {
        return cryptoValues.get(0);
    }

    private Crypto findMaxCryptoValue(final List<Crypto> cryptoValues) {
        return cryptoValues.stream()
                .max(Comparator.comparing(Crypto::getPrice))
                .orElseThrow(() -> new IllegalArgumentException("There was not found the max value for %s".formatted(cryptoValues)));
    }

    private Crypto findMinCryptoValue(final List<Crypto> cryptoValues) {
        return cryptoValues.stream()
                .min(Comparator.comparing(Crypto::getPrice))
                .orElseThrow(() -> new IllegalArgumentException("There was not found the min value for %s".formatted(cryptoValues)));
    }

}
