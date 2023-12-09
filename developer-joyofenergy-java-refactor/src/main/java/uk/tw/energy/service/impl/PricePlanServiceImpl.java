package uk.tw.energy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.tw.energy.helper.CostCalculatorHelper;
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
    @Autowired
    private MeterReadingService meterReadingService;

    public PricePlanServiceImpl(List<PricePlan> pricePlans) {
        this.pricePlans = pricePlans;
    }

    @Override
    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(pricePlans.stream().collect(
                Collectors.toMap(PricePlan::getPlanName, t -> CostCalculatorHelper.calculateCost(electricityReadings.get(), t))
        ));
    }
}
