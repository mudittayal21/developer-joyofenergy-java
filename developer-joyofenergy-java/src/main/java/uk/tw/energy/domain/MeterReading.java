package uk.tw.energy.domain;

import java.util.List;

public record MeterReading(String smartMeterId, List<ElectricityReading> electricityReadings) {

}
