# selenium-component-objects

A Java + Selenium proof of concept for building **Component Objects**: reusable, self-contained UI components (radio button, dropdown, data table, date picker, chart…) that locate their elements *inside their own container* instead of across the whole page.

> **Status:** Proof of concept. Tests in this repository demonstrate how the components are used against public demo sites; they are exploratory and not intended as a complete test suite.

## The problem

Selenium's `PageFactory` works well for flat pages, but it has two limitations when a page is built from repeated UI widgets:

- `@FindBy` always searches from the page (driver) root, so locators for a widget must be written to be unique across the whole page.
- It can only produce `WebElement` or `List<WebElement>`, not domain objects such as `List<RadioButton>`.

As a result, page objects fill up with long, fragile locators and low-level element handling.

## The approach

This project adds a small annotation and factory:

- **`@FindInside`**: marks a field to be located *relative to a container element*. It supports `id`, `css`, `xpath`, `className`, `name`, `tagName`, `linkText`, `partialLinkText`, and `using` (shorthand for a CSS selector).
- **`ComponentFactory.initElements(container, component)`**: reads the annotated fields via reflection and populates them, scoped to the given container.

`ComponentFactory` supports four field types:

| Field type | Result |
|---|---|
| `WebElement` | Single element found inside the container |
| `List<WebElement>` | All matching elements inside the container |
| `MyComponent` | Component instance built from the matching element |
| `List<MyComponent>` | One component instance per matching element |

The only contract for a component class is a public constructor that takes its container: `MyComponent(WebElement container)`.

## Example

A single radio button, scoped to its own wrapper element:

```java
public class RadioButton {

  @FindInside(css = "div.p-radiobutton")
  private WebElement radioButton;

  @FindInside(xpath = ".//label[@for]")
  private WebElement label;

  public RadioButton(WebElement container) {
    ComponentFactory.initElements(container, this);
  }

  public void select() {
    if (!isSelected()) {
      radioButton.click();
    }
  }

  public boolean isSelected() {
    return "true".equals(radioButton.getAttribute("data-p-checked"));
  }

  public String getLabel() {
    return label.getText().trim();
  }
}
```

A group of radio buttons becomes a list of components with a single annotation:

```java
public class RadioButtonList {

  @FindInside(css = ".flex.items-center")
  private List<RadioButton> radioButtons;

  public RadioButtonList(WebElement container) {
    ComponentFactory.initElements(container, this);
  }

  public void selectOption(Ingredient option) {
    radioButtons.stream()
        .filter(rb -> rb.getLabel().equals(option.getDisplayName()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Option not found: " + option))
        .select();
  }
}
```

The page object only locates the container (standard `@FindBy` still works) and delegates to the component:

```java
public class PrimeVueSelectPage {

  @FindBy(css = "section.py-6:nth-child(3) .flex-wrap")
  private WebElement ingredientGroupContainer;

  private RadioButtonList ingredientList;

  public PrimeVueSelectPage(WebDriver driver) {
    PageFactory.initElements(driver, this);
    this.ingredientList = new RadioButtonList(ingredientGroupContainer);
  }

  public void selectIngredient(Ingredient ingredient) {
    ingredientList.selectOption(ingredient);
  }
}
```

The test reads as a business action:

```java
primeVueSelectPage.selectIngredient(Ingredient.MUSHROOM);
Assert.assertEquals(primeVueSelectPage.getSelectedIngredient(), Ingredient.MUSHROOM);
```

## Components

| Component | Description | Demo site |
|---|---|---|
| `RadioButton`, `RadioButtonList` | Single radio button and a group of them | PrimeVue RadioButton |
| `Dropdown`, `DropdownList` | Dropdown with a list of options | PrimeVue Dropdown |
| `DataTable`, `ColumnHeader`, `AdvancedFilter` | Table with sorting and column filters | PrimeVue DataTable |
| `VirtualDataTable` | Table with virtual scrolling (rows rendered on demand) | PrimeVue DataTable |
| `datatable.Table`, `TableHeader`, `HeaderCell` | Alternative table model | PrimeVue DataTable |
| `PrimeVueDatePicker` | Calendar-based date selection | PrimeVue DatePicker |
| `SunburstChart`, `SunburstSlice` | Interactive SVG chart | Plotly |
| `HeaderComponent` | Page header on a classic web app | Sauce Demo |

## Tech stack

Java · Selenium WebDriver 4 · TestNG · Gradle

## Running the tests

Requirements: JDK 11+ and Google Chrome. The Chrome driver is resolved automatically by Selenium Manager.

```bash
# all tests
./gradlew test

# a single test class
./gradlew test --tests "tests.PrimeVueTest"
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Design notes and limitations

- **Eager initialization.** Unlike `PageFactory`, which injects lazy proxies, `ComponentFactory` looks up elements when the component is created. This keeps the mechanism simple and makes each component a snapshot of its container. If the UI re-renders the container, create a new component instance to avoid `StaleElementReferenceException`.
- **Locator priority.** If more than one attribute is set in `@FindInside`, the first non-empty one is used in this order: `using`, `id`, `css`, `xpath`, `className`, `name`, `tagName`, `linkText`, `partialLinkText`.
- **External demo sites.** Tests run against public documentation and demo pages (PrimeVue, Plotly, Sauce Demo). Their markup can change at any time, so some locators may need updating.