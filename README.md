# Delivery Drone Dispatch — Strategy Pattern

Complete Java 17 coursework project: two interchangeable algorithm families, six concrete strategies, two contexts, two selectors, external weather abstraction, JUnit 5, genuine Git history.

## Start here / Осыдан бастаңыз

1. Open this folder as a Maven project in IntelliJ IDEA; select JDK 17.
2. Run `mvn test` to execute the complete test suite.
3. Run `mvn compile exec:java` to execute `com.dronedispatch.app.Demo`.
4. Read [the bilingual explanation](docs/EXPLANATION_EN_KZ.md), [full structure](docs/STRUCTURE.md), [class responsibilities](docs/CLASS_RESPONSIBILITIES.md).
5. Inspect [UML source](docs/class-diagram.puml) or [rendered UML](docs/class-diagram.svg).

Қалтаны IntelliJ IDEA бағдарламасында Maven жобасы ретінде ашыңыз. JDK 17 таңдаңыз. `mvn test` тесттерді іске қосады; `mvn compile exec:java` демонстрацияны көрсетеді. Қосымша сервер, дерекқор, API кілті қажет емес. Алғашқы Maven іске қосылуында тәуелділіктерді жүктеу үшін интернет керек.

## Algorithms / Алгоритмдер

All prices use fictional integer minor currency units. `ceilKg = (weightGrams + 999) / 1000` using integer division.

| Family | Strategy | Actual logic |
|---|---|---|
| Pricing | DistancePricing | `500 + 120 × distanceKm` |
| Pricing | PayloadBandPricing | `700 + 250 × ceilKg`; distance-independent weight bands |
| Pricing | WeatherEnergyPricing | `300 + distanceKm × ceilKg × (20 + windKmh)` |
| Assignment | NearestDroneStrategy | Minimize pickup distance among eligible drones |
| Assignment | BestFitPayloadStrategy | Minimize sufficient capacity, equivalent to minimizing unused capacity |
| Assignment | LongestIdleStrategy | Maximize idle duration among eligible drones |

These are different computations, not percentage variants. Assignment selects one drone with `min`; it does not sort navigation routes.

## Selector rules / Таңдау ережелері

First matching rule wins; no match uses the fallback. Selection conditions live in selector policy lists. Contexts contain no `if`, `switch`, `instanceof`, registry lookup, weather query, or selection rule.

| Selector | Priority | Condition | Result |
|---|---:|---|---|
| PricingSelector | 1 | wind ≥ 30 km/h | WeatherEnergyPricing |
| PricingSelector | 2 | payload > 2000 g | PayloadBandPricing |
| PricingSelector | fallback | previous rules did not match | DistancePricing |
| DroneAssignmentSelector | 1 | urgent delivery | NearestDroneStrategy |
| DroneAssignmentSelector | 2 | payload ≥ 3000 g | BestFitPayloadStrategy |
| DroneAssignmentSelector | fallback | previous rules did not match | LongestIdleStrategy |

Manual selection looks up a named registered strategy. Automatic selection evaluates ordered predicates. Stream filtering expresses the selection conditions without a switch. Constructor-injected registries, rule lists, fallback strategies permit extension without editing selector classes.

## Why Strategy, not State or Template Method (English)

This solution uses Strategy because each family represents several alternative algorithms for the same operation. Every pricing class accepts a Delivery and returns a price. Every assignment class accepts a Delivery plus a fleet and returns an optional drone. The caller can substitute any implementation through the same interface. PricingContext and DroneAssignmentContext hold these interfaces rather than concrete implementations, so delegation remains unchanged when the selected algorithm changes.

The contexts receive their initial strategies through constructors. Their setStrategy methods replace those objects while the application is running. The demo deliberately reuses the same context instance and input to make the replacement visible. The selectors handle a separate responsibility: choosing an algorithm from the current situation. Algorithms calculate results; contexts delegate; selectors choose. The weather interface separates an unstable external dependency from the energy calculation, allowing a fixed fake in tests.

This is not State because the strategy objects do not represent lifecycle states such as waiting, flying, delivered, or charging. They do not perform state transitions or replace themselves after completing an operation. An urgent delivery can influence an external selection rule, but that does not turn the algorithms into a state machine. Manual selection remains possible independently of any lifecycle.

This is not Template Method because there is no superclass defining a fixed algorithm skeleton with overridable steps. The implementations provide complete alternative algorithms behind interfaces. They are composed into contexts instead of inherited by contexts. DroneEligibility is a shared feasibility helper, not an abstract parent algorithm. New implementations can be registered through new configuration without changing existing strategies or contexts. The Open/Closed Principle therefore applies to algorithm extension while genuine business-rule changes remain explicit configuration decisions.

## Неге Strategy, State немесе Template Method емес? (Қазақша)

Бұл жоба Strategy үлгісін қолданады, себебі әр топ бір жұмысты бірнеше бөлек алгоритммен орындайды. Баға стратегиялары Delivery объектісін алып, баға қайтарады. Дрон таңдау стратегиялары жеткізу дерегі мен дрондар тізімін алып, жарамды дронды қайтарады. Барлық алгоритм ортақ интерфейс арқылы шақырылады. Сондықтан контекст нақты алгоритмнің қалай жұмыс істейтінін білмейді.

Контекст бастапқы стратегияны конструктор арқылы қабылдайды. setStrategy әдісі бағдарлама жұмыс істеп тұрған кезде оны ауыстырады. Демонстрацияда сол контекст пен сол кіріс деректері сақталып, тек стратегия өзгереді. Selector жағдайға қарап алгоритмді таңдайды. Стратегия есептейді, контекст жұмысты соған тапсырады, selector таңдауды жасайды. WeatherSource сыртқы ауа райы дерегін есептеу логикасынан бөледі. Тестте оның орнына тұрақты мән беретін fake қолданылады.

Бұл State емес: стратегиялар «күту», «ұшу», «жеткізілді» сияқты өмірлік цикл күйлерін білдірмейді. Олар өздігінен келесі күйге өтпейді, контексттің стратегиясын өздері ауыстырмайды. Шұғылдық сыртқы таңдау ережесіне әсер етуі мүмкін, бірақ ол алгоритмдерді күй машинасына айналдырмайды. Қолмен таңдау өмірлік циклден тәуелсіз орындала береді.

Бұл Template Method те емес: мұнда алгоритм қадамдарының тұрақты қаңқасын анықтайтын базалық класс жоқ. Әр стратегия өз алгоритмін толық орындайды. Контекст стратегиядан мұра алмайды, оны өз өрісінде сақтайды; бұл — композиция. DroneEligibility — ортақ жарамдылықты тексеретін көмекші класс, ата-аналық алгоритм емес. Жаңа алгоритмді интерфейсті іске асыру арқылы қосып, жаңа конфигурацияда тіркеуге болады. Бұрынғы стратегиялар мен контексттерді өзгерту қажет емес. Осылайша Open/Closed Principle сақталады.

## Class responsibilities / Кластардың міндеттері

Every class, interface, record, and test class is listed below. Each responsibility is one sentence of at most 25 words; English descriptions omit the four prohibited conjunctions.

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


## Before → after Git evidence

- [Before refactoring commit](history/BEFORE.md): intentional monolithic `PricingService` switch; tag `before-strategy`.
- [After refactoring commit](history/AFTER.md): six strategies, clean contexts, configurable selectors; tag `after-strategy`.
- [Validation evidence](docs/VALIDATION.md): actual commands, results, unchanged-test verification.

These links open local commit records with full hashes and patches. This project has no hosted remote, so no GitHub commit URL is fabricated. The archive includes the actual `.git` repository. A later documentation commit records the two immutable hashes without rewriting them.

```bash
git log --oneline --decorate
git show before-strategy:src/main/java/com/dronedispatch/pricing/PricingService.java
git diff before-strategy after-strategy -- src/test/java/com/dronedispatch/pricing/PricingRegressionTest.java
```

The last command must produce no output. Run the identical regression test at both commits without changing your working copy:

```bash
git worktree add --detach ../drone-before before-strategy
git worktree add --detach ../drone-after after-strategy
mvn -f ../drone-before/pom.xml -Dtest=PricingRegressionTest test
mvn -f ../drone-after/pom.xml -Dtest=PricingRegressionTest test
```

Екі коммиттің де шынайы тарихы архивке кірді. Бірінші нұсқада барлық баға алгоритмі switch ішінде. Екінші нұсқада олар жеке стратегияларға бөлінді. PricingRegressionTest файлы өзгертілмеді; оның 14 тексеруі екі нұсқада да өтті.

## Requirement traceability

| Requirement | Evidence |
|---|---|
| Two families, ≥3 distinct algorithms each | pricing/ and assignment/; six concrete strategy classes |
| Interface with 1–2 methods | Each strategy interface has exactly one operation |
| Constructor injection, setStrategy, pure delegation | PricingContext; DroneAssignmentContext |
| Separate automatic selector | Two selector classes; ordered, configurable rules |
| Bad version followed by refactoring | Actual before-strategy / after-strategy Git tags |
| Same tests pass before / after | Unchanged PricingRegressionTest: 14 successful invocations per version |
| External data behind own interface | WeatherSource → SimulatedWeatherSource; test FakeWeatherSource |
| JUnit 5 strategy tests | PricingStrategiesTest; DroneAssignmentStrategiesTest |
| Every selector rule | Selector tests cover every rule, fallback, exact threshold, overlap priority |
| Parameterized common family check | Nonnegative price checks across all three pricing strategies |
| Demo compares same input | Demo: 1700/1200/900; A/B/C |
| Manual vs automatic; runtime switching | Demo reuses both context instances with setStrategy |
| Responsibility constraint | Linked bilingual class table |
| Strategy vs State / Template Method | English explanation plus Kazakh explanation above |
| Refactoring commit links | Local commit records with full hashes, patches, real repository |
| Forbidden examples avoided | Drone pricing/assignment; no payment, calculator app, route sorting |

## Scope and input contract

Delivery distance: 0–1000 km; payload: 1–20000 g; wind: 0–100 km/h. Drone capacity: 1–20000 g; pickup distance: 0–1000 km; usable range: 0–5000 km; idle time: 0–1000000 minutes. IDs must have 1–100 characters; callers supply unique IDs. Invalid bounded values throw IndexOutOfBoundsException through Objects.checkIndex; null required inputs throw NullPointerException; unknown selection keys throw IllegalArgumentException.

All assignment strategies first require an available drone, sufficient payload capacity, and `rangeKm ≥ pickupKm + 2 × delivery.distanceKm`. Equal ranking scores use ascending lexical drone ID. No eligible drone produces Optional.empty. Input lists are never sorted or mutated. The demo selects candidates only; it does not reserve drones or control aircraft. The weather energy formula is a teaching cost model, not flight-safety certification. Manual selection bypasses automatic policy, not eligibility checks.
