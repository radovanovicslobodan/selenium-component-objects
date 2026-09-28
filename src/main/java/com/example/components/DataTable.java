package com.example.components;

import com.example.utils.ComponentFactory;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DataTable {

  protected final WebElement container;
  protected final WebDriver driver;
  protected final WebDriverWait wait;

  // Use @FindInside in your field declarations; this class assumes container is the <table>
  public DataTable(WebElement container, WebDriver driver) {

    this.container = container;
    var isIt = container instanceof WrapsDriver;
    WebDriver associatedDriver =
        (container instanceof WrapsDriver) ? ((WrapsDriver) container).getWrappedDriver() : driver;
    String url = associatedDriver.getCurrentUrl();
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    ComponentFactory.initElements(container, this);
  }

  // --- Column Helpers ---

  /** Returns the 0-based index of the column with the given header text. */
  public int getColumnIndex(String columnName) {

    WebDriver associatedDriver = ((WrapsDriver) container).getWrappedDriver();

    var url = associatedDriver.getCurrentUrl();

    List<WebElement> headers = container.findElements(By.cssSelector("thead th .p-column-title"));
    for (int i = 0; i < headers.size(); i++) {
      if (columnName.equals(headers.get(i).getText())) {
        return i;
      }
    }
    throw new IllegalArgumentException("Column '" + columnName + "' not found.");
  }

  /** Checks if the column is currently sorted. */
  public boolean isColumnSorted(String columnName) {

    WebElement headerCell = getHeaderCell(columnName);
    return "true".equals(headerCell.getAttribute("sorted"));
  }

  /** Returns sort order: "ascending", "descending", or null if not sorted. */
  public String getColumnSortOrder(String columnName) {

    WebElement headerCell = getHeaderCell(columnName);
    if (!"true".equals(headerCell.getAttribute("sorted"))) {
      return null;
    }
    String sortOrderAttr = headerCell.getAttribute("sortOrder");
    if (sortOrderAttr == null) return "ascending"; // default in PrimeVue
    return "-1".equals(sortOrderAttr) ? "descending" : "ascending";
  }

  private WebElement getHeaderCell(String columnName) {

    int colIndex = getColumnIndex(columnName);
    return container.findElements(By.cssSelector("thead th")).get(colIndex);
  }

  // --- Row & Cell Helpers ---

  /** Returns the row (WebElement) that contains the given value in the specified column. */
  public WebElement getRowByColumnValue(String columnName, String value) {

    int colIndex = getColumnIndex(columnName);
    List<WebElement> rows = container.findElements(By.cssSelector("tbody tr[role='row']"));
    for (WebElement row : rows) {
      List<WebElement> cells = row.findElements(By.cssSelector("td[role='cell']"));
      if (colIndex < cells.size()) {
        String cellText = cells.get(colIndex).getText();
        if (value.equals(cellText)) {
          return row;
        }
      }
    }
    throw new IllegalArgumentException("No row found with " + columnName + " = '" + value + "'");
  }

  /** Gets the text of a cell by row index and column name. */
  public String getCellText(int rowIndex, String columnName) {

    int colIndex = getColumnIndex(columnName);
    WebElement row = container.findElements(By.cssSelector("tbody tr[role='row']")).get(rowIndex);
    return row.findElements(By.cssSelector("td[role='cell']")).get(colIndex).getText();
  }

  /** Total number of data rows. */
  public int getRowCount() {

    return container.findElements(By.cssSelector("tbody tr[role='row']")).size();
  }
}
