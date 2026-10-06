package anep;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import utilities.ConfigReader;

public class Anep {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {

        driver = new EdgeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get(ConfigReader.getProperty("anep.sit1.url"));
    }

    @Test
    public void validateOrderStatus() throws InterruptedException {

        // Login
        getElement(By.id("signInFormUsername"))
                .sendKeys(ConfigReader.getProperty("anep.username"));

        getElement(By.id("signInFormPassword"))
                .sendKeys(ConfigReader.getProperty("anep.password"));

        getElement(By.xpath("//input[@type='Submit' and @name='signInSubmitButton']"))
                .click();

        Thread.sleep(2000);

        // Navigate to Order Search
        getElement(By.cssSelector("a[href='/om/order/search']"))
                .click();

        // Search Order
        getElement(By.xpath("//input[@placeholder='Quick Search']"))
                .sendKeys(ConfigReader.getProperty("Premise_ID"));

        getElement(By.id("searchBtn"))
                .click();

        Thread.sleep(2000);

        // Fetch all order numbers
        List<WebElement> orders = driver.findElements(
                By.xpath("//tbody//a[contains(@href,'/om/loadOrder/')]"));

        // Select latest order
        WebElement latestOrder = orders.stream()
                .max((a, b) -> Integer.compare(
                        Integer.parseInt(a.getText().trim()),
                        Integer.parseInt(b.getText().trim())))
                .orElseThrow(() -> new RuntimeException("No Orders Found"));

      

        latestOrder.click();

        Thread.sleep(3000);

        // Fetch Order Status
        WebElement orderStatus = getElement(
                By.xpath("//span[contains(@class,'order-status')]"));

        String state = orderStatus.getAttribute("data-content").trim();

        System.out.println("Status : " + state);

        if ("Running".equalsIgnoreCase(state)) {

            System.out.println(
                "Order is Currently in Running state. Please validate it again after some time."
            );

        } else if ("Closed".equalsIgnoreCase(state)) {

            System.out.println("Order is now Completed");

        } else {

            System.out.println("Order is in " + state + " state");

        }
    }

    public WebElement getElement(By locator) {

        return wait.until(driver -> driver.findElements(locator)
                .stream()
                .filter(element -> element.isDisplayed() && element.isEnabled())
                .findFirst()
                .orElse(null));
    }

    @AfterClass
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}