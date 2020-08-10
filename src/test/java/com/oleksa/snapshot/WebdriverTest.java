package com.oleksa.snapshot;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverInfo;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.assertj.core.api.Assertions.assertThat;

public class WebdriverTest {

    private static ChromeOptions options = new ChromeOptions();
    static {
        System.setProperty(
                "webdriver.chrome.driver",
                "C:\\Program Files\\chromeWebdriver\\chromedriver.exe");
        options.setAcceptInsecureCerts(true);
    }
    private static WebDriver driver = new ChromeDriver(options);

    @AfterAll
    static void shut() {
        driver.quit();
    }

    @Test
    void testIndexPage() throws InterruptedException {
        driver.get("https://localhost:8888/index.html");
        Thread.sleep(1000);
        String title = driver.getTitle();
        assertThat(title).isEqualTo("This is index page.");
        String message = driver.findElement(By.id("message")).getText();
        assertThat(message).isEqualTo("Hello from Oleksii!");
    }

}
