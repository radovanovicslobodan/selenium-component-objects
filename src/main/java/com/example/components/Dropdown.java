package com.example.components;

import com.example.annotations.FindInside;
import com.example.enums.City;
import com.example.utils.ComponentFactory;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Dropdown {

  protected WebDriver driver;
  protected final WebElement container;

  // Use your @FindInside to locate elements within the container
  @FindInside(id = "pv_id_45")
  private WebElement trigger;

  private WebElement dropdownList;

  private By dropdownListContainer = By.id("pv_id_45_list");

  public Dropdown(WebElement container, WebDriver driver) {

    this.driver = driver;
    this.container = container;
    ComponentFactory.initElements(container, this);
  }

  public void selectByVisibleText(City city) {

    var a = openDropdown();
    a.selectOption(city);
  }

  public String getSelectedText() {

    return trigger.getText();
  }

  private boolean isPlaceholder(WebElement label) {
    // PrimeVue adds .p-placeholder class when showing placeholder
    return label.getAttribute("class") != null
        && label.getAttribute("class").contains("p-placeholder");
  }

  private DropdownList openDropdown() {

    if (!"true".equals(trigger.getAttribute("aria-expanded"))) {
      trigger.click();
      this.dropdownList = driver.findElement(dropdownListContainer);
      // Wait for the dropdown list to appear in the DOM
      new WebDriverWait(this.driver, Duration.ofSeconds(5))
          .until(ExpectedConditions.visibilityOf(dropdownList));
    }
    return new DropdownList(dropdownList, driver);
  }
}
