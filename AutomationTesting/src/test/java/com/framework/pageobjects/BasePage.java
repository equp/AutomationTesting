package com.framework.pageobjects;

import com.google.common.base.Stopwatch;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sun.webkit.network.URLs.newURL;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait waitUntil;
    Stopwatch executionWatch;

    public BasePage(WebDriver webdriver) {
        this.driver = webdriver;
        PageFactory.initElements(this.driver, this);
        waitUntil = new WebDriverWait(driver, 20);
        waitUntil.pollingEvery(Duration.ofMillis(100));
        executionWatch = Stopwatch.createUnstarted();
    }

    public WebElement waitUntilClickable(By findBy) throws ElementClickInterceptedException {
        try {
            waitForPageLoad();
            return waitUntil.until(ExpectedConditions.elementToBeClickable(findBy));
        } catch (ElementClickInterceptedException ex) {
            throw new ElementClickInterceptedException("Element not clickable.");
        }
    }

    public WebElement waitUntilClickable(WebElement element) throws ElementClickInterceptedException {
        try {
            waitForPageLoad();
            return waitUntil.until(ExpectedConditions.elementToBeClickable(element));
        } catch (ElementClickInterceptedException ex) {
            throw new ElementClickInterceptedException("Element not clickable.");
        }
    }

    public WebElement waitUntilVisible(By findBy) throws ElementNotVisibleException {
        try {
            return waitUntil.until(ExpectedConditions.visibilityOfElementLocated(findBy));
        } catch (ElementNotVisibleException ex) {
            throw new ElementNotVisibleException("Element not visible.");
        }
    }

    public void waitUntilInvisible() throws ElementNotVisibleException {
        try {
            WebElement element = driver.findElement(By.xpath("//span[@class='progress-bar']"));
            waitUntil.until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception ex) {
        }
    }

    public WebElement waitUntilVisible(WebElement element) throws ElementNotVisibleException {
        try {
            waitForPageLoad();
            return waitUntil.until(ExpectedConditions.visibilityOf(element));
        } catch (ElementNotVisibleException ex) {
            throw new ElementNotVisibleException("Element not visible.");
        }
    }

    public void waitUntilUrlContains(String partialText) {
        try {
            waitForPageLoad();
            waitUntil.until(ExpectedConditions.urlContains(partialText));
        } catch (Exception ex) {
        }
    }

    public void waitForPageLoad() {
        new WebDriverWait(driver, 45).until(ExpectedConditions.jsReturnsValue("return (window.jQuery != null) " + "&& (jQuery.active == 0);"));
        //waitUntil.until(ExpectedConditions.invisibilityOfAllElements(
        //      driver.findElement(By.cssSelector("div.loading-spinner-container"))));
    }

    public void closeSurveyPopups() throws WebDriverException {
        try {
            WebElement surveyPopup = waitUntil.until(driver -> driver.findElement(By.id("npsDialogStartSpan")));
            waitUntilVisible(surveyPopup);
            if (surveyPopup.isDisplayed())
                driver.findElement(By.cssSelector("button.nps-button--close-survey")).click();
        } catch (WebDriverException ex) {
        }
    }

    public void clickButton(By buttonLocator) {
        waitForPageLoad();
        waitUntilClickable(buttonLocator);
        driver.findElement(buttonLocator).click();
        waitForPageLoad();
    }

    public void hoverOver(WebElement linkOnScreen) {
        Actions action = new Actions(driver);
        action.moveToElement(linkOnScreen).build().perform();
    }

    /**
     * This method is used to choose options from a <Select> dropdown
     *
     * @param dropdownValue
     * @param findBy
     * @throws Exception
     */
    public void selectFromDropdown(String dropdownValue, By findBy) {
        Select selectValueFromDropdown = new Select(driver.findElement(findBy));
        selectValueFromDropdown.selectByValue(dropdownValue);

    }

    public List<String> convertToStringList(List<WebElement> webElementsList, Function<WebElement, String> predicate) {
        List<String> output = webElementsList.stream().map(predicate).collect(Collectors.toList());
        return output;
    }

    public List<String> compareLists(List<String> list1, List<String> list2) {
        if (!list1.containsAll(list2)) {
            List<String> uniqueElements = new ArrayList();
            uniqueElements.addAll(0, list1);
            uniqueElements.removeAll(list2);
            return uniqueElements;
        } else return null;
    }

    public WebDriver switchTabs(int activeTab) throws InterruptedException {
        Thread.sleep(1200);
        List<String> allTabs = new ArrayList(driver.getWindowHandles());
        driver = driver.switchTo().window(allTabs.get(activeTab));
        return driver;
    }

    public String getDate(int daysToAdd) {
        LocalDate date = LocalDate.now().plusDays(daysToAdd);
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void scrollElementIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", element);
    }

    public static boolean verifyPDF(String pdfUrl, String verificationText) {
        try {
            URL pdfReference = new URL(pdfUrl);
            BufferedInputStream pdfContentStream = new BufferedInputStream(pdfReference.openStream());
            PDDocument file = PDDocument.load(pdfContentStream);
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            String pdfText = new PDFTextStripper().getText(file);
            return pdfText.contains(verificationText);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public boolean isImageBroken(WebElement imageElement) throws IOException {

        String Source = imageElement.getAttribute("src");
        BufferedImage image = null;
        try {
            image = ImageIO.read(newURL(Source));
            return true;
        } catch (IllegalArgumentException e) {
            image.toString();
            return false;
        }
    }

    public WebElement checkBrokenLinks(WebElement element) throws IOException {

        if (element != null) {
            String elementHref = element.getAttribute("href");

            if (elementHref != null && !elementHref.contains("javascript:;")) {
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(element.getAttribute("elementHref"));

                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode() != 200) return element;
                else return null;
            }
        }
        System.out.println(element);
        return element;
    }

    public List<WebElement> checkBrokenLinks(List<WebElement> elementList) throws IOException {
        List<WebElement> brokenList = new ArrayList();
        for (WebElement element : elementList) {
            if (element != null) {
                String elementHref = element.getAttribute("href");

                if (elementHref != null && !elementHref.contains("javascript:;")) {
                    HttpClient client = HttpClientBuilder.create().build();
                    HttpGet request = new HttpGet(elementHref);
                    HttpResponse response = client.execute(request);
                    if (response.getStatusLine().getStatusCode() != 200) brokenList.add(element);
                } else brokenList.add(element);
            }
        }
        return brokenList;
    }
}