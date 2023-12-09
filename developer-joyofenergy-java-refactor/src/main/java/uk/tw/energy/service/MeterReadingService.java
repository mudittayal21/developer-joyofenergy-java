package uk.tw.energy.service;

import uk.tw.energy.model.ElectricityReading;
import uk.tw.energy.model.MeterReadings;

import java.util.List;
import java.util.Optional;

public interface MeterReadingService {
    Optional<List<ElectricityReading>> getReadings(String smartMeterId);
    void storeReadings(MeterReadings meterReadings);
}
