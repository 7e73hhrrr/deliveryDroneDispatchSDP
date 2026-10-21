# Class responsibilities / Кластардың міндеттері

Every responsibility is one sentence under 25 words. English responsibilities omit the four prohibited conjunctions.

| Class / interface | Responsibility (EN) | Міндеті (KZ) |
|---|---|---|
| `Delivery` | Stores validated delivery distance, payload weight, urgency. | Жеткізу қашықтығын, жүк салмағын, шұғылдығын сақтайды. |
| `Drone` | Stores validated drone identity, capacity, pickup distance, range, idle time, availability. | Дронның атауын, жүк көтерімділігін, қашықтығын, ұшу қорын, бос уақытын сақтайды. |
| `PricingStrategy` | Defines the interchangeable delivery quotation contract. | Жеткізу бағасын есептеу келісімшартын анықтайды. |
| `DistancePricing` | Calculates delivery cost from travelled distance. | Бағаны жеткізу қашықтығы арқылы есептейді. |
| `PayloadBandPricing` | Calculates delivery cost using started kilogram bands. | Бағаны әр басталған килограмм бойынша есептейді. |
| `WeatherEnergyPricing` | Estimates delivery cost from payload, distance, external wind observations. | Бағаны салмақ, қашықтық, сыртқы жел дерегі арқылы есептейді. |
| `PricingContext` | Delegates quotations to its replaceable pricing strategy. | Баға есептеу жұмысын ауыстырылатын стратегияға тапсырады. |
| `PricingSelector` | Selects pricing strategies through registered keys, ordered conditions, fallback policy. | Баға стратегиясын атауы немесе жағдай ережелері бойынша таңдайды. |
| `PricingService` | Preserves the original quotation entry point throughout refactoring. | Рефакторинг кезінде бастапқы баға сұрау интерфейсін сақтайды. |
| `DroneAssignmentStrategy` | Defines the interchangeable drone assignment contract. | Дрон таңдау келісімшартын анықтайды. |
| `NearestDroneStrategy` | Selects the eligible drone closest to the pickup point. | Жүкті алу нүктесіне ең жақын жарамды дронды таңдайды. |
| `BestFitPayloadStrategy` | Selects the eligible drone with the smallest sufficient payload capacity. | Жүкті көтере алатын ең шағын сыйымдылықты дронды таңдайды. |
| `LongestIdleStrategy` | Selects the eligible drone with the longest idle duration. | Ең ұзақ уақыт бос тұрған жарамды дронды таңдайды. |
| `DroneEligibility` | Filters drones using availability, payload capacity, remaining flight range. | Қолжетімділік, жүк көтерімділік, ұшу қоры бойынша дрондарды сүзеді. |
| `DroneAssignmentContext` | Delegates assignment to its replaceable drone strategy. | Дрон таңдау жұмысын ауыстырылатын стратегияға тапсырады. |
| `DroneAssignmentSelector` | Selects drone strategies through registered keys, ordered conditions, fallback policy. | Дрон стратегиясын атауы немесе жағдай ережелері бойынша таңдайды. |
| `SelectionRule<S>` | Pairs a situation predicate with a strategy instance. | Жағдай шартын нақты стратегиямен байланыстырады. |
| `WeatherSource` | Defines the external wind observation boundary. | Жел туралы сыртқы дерек көзінің интерфейсін анықтайды. |
| `SimulatedWeatherSource` | Supplies a validated simulated wind observation. | Тексерілген жасанды жел мәнін береді. |
| `Demo` | Demonstrates comparable inputs, manual selection, automatic selection, runtime replacement. | Бірдей кірісті, қолмен таңдауды, автоматты таңдауды, орындау кезіндегі ауысуды көрсетеді. |
| `FakeWeatherSource` | Supplies deterministic wind observations during tests. | Тест кезінде тұрақты жел мәнін береді. |
| `PricingRegressionTest` | Verifies unchanged quotation behaviour across both refactoring commits. | Екі коммиттегі баға есептеу нәтижелерінің өзгермегенін тексереді. |
| `PricingStrategiesTest` | Checks each pricing algorithm, external observations, nonnegative quotations. | Баға алгоритмдерін, сыртқы деректі, теріс емес нәтижелерді тексереді. |
| `PricingSelectorTest` | Checks pricing rules, boundaries, precedence, extensibility, context replacement. | Баға таңдау ережелерін, шекараларын, басымдығын, кеңейтуді, ауыстыруды тексереді. |
| `DroneAssignmentStrategiesTest` | Checks assignment algorithms, eligibility, tie resolution, empty fleets, context replacement. | Дрон алгоритмдерін, жарамдылықты, тең нәтижелерді, бос тізімді, ауыстыруды тексереді. |
| `DroneAssignmentSelectorTest` | Checks assignment rules, boundaries, precedence, registration, fallback selection. | Дрон таңдау ережелерін, шекараларын, басымдығын, тіркеуді, әдепкі таңдауды тексереді. |
| `ModelValidationTest` | Checks domain bounds plus simulated weather validation. | Домен мәндерінің шектерін, жасанды ауа райының дұрыстығын тексереді. |
