package tests;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PrimeVueVirtualScrollTest {

  private WebDriver driver;
  private Instant startTime;

  // Selectors (reusable and centralized)
  private static final String DATATABLE_SECTION_SELECTOR = "section:nth-child(17)";
  private static final String SCROLLABLE_SELECTOR =
      DATATABLE_SECTION_SELECTOR + " .p-virtualscroller";
  private static final String ROW_SELECTOR = DATATABLE_SECTION_SELECTOR + " tbody tr";
  private static final String TABLE_WRAPPER_SELECTOR =
      DATATABLE_SECTION_SELECTOR + " .p-datatable-wrapper";

  @BeforeClass
  public void setUp() {
    startTime = Instant.now();
    // Initialize your WebDriver — example:
    // System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
    // driver = new ChromeDriver();
    // Make sure this is set up in your actual test environment
    driver = new ChromeDriver(); // <- Replace with your driver setup
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
  }

  @Test
  public void collectFirst100RowsTest() {
    driver.get("https://v3.primevue.org/datatable/#virtualscroll");

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(TABLE_WRAPPER_SELECTOR)));

    // Collect first 100 unique rows
    Map<Integer, String> first100 = collectFirstNRows(100);

    System.out.println("* Collected " + first100.size() + " rows:");
    first100.forEach((index, id) -> System.out.println("Index: " + index + " -> ID: " + id));

    // Optional: verify a known ID is present
    String targetId = "1000";
    if (first100.containsValue(targetId)) {
      Integer index = first100.entrySet().stream()
          .filter(e -> targetId.equals(e.getValue()))
          .findFirst()
          .map(Map.Entry::getKey)
          .orElse(null);
      System.out.println("* Found target ID '" + targetId + "' at index: " + index);
    } else {
      System.err.println("* Target ID '" + targetId + "' not found in collected rows.");
    }
  }

  /**
   * Collects up to `n` unique rows from the virtual-scrolled PrimeVue DataTable. Uses
   * `data-p-index` to track logical row position and avoid duplicates.
   */
  public Map<Integer, String> collectFirstNRows(int n) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    // Get row height for scroll step
    WebElement firstRow = wait.until(
        ExpectedConditions.presenceOfElementLocated(By.cssSelector(ROW_SELECTOR)));
    int itemHeight = firstRow.getSize().getHeight();
    int scrollStep = itemHeight * 25; // scroll ~25 rows at a time

    WebElement scrollable = driver.findElement(By.cssSelector(SCROLLABLE_SELECTOR));

    // Initial small scroll to ensure data is loaded
    js.executeScript("arguments[0].scrollTop += arguments[1];", scrollable, itemHeight * 5);

    Map<Integer, String> collected = new LinkedHashMap<>(); // preserves order
    Set<Integer> seenIndices = new HashSet<>();
    int attempts = 0;
    int maxAttempts = 50;

    while (collected.size() < n && attempts < maxAttempts) {
      // Map currently visible rows
      List<WebElement> rows = driver.findElements(By.cssSelector(ROW_SELECTOR));
      List<Row> mappedRows = mapRows(rows);

      // Add unseen rows
      for (Row row : mappedRows) {
        if (!seenIndices.contains(row.index)) {
          collected.put(row.index, row.text);
          seenIndices.add(row.index);
          if (collected.size() >= n) {
            break;
          }
        }
      }

      if (collected.size() >= n) {
        break;
      }

      // Record max index BEFORE scrolling
      int preScrollMaxIndex = getDomMaxIndex();

      // Scroll down
      js.executeScript("arguments[0].scrollTop += arguments[1];", scrollable, scrollStep);

      // Wait until a higher index appears (new data loaded)
      wait.until(d -> getDomMaxIndex() > preScrollMaxIndex);

      attempts++;
    }

    return collected;
  }

  /**
   * Returns the highest `data-p-index` currently present in the DOM.
   */
  private int getDomMaxIndex() {
    return driver.findElements(By.cssSelector(ROW_SELECTOR))
        .stream()
        .map(webElement -> webElement.getDomAttribute("data-p-index"))
        .filter(Objects::nonNull)
        .filter(s -> !s.isEmpty())
        .mapToInt(Integer::parseInt)
        .max()
        .orElse(-1);
  }

  /**
   * Converts WebElements to Row objects (index + first cell text).
   */
  private List<Row> mapRows(List<WebElement> rows) {
    List<Row> result = new ArrayList<>();
    for (WebElement row : rows) {
      try {
        String indexAttr = row.getDomAttribute("data-p-index");
        if (indexAttr == null || indexAttr.isEmpty()) {
          continue;
        }

        int index = Integer.parseInt(indexAttr);
        List<WebElement> cells = row.findElements(By.tagName("td"));
        if (!cells.isEmpty()) {
          String idCellText = cells.get(0).getText().trim();
          String vinCellText = cells.get(1).getText().trim();
          result.add(new Row(index, idCellText, vinCellText));
        }
      } catch (Exception e) {
        // Skip malformed rows (e.g., parsing error)
      }
    }
    return result;
  }

  @AfterClass
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }

    Duration elapsed = Duration.between(startTime, Instant.now());
    long seconds = elapsed.toSeconds();
    long millis = elapsed.toMillis();
    System.out.printf("Test execution time: %d seconds (%d ms)%n", seconds, millis);
  }

  // Helper class to hold row data
  private static class Row {

    int index;
    String text;
    String vin;

    Row(int index, String text, String vin) {
      this.index = index;
      this.text = text;
      this.vin = vin;
    }
  }
}