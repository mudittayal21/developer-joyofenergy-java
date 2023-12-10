package uk.tw.energy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.model.ElectricityReading;
import uk.tw.energy.model.MeterReading;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.impl.MeterReadingServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MeterReadingControllerTest {

    private static final String SMART_METER_ID = "10101010";
    private MeterReadingController meterReadingController;
    private MeterReadingService meterReadingService;

    @BeforeEach
    public void setUp() {
        this.meterReadingService = new MeterReadingServiceImpl(new HashMap<>());
        this.meterReadingController = new MeterReadingController(meterReadingService);
    }

    @Test
    public void givenNoMeterIdIsSuppliedWhenStoringShouldReturnErrorResponse() {
        MeterReading meterReading = new MeterReading(null, Collections.emptyList());
        assertThat(meterReadingController.storeReadings(meterReading).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void givenEmptyMeterReadingShouldReturnErrorResponse() {
        MeterReading meterReading = new MeterReading(SMART_METER_ID, Collections.emptyList());
        assertThat(meterReadingController.storeReadings(meterReading).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void givenNullReadingsAreSuppliedWhenStoringShouldReturnErrorResponse() {
        MeterReading meterReading = new MeterReading(SMART_METER_ID, null);
        assertThat(meterReadingController.storeReadings(meterReading).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void givenMultipleBatchesOfMeterReadingsShouldStore() {
        MeterReading meterReading = new MeterReadingsBuilder().setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        MeterReading otherMeterReading = new MeterReadingsBuilder().setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        meterReadingController.storeReadings(meterReading);
        meterReadingController.storeReadings(otherMeterReading);

        List<ElectricityReading> expectedElectricityReadings = new ArrayList<>();
        expectedElectricityReadings.addAll(meterReading.electricityReadings());
        expectedElectricityReadings.addAll(otherMeterReading.electricityReadings());

        assertThat(meterReadingService.getReadings(SMART_METER_ID).get()).isEqualTo(expectedElectricityReadings);
    }

    @Test
    public void givenMeterReadingsAssociatedWithTheUserShouldStoreAssociatedWithUser() {
        MeterReading meterReading = new MeterReadingsBuilder().setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        MeterReading otherMeterReading = new MeterReadingsBuilder().setSmartMeterId("00001")
                .generateElectricityReadings()
                .build();

        meterReadingController.storeReadings(meterReading);
        meterReadingController.storeReadings(otherMeterReading);

        assertThat(meterReadingService.getReadings(SMART_METER_ID).get()).isEqualTo(meterReading.electricityReadings());
    }

    @Test
    public void givenMeterIdThatIsNotRecognisedShouldReturnNotFound() {
        assertThat(meterReadingController.readReadings(SMART_METER_ID).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
