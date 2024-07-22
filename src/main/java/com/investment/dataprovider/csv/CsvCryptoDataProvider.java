package com.investment.dataprovider.csv;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;
import com.investment.dataprovider.CryptoDataProvider;
import com.investment.dataprovider.csv.reader.CsvCryptoValuesReader;
import com.investment.service.exeption.ApplicationException;
import com.investment.service.model.Crypto;
import com.investment.service.model.CryptoSymbol;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CsvCryptoDataProvider implements CryptoDataProvider {
    private final CsvCryptoValuesReader cryptoCSVReader;

    @Override
    public List<Crypto> findCryptoValues(@NonNull final CryptoSymbol symbol, @Nullable final Integer year, @Nullable final Month month) {
        try {
            return cryptoCSVReader.read(symbol, year, month);
        } catch (IOException e) {
            log.error("An exception occurred while reading the {} values for year: {} and month: {}", symbol, year, month, e);
            throw new ApplicationException(e);
        }
    }

    @Override
    public List<Pair<CryptoSymbol, List<Crypto>>> findCryptosValues(@Nullable final Integer year, @Nullable final Month month) {
        return Stream.of(CryptoSymbol.values())
                .map(crypto -> Pair.of(crypto, findCryptoValues(crypto, year, month)))
                .toList();
    }

}
