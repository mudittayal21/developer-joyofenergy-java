package uk.tw.energy.model;

import java.util.List;

public record MeterReading(String smartMeterId, List<ElectricityReading> electricityReadings) {
    public boolean isValid() {
        return smartMeterId != null && !smartMeterId.isEmpty() &&
                electricityReadings != null && !electricityReadings.isEmpty();
    }
}
