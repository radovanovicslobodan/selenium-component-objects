package com.example.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AdvancedFilter {

    WebElement container;
    WebDriver driver;

    public AdvancedFilter(String id, WebDriver driver) {
        this.driver = driver;
        this.container = driver.findElement(By.id(id));
    }

    public DropdownList openOperatorList() {
        WebElement toggleButton =
                container.findElement(By.cssSelector(".p-datatable-filter-operator .p-select"));
        String dropDownListId = toggleButton.getDomAttribute("id") + "_list";
        toggleButton.click();
        WebElement dropDownContainer = driver.findElement(By.id(dropDownListId));
        return new DropdownList(dropDownContainer, driver);
    }

    public DropdownList openRuleList() {
        WebElement toggleButton =
                container.findElement(By.cssSelector(".p-datatable-filter-rule .p-select"));
        String dropDownListId = toggleButton.getDomAttribute("id") + "_list";
        toggleButton.click();
        WebElement dropDownContainer = driver.findElement(By.id(dropDownListId));
        return new DropdownList(dropDownContainer, driver);
    }

    public void enterSearchTerm(String term) {
        WebElement inputField = container.findElement(By.cssSelector(".p-datatable-filter-rule input"));
        inputField.sendKeys(term);
    }

    public void clearFilter() {
        WebElement clearButton =
                container.findElement(By.cssSelector(".p-datatable-filter-clear-button"));
        clearButton.click();
    }

    public void applyFilter() {
        WebElement applyButton = container.findElement(By.cssSelector("[data-pc-name='pcfilterapplybutton'"));
        applyButton.click();
    }
}
