# How the project works / Жоба қалай жұмыс істейді

## 1. Domain choice / Доменді таңдау

**English.** Delivery Drone Dispatch needs to decide how much a delivery costs and which drone should handle it. These are separate operations, so each receives its own strategy family. A price formula should not choose a drone, and a drone selection algorithm should not calculate money. The six concrete classes contain genuinely different algorithms rather than the same formula with different constants.

**Қазақша.** Delivery Drone Dispatch — жеткізуге дрон таңдау жүйесі. Мұнда екі бөлек сұрақ бар: «Жеткізу қанша тұрады?» және «Қай дронды таңдаймыз?». Сондықтан екі стратегия тобы құрылды. Баға алгоритмі дрон таңдамайды, дрон таңдау алгоритмі баға есептемейді. Алты нақты класс бір формуланың пайызын ғана өзгертпейді: олардың есептеу тәсілдері әртүрлі.

## 2. Data objects / Дерек объектілері

**English.** `Delivery` is an immutable Java record containing distance in kilometres, payload in grams, and an urgent flag. `Drone` stores an identifier, payload capacity, distance to the pickup point, currently usable range, idle minutes, and availability. Records provide constructors, accessors such as `weightGrams()`, equality, and readable output. Compact constructors check bounds with `Objects.checkIndex`, so invalid inputs cannot enter calculations. Whole-number units keep examples easy to defend. Prices use `long` minor units, avoiding floating-point rounding.

**Қазақша.** `Delivery` — өзгермейтін Java record. Ол қашықтықты, жүк салмағын, тапсырыстың шұғылдығын сақтайды. `Drone` дронның атауын, жүк көтерімділігін, жүкті алу нүктесіне дейінгі қашықтығын, қалған ұшу қорын, бос тұрған минуттарын, қолжетімділігін сақтайды. Record конструкторды, `weightGrams()` сияқты оқу әдістерін, салыстыруды, мәтіндік көріністі береді. Конструктордағы тексерулер қате мәндерді қабылдамайды. Баға `long` түріндегі ең кіші ақша бірлігімен есептеледі, сондықтан бөлшек санды дөңгелектеу мәселесі жоқ.

## 3. Pricing family / Баға стратегиялары

**English.** `PricingStrategy` declares only `long quote(Delivery delivery)`. `DistancePricing` adds a base fee to a per-kilometre fee. `PayloadBandPricing` charges each started kilogram: 1000 g occupies one band, while 1001 g occupies two. Integer arithmetic `(grams + 999) / 1000` performs this rounding upward. `WeatherEnergyPricing` estimates effort from distance multiplied by started kilograms, then uses wind to change the effort cost. Thus the first strategy follows distance, the second uses weight bands independently of distance, and the third combines distance, weight, and external weather.

For a 10 km delivery weighing 1500 g with 10 km/h wind:

| Strategy | Substitution | Result |
|---|---|---:|
| Distance | `500 + 120 × 10` | 1700 |
| Payload bands | `700 + 250 × 2` | 1200 |
| Weather energy | `300 + 10 × 2 × (20 + 10)` | 900 |

**Қазақша.** `PricingStrategy` интерфейсінде бір ғана `quote(Delivery delivery)` әдісі бар. `DistancePricing` бастапқы төлемге әр километрдің құнын қосады. `PayloadBandPricing` әр басталған килограмм үшін есептейді: 1000 грамм — бір бөлік, 1001 грамм — екі бөлік. `(grams + 999) / 1000` бүтін санмен жоғары қарай дөңгелектейді. `WeatherEnergyPricing` қашықтық пен салмақ арқылы күш жұмсауды бағалап, желді есепке алады. 10 км, 1500 г, 10 км/сағ жел үшін нәтижелер: 1700, 1200, 900. Бұл нақты коммерциялық тарифтер емес, үлгіні түсіндіруге арналған ережелер.

## 4. Drone assignment family / Дрон таңдау стратегиялары

**English.** `DroneAssignmentStrategy` declares `Optional<Drone> assign(Delivery delivery, List<Drone> fleet)`. `Optional` means a drone might not exist. `DroneEligibility` first excludes busy drones, drones unable to carry the parcel, and drones without enough range. Required range is pickup distance plus twice the delivery distance: reach the parcel, fly to the recipient, then return to the pickup point. This is an explicit simplified trip model.

After filtering, `NearestDroneStrategy` minimizes pickup distance. `BestFitPayloadStrategy` minimizes available capacity among sufficient drones, preserving larger drones for larger parcels. For a fixed parcel weight, minimizing capacity also minimizes unused capacity. `LongestIdleStrategy` maximizes idle minutes to prefer the drone that has waited longest. `Comparator.reversed()` reverses the idle comparison; the `min()` call then selects the longest wait. Each comparator ends with `thenComparing(Drone::id)` for deterministic ties. None of these algorithms sorts routes or mutates the fleet.

The demo fleet is A: 5000 g, 1 km away, idle 10 min; B: 2000 g, 4 km away, idle 20 min; C: 4000 g, 3 km away, idle 90 min. All have sufficient range. The same 1500 g parcel produces A, B, C respectively. An empty or entirely ineligible fleet returns `Optional.empty()`.

**Қазақша.** `DroneAssignmentStrategy` интерфейсінің `assign` әдісі жеткізу дерегі мен дрондар тізімін қабылдайды. Нәтиже `Optional<Drone>` болғандықтан, жарамды дрон табылмауы да мүмкін. `DroneEligibility` алдымен бос емес, жүкті көтере алмайтын, ұшу қоры жетпейтін дрондарды алып тастайды. Қажетті ұшу қоры: жүкті алу нүктесіне дейінгі қашықтық + жеткізу қашықтығының екі есесі. Яғни дрон жүкті алады, алушыға жеткізеді, жүкті алған нүктеге қайтады.

Одан кейін `NearestDroneStrategy` ең жақын дронды таңдайды. `BestFitPayloadStrategy` жүкті көтере алатын ең шағын сыйымдылықты дронды таңдайды: үлкен дрондарды ауыр жүктерге қалдыруға болады. `LongestIdleStrategy` ең ұзақ бос тұрған дронды таңдайды. Бірдей нәтижелерде атауы алфавит бойынша бірінші дрон алынады. Мысалда A ең жақын, B жүкке ең сәйкес, C ең ұзақ бос тұрған. Сондықтан бір тапсырыс үшін үш стратегия A, B, C нәтижелерін береді. Жарамды дрон болмаса, нәтиже бос болады.

## 5. Contexts versus selectors / Контекст пен selector айырмасы

**English.** A context stores a strategy reference, accepts it in its constructor, replaces it through `setStrategy`, and forwards the operation. That is its entire job. Calling `setStrategy(new PayloadBandPricing())` on an existing `PricingContext` changes the next calculation without rebuilding the context or changing its source code.

A selector has different knowledge: a map of manual keys, an ordered list of `SelectionRule` entries, and a fallback. Each rule pairs a `Predicate<Delivery>` with a strategy. A predicate is simply a function returning true or false. `automatic()` evaluates rules in order and takes the first match. Pricing gives wind ≥30 priority over weight >2000 g; assignment gives urgency priority over weight ≥3000 g. Every fallback is explicit. `manual()` looks up a key and rejects unknown keys. The map is not part of either context.

A selector returns a strategy; the caller passes it to the context. It does not silently monitor changes. When conditions change, call `automatic()` again and pass the result to `setStrategy()`. WeatherEnergyPricing reads its provider on every quote, so changing observations can change prices even before reselection. The simulated provider in this demo is fixed for reproducibility.

**Қазақша.** Контекст стратегияға сілтемені сақтайды. Конструктор оны қабылдайды, `setStrategy` ауыстырады, ал `quote` немесе `assign` жұмысты стратегияға береді. Контексттің міндеті осы ғана. Бір контекстке `setStrategy(new PayloadBandPricing())` шақырсақ, келесі есептеу жаңа тәсілмен жүреді.

Selector стратегияны таңдайды. Онда қолмен таңдауға арналған атаулар кестесі, реттелген ережелер тізімі, әдепкі стратегия бар. `SelectionRule` шартты нақты стратегиямен байланыстырады. `Predicate` — true немесе false қайтаратын шарт. Бірінші орындалған ереже жеңеді. Бағада желдің кемінде 30 болуы салмақтан басым; дрон таңдауда шұғылдық салмақтан басым. `manual()` берілген атау бойынша таңдайды. Белгісіз атау қате туғызады.

Selector жағдайды өздігінен бақыламайды. Жағдай өзгерген кезде `automatic()` әдісін қайта шақырып, шыққан стратегияны контекстке беру керек. Ал ауа райына тәуелді стратегия әр есептеу кезінде дерек көзінен жел мәнін қайта алады.

## 6. External dependency / Сыртқы тәуелділік

**English.** `WeatherSource` is our own interface, not a concrete API. `WeatherEnergyPricing` receives this interface through constructor injection. Production demo wiring uses `SimulatedWeatherSource`; tests use `FakeWeatherSource`. A real provider could implement the same single method later. No change to the energy formula is needed. The fake is a Java record returning a fixed value, so a test expecting 900 cannot fail because the weather changed. Tests also verify fresh observations and reject values outside 0–100. Provider failures propagate; this teaching project does not invent a fallback weather value.

**Қазақша.** `WeatherSource` — өзіміз анықтаған интерфейс. `WeatherEnergyPricing` нақты API-ге емес, осы интерфейске тәуелді. Демонстрацияда `SimulatedWeatherSource`, тестте `FakeWeatherSource` беріледі. Кейін нақты ауа райы сервисін қосу үшін осы интерфейсті іске асыру жеткілікті. Баға стратегиясының кодын өзгерту қажет емес. Fake тұрақты мән қайтарады, сондықтан тест шынайы ауа райына тәуелді болмайды. Дерек көзі қате берсе, қате жоғарыға беріледі; жүйе ойдан ауа райы мәнін алмайды.

## 7. Git refactoring / Git арқылы рефакторинг

**English.** The first commit intentionally contains the bad design: `PricingService.quote(mode, delivery)` holds a switch with all three formulas. The first test run established 14 successful characterization checks. In the refactoring commit, each formula moves into its own strategy. `PricingService` remains as a small compatibility entry point; it delegates selection to `PricingSelector`, then quotation to `PricingContext`. It is not a third context: it has no replaceable strategy field and makes no algorithm decision itself.

`PricingRegressionTest` was not edited. Its identical source passes before and after. Additional tests were added for individual strategies, selector boundaries, overlapping rules, fallback behavior, extension, eligibility, runtime replacement, external data, and invalid inputs. The final suite contains 57 successful JUnit invocations. Parameterized methods create multiple invocations, so 57 is not the number of Java test methods. The actual before and after commits exist, with tags and full hashes recorded in the history folder. A third documentation commit adds their final references.

**Қазақша.** Бірінші коммитте әдейі нашар құрылым жасалды: үш баға формуласы бір `PricingService.quote()` әдісінің switch блогында тұрды. Алдымен 14 тексеру сәтті өтті. Екінші коммитте формулалар жеке стратегияларға бөлінді. `PricingService` бастапқы шақыру интерфейсін сақтау үшін қалды: ол таңдауды selector-ға, есептеуді контекстке береді. Ол үшінші контекст емес, себебі ауыстырылатын стратегия өрісін ұстамайды.

`PricingRegressionTest` файлы өзгертілген жоқ. Сол тесттер екі нұсқада да өтеді. Қосымша тесттер нақты стратегияларды, шекара мәндерін, ережелер басымдығын, кеңейтуді, дрон жарамдылығын, орындау кезіндегі ауысуды, сыртқы деректі тексереді. Соңғы жиынтықта 57 JUnit тексеруі сәтті өтті. Бұл 57 бөлек Java әдісі емес: параметрленген тест бір әдісті бірнеше дерекпен іске қосады. Екі шынайы коммит архивте бар; үшінші коммит олардың сілтемелерін құжаттайды.

## 8. Adding a new algorithm / Жаңа алгоритм қосу

**English.** Write a new class implementing the appropriate strategy interface. Construct it in your application's setup. Pass it directly to a context for manual use, or build a selector with an extended key map and an added rule. Existing context, selector, and strategy source files remain unchanged. You are adding a class and new configuration, not secretly modifying a switch. The selector tests demonstrate this extension with small lambdas that implement the same interface.

```java
PricingStrategy specialContract = delivery -> 800L;
PricingStrategy ordinary = new DistancePricing();
PricingSelector customSelector = new PricingSelector(
    Map.of("contract", specialContract, "distance", ordinary),
    List.of(new SelectionRule<>(Delivery::urgent, specialContract)),
    ordinary);
PricingContext context = new PricingContext(customSelector.automatic(delivery));
```

This snippet is illustrative setup code; the six required concrete production strategies already exist as named classes. For a full new business algorithm, implement a named class and test its logic. Automatic behavior always reflects the configuration you explicitly supply.

**Қазақша.** Жаңа алгоритм үшін тиісті стратегия интерфейсін іске асыратын класс жазыңыз. Оны бағдарламаны құрастыру кезінде жасаңыз. Қолмен қолдану үшін тікелей контекстке беруге болады. Автоматты қолдану үшін жаңа атау мен ережені selector конфигурациясына қосыңыз. Бұрынғы контексттің, selector-дың, стратегиялардың кодын өзгертпейсіз. Жаңа класс пен жаңа конфигурация қосылады. Жоғарыдағы шағын мысал осыны көрсетеді; жобада талап етілген алты стратегия бөлек нақты кластар ретінде жазылған.

## 9. Reading the UML / UML оқу

**English.** A dashed line with a hollow triangle means “implements interface”. A context's open-diamond association means it holds a strategy reference supplied externally; it does not own that object's lifetime. A dependency arrow indicates use. WeatherEnergyPricing depends on WeatherSource, while SimulatedWeatherSource and FakeWeatherSource implement it. Both selectors hold rule entries. Demo creates and connects components. The diagram includes every production type and the weather fake; test-class responsibilities appear in the complete table.

**Қазақша.** Үзік сызық пен бос үшбұрыш «интерфейсті іске асырады» дегенді білдіреді. Контекст жанындағы бос ромб оның сырттан берілген стратегияға сілтемені сақтайтынын көрсетеді; стратегияның өмірлік циклін өзі басқармайды. Тәуелділік жебесі бір кластың екіншісін қолданатынын білдіреді. Ауа райына тәуелді стратегия WeatherSource интерфейсін қолданады, ал simulated және fake оны іске асырады. Demo объектілерді жасап, бір-бірімен байланыстырады. UML барлық негізгі типті қамтиды; тест кластары толық міндеттер кестесінде берілген.

## 10. Short defense script / Қорғауға арналған қысқа мәтін

**English.** “My project has two Strategy families: pricing and drone assignment. Each has one interface, three concrete algorithms, a context with constructor injection and setStrategy, and an independent selector. Contexts delegate without checking strategy types. Weather is hidden behind WeatherSource and replaced by a fixed fake in tests. The demo runs identical inputs through every algorithm, compares manual with automatic selection, and switches both contexts at runtime. I preserved the original pricing tests across real before and after commits. All 57 final test invocations pass.”

**Қазақша.** «Менің жобамда екі Strategy тобы бар: баға есептеу және дрон таңдау. Әр топта бір интерфейс, үш нақты алгоритм, стратегияны конструктормен қабылдайтын және setStrategy арқылы ауыстыратын контекст, бөлек selector бар. Контекст стратегияның түрін тексермейді, жұмысты соған тапсырады. Ауа райы WeatherSource интерфейсінің артында орналасқан, тестте тұрақты fake қолданылады. Демонстрация бірдей деректі барлық алгоритммен өңдейді, қолмен және автоматты таңдауды салыстырады, стратегияны орындау кезінде ауыстырады. Бастапқы баға тесттері рефакторингке дейінгі және кейінгі шынайы коммиттерде өзгеріссіз өтті. Соңғы 57 тексерудің бәрі сәтті орындалды».
