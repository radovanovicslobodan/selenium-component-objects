package com.example.components.datatable;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class TableHeader {

    private WebElement container;
    private WebDriver driver;
    private WebDriverWait wait;

    public TableHeader(WebElement container, WebDriver driver) {
        this.container = container;
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Lazy initialization: create HeaderCell objects only when needed
    private List<HeaderCell> getHeaderCells() {
        return container.findElements(By.cssSelector("th"))
                .stream()
                .map(webElement -> new HeaderCell(webElement, driver))
                .collect(Collectors.toList());
    }

    public HeaderCell getByIndex(int index) {
        List<HeaderCell> cells = getHeaderCells();
        if (index < 0 || index >= cells.size()) {
            throw new IndexOutOfBoundsException("Header index " + index + " is out of bounds. Total headers: " + cells.size());
        }
        return cells.get(index);
    }

    public HeaderCell getByName(String headerText) {
        return getHeaderCells().stream()
                .filter(cell -> headerText.equals(cell.getText()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Header with text '" + headerText + "' not found."));
    }
}
