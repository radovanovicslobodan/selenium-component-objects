package com.example.components.datatable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Table {

    // data-pc-name="datatable"
    private WebElement container;
    private WebDriver driver;
    private WebDriverWait wait;

    private By tableHeader = By.cssSelector(".p-datatable-thead");
    private By tableBody = By.cssSelector(".p-datatable-tbody");

    public Table(WebElement container, WebDriver driver) {
        this.driver = driver;
        this.container = container;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public TableHeader getTableHeader() {
        return new TableHeader(container.findElement(tableHeader), driver);
    }
}
