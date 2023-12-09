# Thought Works

## *Change Log - 2023-12-09*

1. *Updating the Directory Structure*
    
    *Old*
    
    ![Untitled](Thought%20Works%20dc4cb923312240fea243065a8904db85/Untitled.png)
    
    *New*
    
    ![Untitled](Thought%20Works%20dc4cb923312240fea243065a8904db85/Untitled%201.png)
    

1. `Meter Reading Record Class`
    1. Adding the logic to validate the meter reading entry. This would encapsulate the logic within the class where the data is stored.
    
    ```java
    public boolean isValid() {
        return smartMeterId != null && !smartMeterId.isEmpty() &&
                electricityReadings != null && !electricityReadings.isEmpty();
    }
    ```
    

1. `MeterReadingController.java` and `MeterReadings.java`
    1. Changing the `Controller Injection` to `@Autowired`, in order to make the code more concise and easier to read.
        1. *QUESTION: What is the advantage of the previous syntax being used?*
        
        *Old*
        
        ```java
        private final MeterReadingService meterReadingService;
        public MeterReadingController(MeterReadingService meterReadingService) {
            this.meterReadingService = meterReadingService;
        }
        ```
        
        *New*
        
        ```java
        @Autowired
        private MeterReadingService meterReadingService;
        ```
        
    2. *Store Readings Method*
        1. Replacing the `isMeterReadingsValid` method call with the centralized logic at the Record Level. Deleting the method `isMeterReadingsValid` from the class.
        2. Updating the Error from `INTERNAL_SERVER_ERROR` to `BAD_REQUEST` whenever a invalid entry is submitted by the API Consuming Device.
        
        *Old*
        
        ```java
        @PostMapping("/store")
        public ResponseEntity storeReadings(@RequestBody MeterReadings meterReadings) {
            if (!isMeterReadingsValid(meterReadings)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
            return ResponseEntity.ok().build();
        }
        ```
        
        *New*
        
        ```java
        @PostMapping("/store")
        public ResponseEntity storeReadings(@RequestBody MeterReadings meterReadings) {
            if (!meterReadings.isValid()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
            return ResponseEntity.ok().build();
        }
        ```
        

1. `PricePlanComparatorController.java`
    1. Changing the `Controller Injection` to `@Autowired`, in order to make the code more concise and easier to read.
    2. *QUESTION: What is the advantage of the previous syntax being used?*
    
    *Old*
    
    ```java
    private final PricePlanService pricePlanService;
    private final AccountService accountService;
    public PricePlanComparatorController(PricePlanService pricePlanService, AccountService accountService) {
        this.pricePlanService = pricePlanService;
        this.accountService = accountService;
    }
    ```
    
    *New*
    
    ```java
    @Autowired
    private PricePlanService pricePlanService;
    @Autowired
    private AccountService accountService;
    ```
    

1. Updating the Service Classes to an Interface.
    1. `AccountService`
    2. `MeterReadingService`
    3. `PricePlanService`
    
    *****************With this change, we are practicing the Dependency Inversion Principle (DIP) in our code, which states that the*****************
    
    > High Level modules should not depend on the low level modules. Instead, both should depend on Abstraction. Abstraction should not depend on details, but the details should depend upon abstraction.
    > 
    
    *Old*
    
    ```java
    @Service
    public class AccountService {
    
        private final Map<String, String> smartMeterToPricePlanAccounts;
    
        public AccountService(Map<String, String> smartMeterToPricePlanAccounts) {
            this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
        }
    
        public String getPricePlanIdForSmartMeterId(String smartMeterId) {
            return smartMeterToPricePlanAccounts.get(smartMeterId);
        }
    }
    ```
    
    *New*
    
    ```java
    public interface AccountService {
        String getPricePlanIdForSmartMeterId(String smartMeterId);
    }
    
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
    ```
    

1. Updating the Method Signature for the Service Methods to Accept the Entire `MeterReading` Object instead of extracting and passing all the details as separate parameters.
    
    *Old*
    
    ```java
    void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings);
    ```
    
    *New*
    
    ```java
    @Override
    public void storeReadings(MeterReadings meterReadings) {
        if (!meterAssociatedReadings.containsKey(meterReadings.smartMeterId())) {
            meterAssociatedReadings.put(meterReadings.smartMeterId(), new ArrayList<>());
        }
        meterAssociatedReadings.get(meterReadings.smartMeterId()).addAll(meterReadings.electricityReadings());
    }
    ```
    

1. Moving the `CostCalulation` logic to a helper method.
    
    This change follows the Single Responsibility Principle (SRP), which states that the class should have only one reason to chane. Thus, making the code more modular and readable. 
    
    *New*
    
    ```java
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
    ```
    
    ```java
    public class CostCalculatorHelper {
        public static BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
            BigDecimal average = calculateAverageReading(electricityReadings);
            BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);
    
            BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);
            return averagedCost.multiply(pricePlan.getUnitRate());
        }
    
        private static BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
            BigDecimal summedReadings = electricityReadings.stream()
                    .map(ElectricityReading::reading)
                    .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));
    
            return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
        }
    
        private static BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
            ElectricityReading first = electricityReadings.stream()
                    .min(Comparator.comparing(ElectricityReading::time))
                    .get();
    
            ElectricityReading last = electricityReadings.stream()
                    .max(Comparator.comparing(ElectricityReading::time))
                    .get();
    
            return BigDecimal.valueOf(Duration.between(first.time(), last.time()).getSeconds() / 3600.0);
        }
    }
    ```