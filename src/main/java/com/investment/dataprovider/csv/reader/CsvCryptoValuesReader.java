package com.investment.dataprovider.csv.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import com.investment.dataprovider.csv.model.CsvCryptoValuesHeader;
import com.investment.service.model.Crypto;
import com.investment.service.model.CryptoSymbol;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Log4j2
@Component
@RequiredArgsConstructor
public class CsvCryptoValuesReader {

    private static final String CRYPTO_PRICE_FILE_PATH_PATTERN = "/crypto-prices/%s/%s/%s_values.csv";
    private static final int DEFAULT_SEARCH_YEAR = 2022;
    private static final String DEFAULT_SEARCH_MONTH = "january";

    private final ResourceLoader resourceLoader;
    private final CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader(CsvCryptoValuesHeader.class)
            .setTrim(true)
            .setIgnoreHeaderCase(true)
            .setSkipHeaderRecord(true)
            .build();

    public List<Crypto> read(@NonNull final CryptoSymbol symbol, @Nullable final Integer year, @Nullable final Month month) throws IOException {
        Assert.notNull(symbol, "symbol name must not be null.");

        final String filePath = CRYPTO_PRICE_FILE_PATH_PATTERN.formatted(getCryptoPricesYear(year), getCryptoPricesMonth(month), symbol);
        final Resource resource = loadResourceForPath(filePath);
        if (resource.exists()) {
            final URI fileUri = resource.getURI();
            final Reader reader = Files.newBufferedReader(Paths.get(fileUri));
            final Iterable<CSVRecord> records = csvFormat.parse(reader);
            final List<Crypto> cryptoPrices = new LinkedList<>();
            for (CSVRecord record : records) {
                cryptoPrices.add(buildCryptoPrice(record));
            }
            return cryptoPrices;
        } else {
            log.info("There is no information for {} values for {} year and {} month.", symbol, year, month);
            return List.of();
        }
    }

    private String getCryptoPricesMonth(Month month) {
        return Optional.ofNullable(month)
                .map(Month::toString)
                .map(String::toLowerCase)
                .orElse(DEFAULT_SEARCH_MONTH);
    }

    private int getCryptoPricesYear(Integer year) {
        return Optional.ofNullable(year)
                .orElse(DEFAULT_SEARCH_YEAR);
    }

    private Crypto buildCryptoPrice(final CSVRecord record) {
        return Crypto.builder()
                .timestamp(convertTimestempToOffsetDateTime(record.get(CsvCryptoValuesHeader.TIMESTAMP)))
                .price(new BigDecimal(record.get(CsvCryptoValuesHeader.PRICE)))
                .cryptoName(CryptoSymbol.fromValue(record.get(CsvCryptoValuesHeader.SYMBOL)))
                .build();
    }

    private OffsetDateTime convertTimestempToOffsetDateTime(final String timestamp) {
        final Instant instant = Instant.ofEpochMilli(Long.parseLong(timestamp));
        return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private Resource loadResourceForPath(final String filePath) {
        return resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + filePath);
    }
}
