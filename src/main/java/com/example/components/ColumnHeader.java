package com.example.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ColumnHeader {

  WebElement container;
  WebDriver driver;

  public ColumnHeader(WebElement container, WebDriver driver) {
    this.container = container;
    this.driver = driver;
  }

  public String getName() {
    return container.getText();
  }

  public AdvancedFilter openFilter() {
    WebElement filterButton = container.findElement(By.cssSelector(".p-datatable-filter button"));
    filterButton.click();
    String filterId = filterButton.getDomAttribute("aria-controls");
    return new AdvancedFilter(filterId, driver);
  }
}
