package uk.tw.energy.service.impl;

import org.springframework.stereotype.Service;
import uk.tw.energy.service.AccountService;

import java.util.Map;

@Service
public class AccountServiceImpl implements AccountService {
    private final Map<String, String> smartMeterToPricePlanAccounts;

    public AccountServiceImpl(Map<String, String> smartMeterToPricePlanAccounts) {
        this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
    }

    @Override
    public String getPricePlanIdForSmartMeterId(String smartMeterId) {
        return smartMeterToPricePlanAccounts.get(smartMeterId);
    }
}
