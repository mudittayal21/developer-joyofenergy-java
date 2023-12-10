package uk.tw.energy.service.impl;

import org.springframework.stereotype.Service;
import uk.tw.energy.helper.CostCalculator;
import uk.tw.energy.model.ElectricityReading;
import uk.tw.energy.model.PricePlan;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PricePlanServiceImpl implements PricePlanService {

    private final List<PricePlan> pricePlans;
    private final MeterReadingService meterReadingService;

    public PricePlanServiceImpl(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
        this.pricePlans = pricePlans;
        this.meterReadingService = meterReadingService;
    }

    @Override
    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(pricePlans.stream().collect(
                Collectors.toMap(PricePlan::getPlanName, t -> CostCalculator.calculateCost(electricityReadings.get(), t))
        ));
    }
}
