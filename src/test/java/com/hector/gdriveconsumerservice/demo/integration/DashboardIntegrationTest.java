package com.hector.gdriveconsumerservice.demo.integration;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.File;
import java.time.Duration;


public class DashboardIntegrationTest extends BaseTest{
    @Test(priority = 1)
    public void testListFiles() throws InterruptedException {
        driver.get("http://localhost:8080/");
        Thread.sleep(5000);
        final WebElement fileList = driver.findElement(By.id("fileContainer"));
        Assert.assertTrue(fileList.isDisplayed(), "File list is not displayed.");
        final int fileCount = fileList.findElements(By.xpath("./*")).size();
        Assert.assertTrue(fileCount > 0, "File list is empty, no files or folders found.");
    }

    @Test(priority = 2)
    public void testDownloadFile() throws InterruptedException {
        final WebElement downloadButton = driver.findElement(By.id("SLIDE_EXAMPLE_dw"));
        downloadButton.click();
        Thread.sleep(5000);
        // Verify file exists in download directory (assuming default location)
        final File downloadedFile = new File(System.getProperty("user.home") + "/Downloads/SLIDE_EXAMPLE.pdf");
        Assert.assertTrue(downloadedFile.exists(), "File was not downloaded successfully.");
    }

    @Test(priority = 3)
    public void testUploadFileToRoot() {
        uploadFileToFolder("testfile.txt", "rootUpload");
    }

    @Test(priority = 4)
    public void testUploadFileToFolder(){
        uploadFileToFolder("folderfile.txt", "DemoFolder1_fd");
    }

    private void uploadFileToFolder(String fileName, String folderId) {

        final WebElement fileInput = driver.findElement(By.id(folderId));
        final ClassLoader classLoader = getClass().getClassLoader();
        File fileToUpload = new File(classLoader.getResource(fileName).getFile());
        fileInput.sendKeys(fileToUpload.getAbsolutePath());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (final NoAlertPresentException e) {
            System.out.println("No alert was present.");
        }
        // Wait for the file to appear in the list after upload
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(text(), '" + fileName + "')]")));
        Assert.assertTrue(driver.findElement(By.xpath("//td[contains(text(), '" + fileName + "')]")).isDisplayed(),
                "Uploaded file is not displayed in the file list.");
    }

    @Test(priority = 5)
    public void testDeleteFiles() throws InterruptedException {
        deleteFile("testfile.txt");
        deleteFile("folderfile.txt");
    }

    private void deleteFile(final String fileName) throws InterruptedException {
        Thread.sleep(5000);
        final WebElement deleteButton = driver.findElement(By.id(fileName + "_del"));
        deleteButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (final NoAlertPresentException e) {
            System.out.println("No alert was present.");
        }
        Assert.assertThrows("File was not deleted from the file list.", NoSuchElementException.class,
                () -> driver.findElement(By.xpath("//td[contains(text(), '"+ fileName + "')]")).isDisplayed());
    }


}
