package com.example.components.datatable;

import com.example.components.AdvancedFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HeaderCell {

    private WebElement container;
    private WebDriver driver;
    private WebDriverWait wait;

    public HeaderCell(WebElement container, WebDriver driver) {
        this.container = container;
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String getText() {
        return container.getText().trim();
    }

    /**
     * This method will open advanced filter,
     * but developer is responsible to be aware what type of filter is
     */
    public AdvancedFilter openFilter() {
        WebElement filterButton = container.findElement(By.cssSelector(".p-datatable-filter button"));
        filterButton.click();
        String filterId = filterButton.getDomAttribute("aria-controls");
        return new AdvancedFilter(filterId, driver);
    }
}
