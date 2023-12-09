package uk.tw.energy.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public interface PricePlanService {
    Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId);
}
