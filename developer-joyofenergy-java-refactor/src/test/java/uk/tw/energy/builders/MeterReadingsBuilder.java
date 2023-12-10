package uk.tw.energy.builders;

import uk.tw.energy.helper.ElectricityReadingsGenerator;
import uk.tw.energy.model.ElectricityReading;
import uk.tw.energy.model.MeterReading;

import java.util.ArrayList;
import java.util.List;

public class MeterReadingsBuilder {

    private static final String DEFAULT_METER_ID = "id";

    private String smartMeterId = DEFAULT_METER_ID;
    private List<ElectricityReading> electricityReadings = new ArrayList<>();

    public MeterReadingsBuilder setSmartMeterId(String smartMeterId) {
        this.smartMeterId = smartMeterId;
        return this;
    }

    public MeterReadingsBuilder generateElectricityReadings() {
        return generateElectricityReadings(5);
    }

    public MeterReadingsBuilder generateElectricityReadings(int number) {
        this.electricityReadings = ElectricityReadingsGenerator.generate(number);
        return this;
    }

    public MeterReading build() {
        return new MeterReading(smartMeterId, electricityReadings);
    }
}
