package uk.tw.energy.service.impl;

import org.springframework.stereotype.Service;
import uk.tw.energy.model.ElectricityReading;
import uk.tw.energy.model.MeterReading;
import uk.tw.energy.service.MeterReadingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MeterReadingServiceImpl implements MeterReadingService {

    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingServiceImpl(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    @Override
    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    @Override
    public void storeReadings(MeterReading meterReading) {
        if (!meterAssociatedReadings.containsKey(meterReading.smartMeterId())) {
            meterAssociatedReadings.put(meterReading.smartMeterId(), new ArrayList<>());
        }
        meterAssociatedReadings.get(meterReading.smartMeterId()).addAll(meterReading.electricityReadings());
    }
}
