package com.investment.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;
import com.investment.dataprovider.CryptoDataProvider;
import com.investment.service.model.Crypto;
import com.investment.service.model.CryptoSymbol;
import com.investment.service.model.PriceOption;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.investment.service.model.CryptoSymbol.BTC;
import static com.investment.service.model.CryptoSymbol.DOGE;
import static com.investment.service.model.CryptoSymbol.ETH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoRecommendationServiceTest {

    @Mock
    private CryptoDataProvider cryptoDataProvider;
    @InjectMocks
    private CryptoRecommendationService underTest;

    private final static Crypto btc1 = Crypto.builder()
            .cryptoName(BTC)
            .price(BigDecimal.valueOf(46813.21))
            .timestamp(OffsetDateTime.of(2022, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC))
            .build();
    private final static Crypto btc2 = Crypto.builder()
            .cryptoName(BTC)
            .price(BigDecimal.valueOf(46979.61))
            .timestamp(OffsetDateTime.of(2022, 1, 1, 10, 0, 0, 0, ZoneOffset.UTC))
            .build();
    private final static Crypto btc3 = Crypto.builder()
            .cryptoName(BTC)
            .price(BigDecimal.valueOf(47143.98))
            .timestamp(OffsetDateTime.of(2022, 1, 2, 1, 0, 0, 0, ZoneOffset.UTC))
            .build();

    private final static Crypto eth1 = Crypto.builder()
            .cryptoName(ETH)
            .price(BigDecimal.valueOf(3819.1))
            .timestamp(OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
            .build();
    private final static Crypto eth2 = Crypto.builder()
            .cryptoName(ETH)
            .price(BigDecimal.valueOf(3792.87))
            .timestamp(OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
            .build();
    @Test
    void findNormalizedRangeSortedList() {
        final List<Pair<CryptoSymbol, List<Crypto>>> cryptosValues = List.of(Pair.of(BTC, List.of(btc1, btc2, btc3)),
                                                                             Pair.of(DOGE, List.of()),
                                                                             Pair.of(ETH, List.of(eth1, eth2)));
        final List<CryptoSymbol> expected = List.of(BTC, ETH);
        when(cryptoDataProvider.findCryptosValues(null, null)).thenReturn(cryptosValues);

        final List<CryptoSymbol> result = underTest.findNormalizedRangeSortedList();

        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{index}. Given price option = {0}, the expected result should be {1}.")
    @MethodSource("providedValues")
    void findCryptoByNameAndPriceOption(final PriceOption priceOption, final Crypto expected) {
        when(cryptoDataProvider.findCryptoValues(BTC, null, null)).thenReturn(List.of(btc1,btc2,btc3));

        final Crypto result = underTest.findCryptoByNameAndPriceOption(BTC, priceOption);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findMaxRangeCrypto() {
        final List<Pair<CryptoSymbol, List<Crypto>>> cryptosValues = List.of(Pair.of(BTC, List.of(btc1, btc2, btc3)),
                                                                             Pair.of(DOGE, List.of()),
                                                                             Pair.of(ETH, List.of(eth1, eth2)));
        when(cryptoDataProvider.findCryptosValues(2022, Month.JANUARY)).thenReturn(cryptosValues);

        final CryptoSymbol result = underTest.findMaxRangeCrypto(LocalDate.of(2022, 1, 1));

        assertThat(result).isEqualTo(ETH);
    }

    private static Stream<Arguments> providedValues() {
        return Stream.of(
                Arguments.of(PriceOption.MAX, btc3),
                Arguments.of(PriceOption.MIN, btc1),
                Arguments.of(PriceOption.OLDEST, btc3),
                Arguments.of(PriceOption.NEWEST, btc1));
    }
}