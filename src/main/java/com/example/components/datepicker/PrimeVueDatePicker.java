package com.example.components.datepicker;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PrimeVueDatePicker {

  private final WebElement container;
  private final WebDriverWait wait;
  private final WebDriver driver;

  // Relative selectors (scoped inside the container)
  private final By prevButtonSelector = By.cssSelector("button.p-datepicker-prev-button");
  private final By nextButtonSelector = By.cssSelector("button.p-datepicker-next-button");
  private final By monthButtonSelector = By.cssSelector("button.p-datepicker-select-month");
  private final By yearButtonSelector = By.cssSelector("button.p-datepicker-select-year");

  // Constructor: accepts the date picker panel (or a parent that contains it)
  public PrimeVueDatePicker(WebElement datePickerPanel) {
    this.container = datePickerPanel;
    this.driver = getDriverFromElement(datePickerPanel);
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  // Helper to extract WebDriver from a WebElement
  private WebDriver getDriverFromElement(WebElement element) {
    return ((RemoteWebElement) element).getWrappedDriver();
  }

  // Wait until a child element inside container is visible
  private WebElement findVisibleChild(By locator) {
    return wait.until(ExpectedConditions.visibilityOf(container.findElement(locator)));
  }

  // Wait until a child element inside container is clickable
  private WebElement findClickableChild(By locator) {
    return wait.until(ExpectedConditions.elementToBeClickable(container.findElement(locator)));
  }

  public void clickPreviousMonth() {
    findClickableChild(prevButtonSelector).click();
  }

  public void clickNextMonth() {
    findClickableChild(nextButtonSelector).click();
  }

  public String getCurrentMonth() {
    return findVisibleChild(monthButtonSelector).getText();
  }

  public String getCurrentYear() {
    return findVisibleChild(yearButtonSelector).getText();
  }

  public void selectDay(String dayText) {
    // Find a non-disabled day cell with exact text inside the container
    String dayXPath = ".//span[@data-pc-section='day' and text()='" + dayText
        + "' and not(contains(@class, 'p-disabled'))]";
    WebElement day = wait.until(
        ExpectedConditions.elementToBeClickable(container.findElement(By.xpath(dayXPath))));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoViewIfNeeded(true);", day);
    day.click();
  }

  // Navigate to a specific month and year
  public void navigateToMonthYear(String targetMonth, String targetYear) {
    int maxAttempts = 240;
    int attempts = 0;

    while (!getCurrentMonth().equals(targetMonth) || !getCurrentYear().equals(targetYear)) {
      if (attempts++ > maxAttempts) {
        throw new RuntimeException("Failed to navigate to " + targetMonth + " " + targetYear);
      }

      int currentYear = Integer.parseInt(getCurrentYear());
      int targetY = Integer.parseInt(targetYear);
      int currentMonthOrder = getMonthOrder(getCurrentMonth());
      int targetMonthOrder = getMonthOrder(targetMonth);

      if (currentYear < targetY || (currentYear == targetY
          && currentMonthOrder < targetMonthOrder)) {
        clickNextMonth();
      } else {
        clickPreviousMonth();
      }

      // Wait for transition: ensure month/year buttons are re-rendered
//      wait.until(ExpectedConditions.stalenessOf(container.findElement(monthButtonSelector)));
      wait.until(ExpectedConditions.visibilityOf(container.findElement(monthButtonSelector)));
    }
  }

  private int getMonthOrder(String monthName) {
    for (int i = 1; i <= 12; i++) {
      if (Month.of(i).getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(monthName)) {
        return i;
      }
    }
    throw new IllegalArgumentException("Unknown month: " + monthName);
  }

  public void selectDate(LocalDate date) {
    String targetMonth = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    String targetYear = String.valueOf(date.getYear());
    String targetDay = String.valueOf(date.getDayOfMonth());

    navigateToMonthYear(targetMonth, targetYear);
    selectDay(targetDay);
  }

  public void selectDate(String isoDate) {
    selectDate(LocalDate.parse(isoDate));
  }

  // Optional: close by clicking outside (relative to container’s location)
  public void close() {
    new Actions(driver)
        .moveToElement(container, 100, 100)
        .click()
        .perform();
  }
}
