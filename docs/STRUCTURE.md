# Full project structure / Жобаның толық құрылымы

```text
delivery-drone-dispatch/
  pom.xml
  .gitignore
  README.md
  src/main/java/com/dronedispatch/app/Demo.java
  src/main/java/com/dronedispatch/assignment/BestFitPayloadStrategy.java
  src/main/java/com/dronedispatch/assignment/DroneAssignmentContext.java
  src/main/java/com/dronedispatch/assignment/DroneAssignmentSelector.java
  src/main/java/com/dronedispatch/assignment/DroneAssignmentStrategy.java
  src/main/java/com/dronedispatch/assignment/DroneEligibility.java
  src/main/java/com/dronedispatch/assignment/LongestIdleStrategy.java
  src/main/java/com/dronedispatch/assignment/NearestDroneStrategy.java
  src/main/java/com/dronedispatch/model/Delivery.java
  src/main/java/com/dronedispatch/model/Drone.java
  src/main/java/com/dronedispatch/pricing/DistancePricing.java
  src/main/java/com/dronedispatch/pricing/PayloadBandPricing.java
  src/main/java/com/dronedispatch/pricing/PricingContext.java
  src/main/java/com/dronedispatch/pricing/PricingSelector.java
  src/main/java/com/dronedispatch/pricing/PricingService.java
  src/main/java/com/dronedispatch/pricing/PricingStrategy.java
  src/main/java/com/dronedispatch/pricing/WeatherEnergyPricing.java
  src/main/java/com/dronedispatch/selection/SelectionRule.java
  src/main/java/com/dronedispatch/weather/SimulatedWeatherSource.java
  src/main/java/com/dronedispatch/weather/WeatherSource.java
  src/test/java/com/dronedispatch/assignment/DroneAssignmentSelectorTest.java
  src/test/java/com/dronedispatch/assignment/DroneAssignmentStrategiesTest.java
  src/test/java/com/dronedispatch/model/ModelValidationTest.java
  src/test/java/com/dronedispatch/pricing/PricingRegressionTest.java
  src/test/java/com/dronedispatch/pricing/PricingSelectorTest.java
  src/test/java/com/dronedispatch/pricing/PricingStrategiesTest.java
  src/test/java/com/dronedispatch/support/FakeWeatherSource.java
  docs/class-diagram.puml
  docs/class-diagram.svg
  docs/class-diagram.png
  docs/CLASS_RESPONSIBILITIES.md
  docs/EXPLANATION_EN_KZ.md
  docs/STRUCTURE.md
  docs/VALIDATION.md
  docs/before-tests.txt
  docs/after-tests.txt
  docs/demo-output.txt
  history/BEFORE.md
  history/AFTER.md
  history/before.patch
  history/after.patch
  .git/ (actual Git history)
```

`src/main/java` is the production source root; `src/test/java` is the test source root. Open `pom.xml` as a Maven project so IntelliJ configures both automatically. Java package paths must stay intact. `target/` is generated, ignored, and omitted from the archive.

Негізгі кодтың түбірі — `src/main/java`, тесттердің түбірі — `src/test/java`. `pom.xml` файлын Maven жобасы ретінде ашсаңыз, IntelliJ оларды өзі белгілейді. Package қалталарын өзгертпеңіз. `target/` автоматты жасалады, архивке кірмейді.
