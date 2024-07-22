package com.investment.dataprovider;

import java.time.Month;
import java.util.List;
import com.investment.service.model.Crypto;
import com.investment.service.model.CryptoSymbol;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public interface CryptoDataProvider {

    List<Crypto> findCryptoValues(@NonNull CryptoSymbol symbol, @Nullable Integer year, @Nullable Month month);

    List<Pair<CryptoSymbol, List<Crypto>>> findCryptosValues(@Nullable Integer year, @Nullable Month month);
}
