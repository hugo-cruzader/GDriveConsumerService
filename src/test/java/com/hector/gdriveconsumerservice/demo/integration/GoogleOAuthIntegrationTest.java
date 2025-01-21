package com.hector.gdriveconsumerservice.demo.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;


public class GoogleOAuthIntegrationTest extends BaseTest{

    /**
     * Unfortunately the browser shows a different flow when using my demo account.
     * After entering the email the flow goes into passkey mode instead of asking for password.
     * That is why this test is disabled since my account has this issue which could not let me enter my password afterward.
     *
     */
    @Test(enabled = false)
    public void testGoogleOAuthLogin() throws InterruptedException {
        driver.get("http://localhost:8080");

        WebElement signInButton = driver.findElement(By.id("google-signin-btn"));
        signInButton.click();

        // Assuming Google login page opens, handle login
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='identifierId']")))
                .sendKeys("<DEMO_ACCOUNT_EMAIL>");
        driver.findElement(By.id("identifierNext")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='password']")))
                .sendKeys("<ACCOUNT_PASSWORD>");
        driver.findElement(By.id("passwordNext")).click();

        Thread.sleep(5000); // Wait for redirection

        Assert.assertTrue(driver.getCurrentUrl().contains("localhost:8080/dashboard"));
    }
}
