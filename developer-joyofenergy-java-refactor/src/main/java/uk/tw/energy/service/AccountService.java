package uk.tw.energy.service;

import org.springframework.stereotype.Service;

public interface AccountService {
    String getPricePlanIdForSmartMeterId(String smartMeterId);
}
