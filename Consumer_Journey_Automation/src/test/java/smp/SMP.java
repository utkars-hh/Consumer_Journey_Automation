package smp;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import utilities.ConfigReader;
import utilities.ExcelReader;

public class SMP {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setup() {

        driver = new EdgeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    public void verifyOrderStatus() {

        driver.get(ConfigReader.getProperty("smp.url"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("idToken1")))
                .sendKeys(ConfigReader.getProperty("smp.username"));

        driver.findElement(By.id("idToken2"))
                .sendKeys(ConfigReader.getProperty("smp.password"));

        driver.findElement(By.id("loginButton_0"))
                .click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and @value='allow']")))
                .click();

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                By.name("smpMain")));

        String filePath =
                "src/test/resources/Port_Reservation_Data.xlsx";

        String siteReference =
                ExcelReader.getCellDataByColumnName(
                        filePath,
                        "Sheet1",
                        1,
                        "siteReference");

        String addressIdentifier =
                ExcelReader.getCellDataByColumnName(
                        filePath,
                        "Sheet1",
                        1,
                        "addressIdentifier");

        String accountNumber =
                siteReference + addressIdentifier;

        WebElement searchBox = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.name("SubAcctSearchAll.acct")));

        searchBox.clear();
        searchBox.sendKeys(accountNumber);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("btnSubmit")))
                .click();

        driver.switchTo().defaultContent();

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                By.name("smpMenu")));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[normalize-space()='Orders']")))
                .click();

        driver.switchTo().defaultContent();

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                By.name("smpMain")));

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                By.name("mainFrame")));

        String status = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//td[contains(normalize-space(.),'State:')]"
                                + "/following-sibling::td[1]")))
                .getText()
                .trim();
        
        System.out.println("Status : " + status);
        System.out.println("The order is in " + status + " state.");
    }

    @AfterMethod
    public void tearDown() {

        if (driver != null) {
//            driver.quit();
        }
    }
}
