package com.example.components;

import com.example.annotations.FindInside;
import com.example.enums.City;
import com.example.enums.SelectableOption;
import com.example.utils.ComponentFactory;
import java.util.List;
import java.util.Objects;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DropdownList {

  protected WebDriver driver;
  protected final WebElement container;

  // Use your @FindInside to locate elements within the container
  @FindInside(tagName = "li")
  List<WebElement> options;

  public DropdownList(WebElement container, WebDriver driver) {

    this.driver = driver;
    this.container = container;
    ComponentFactory.initElements(container, this);
  }

  public void selectOption(City city) {

    // Find the option by visible text
    for (WebElement option : options) {
      String label = option.getText();
      if (city.getDisplayName().equals(label)) {
        if (!option.getAttribute("aria-disabled").equals("true")) {
          option.click();
          return;
        } else {
          throw new IllegalArgumentException("Option '" + city.getDisplayName() + "' is disabled.");
        }
      }
    }
    throw new NoSuchElementException(
        "Option '" + city.getDisplayName() + "' not found in dropdown.");
  }

  public void selectOption(String opt) {

    // Find the option by visible text
    for (WebElement option : options) {
      String label = option.getText();
      if (opt.equals(label)) {
        if (!Objects.equals(option.getDomProperty("aria-disabled"), "true")) {
          option.click();
          return;
        } else {
          throw new IllegalArgumentException("Option '" + opt + "' is disabled.");
        }
      }
    }
    throw new NoSuchElementException("Option '" + opt + "' not found in dropdown.");
  }

  public void selectOption(SelectableOption opt) {

    // Find the option by visible text
    for (WebElement option : options) {
      String label = option.getText();
      if (opt.getLabel().equals(label)) {
        if (!Objects.equals(option.getDomProperty("aria-disabled"), "true")) {
          option.click();
          return;
        } else {
          throw new IllegalArgumentException("Option '" + opt.getLabel() + "' is disabled.");
        }
      }
    }
    throw new NoSuchElementException("Option '" + opt.getLabel() + "' not found in dropdown.");
  }
}
