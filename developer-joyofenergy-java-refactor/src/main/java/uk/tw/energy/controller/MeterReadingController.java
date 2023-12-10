package uk.tw.energy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.model.ElectricityReading;
import uk.tw.energy.model.MeterReading;
import uk.tw.energy.service.MeterReadingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/readings")
public class MeterReadingController {
    private final MeterReadingService meterReadingService;
    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    @PostMapping("/store")
    public ResponseEntity storeReadings(@RequestBody MeterReading meterReading) {
        if (!meterReading.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        meterReadingService.storeReadings(meterReading);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity readReadings(@PathVariable String smartMeterId) {
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
        return readings.isPresent()
                ? ResponseEntity.ok(readings.get())
                : ResponseEntity.notFound().build();
    }
}
