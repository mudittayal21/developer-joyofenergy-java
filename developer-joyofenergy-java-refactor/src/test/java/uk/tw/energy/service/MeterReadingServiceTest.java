package uk.tw.energy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.model.MeterReading;
import uk.tw.energy.service.impl.MeterReadingServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MeterReadingServiceTest {

    private MeterReadingService meterReadingService;

    @BeforeEach
    public void setUp() {
        meterReadingService = new MeterReadingServiceImpl(new HashMap<>());
    }

    @Test
    public void givenMeterIdThatDoesNotExistShouldReturnNull() {
        assertThat(meterReadingService.getReadings("unknown-id")).isEqualTo(Optional.empty());
    }

    @Test
    public void givenMeterReadingThatExistsShouldReturnMeterReadings() {
        MeterReading meterReading = new MeterReading("random-id", new ArrayList<>());

        meterReadingService.storeReadings(meterReading);
        assertThat(meterReadingService.getReadings("random-id")).isEqualTo(Optional.of(new ArrayList<>()));
    }
}
