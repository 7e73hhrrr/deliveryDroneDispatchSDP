# Validation evidence / Тексеру дәлелі

The baseline was compiled and tested before its commit was created. The refactored version was compiled and tested before its commit was created.

| Version | Executed checks | Successful | Failed |
|---|---:|---:|---:|
| Before: PricingRegressionTest | 14 | 14 | 0 |
| After: full JUnit suite | 57 | 57 | 0 |

The after suite includes all 14 unchanged regression invocations. Test source equality was checked with `git diff before-strategy -- src/test/java/com/dronedispatch/pricing/PricingRegressionTest.java`; no difference was printed. The same fake source was retained. Raw JUnit reports: [before](before-tests.txt), [after](after-tests.txt). Actual demo output: [demo-output.txt](demo-output.txt).

## Execution environment

OpenJDK 17 runtime was available. Maven and javac were not installed. Consequently actual validation used the Eclipse Java compiler ECJ 3.38.0 with Java 17 compatibility and the genuine JUnit Platform Console Standalone 1.11.4, containing JUnit Jupiter 5.11.4. No substitute test framework or hand-written JUnit stubs were used. Maven commands are supplied for the normal user workflow; a Maven run was not executed in the authoring environment.

Commands executed from the project directory (tools were kept outside the deliverable):

```bash
java -jar ../tools/ecj.jar -17 -proc:none -d target/classes $(find src/main/java -name '*.java')
java -jar ../tools/ecj.jar -17 -proc:none -cp target/classes:../tools/junit.jar -d target/test-classes $(find src/test/java -name '*.java')
java -jar ../tools/junit.jar execute --class-path target/classes:target/test-classes --scan-class-path --disable-ansi-colors
java -cp target/classes com.dronedispatch.app.Demo
```

Compilation and demo execution succeeded. PlantUML rendered the checked-in UML source to SVG with its internal Smetana layout engine. The final archive excludes generated class files and third-party compiler/test binaries; Maven resolves the declared dependencies on your machine.

## Coverage by purpose

- Exact expected results for all six strategy implementations.
- Every automatic rule, every fallback, exact threshold values, competing-rule priorities.
- Manual key lookup, unknown key rejection, extension through constructor-supplied configuration.
- Runtime replacement in both contexts, null strategy rejection.
- Parameterized nonnegative-price contract over all pricing strategies and 20 boundary combinations per strategy.
- Parameterized assignment checks for availability, capacity, range, empty fleets, deterministic ties, unchanged input ordering.
- Fixed fake weather, different wind observations, fresh observations at execution time, invalid external values.
- Validated domain boundaries.

Қазақша: бастапқы нұсқада 14 тексеру, соңғы нұсқада 57 тексеру өтті. Бастапқы тест файлы өзгерген жоқ. Бұл ортада Maven мен javac болмағандықтан, код ECJ арқылы Java 17 үшін компиляцияланып, шынайы JUnit 5 консоль құралы арқылы тексерілді. Maven орындалды деп мәлімделмейді. Демонстрация нақты іске қосылды; оның шығысы қоса берілген.
