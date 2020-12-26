package org.jekajops.call_api.call_managers.callbine.selenium;

import com.google.common.base.Predicate;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.jekajops.core.utils.files.FileManager;
import org.jekajops.core.utils.files.PropertiesManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class UploadRobot {
    private static final String baseUrl = "https://callbine.ru/app/";
    private static final String audioclipsUrlAddition = "?action=listAudioclips";
    private static final String uploadButtonXPath = "/html/body/div[1]/div[2]/p/button[1]";
    private static final String inputFileXPath = "//*[@id=\"step1upload\"]/a/input";
    private static final String inputNameXPath = "//*[@id=\"audioclips_name_id\"]";
    private static final String sendButtonXPath = "//*[@id=\"audioAdd\"]";
    private static final String inputLoginXPath = "//*[@id=\"inputEmail\"]";
    private static final String inputPasswordXPath = "//*[@id=\"inputPassword\"]";
    private static final String loginButtonXPath = "/html/body/div/form/button";
    private final String login;
    private final String password;
    private final WebDriver webDriver;
    private WebDriverWait wait;


    public UploadRobot() throws WebDriverException {
        Properties properties = PropertiesManager.getProperties("callbineLogin");
        webDriver = initWebDriver();
        //wait = new WebDriverWait(webDriver, 10);
        login = properties.getProperty("login");
        password = properties.getProperty("password");
        openPage(baseUrl);
        login();
    }

    private WebDriver initWebDriver() {
        System.setProperty("GOOGLE_CHROME_BIN", "/app/.apt/usr/bin/google-chrome");
        System.setProperty("CHROMEDRIVER_PATH", "/app/.chromedriver/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        if (getOS().contains("win")) {
            System.setProperty("webdriver.chrome.driver", FileManager.getFileFromResources("chromedriver.exe").getAbsolutePath());
        } else {
            //options.setBinary("/app/.apt/usr/bin/google-chrome");
            String binaryPath= null;
            try {
                binaryPath = EnvironmentUtils.getProcEnvironment().get("GOOGLE_CHROME_SHIM");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Path: "+binaryPath);
            options.setBinary(binaryPath);
        }
        options.addArguments("--enable-javascript");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        return new ChromeDriver(options);
    }

    public String uploadFile(File file, String name) throws WebDriverException {
        openPage(baseUrl + audioclipsUrlAddition);
        System.out.println("Page Open");
        clickUploadButton();
        System.out.println("Click Upload Button");
        inputFile(file.getAbsolutePath());
        System.out.println("Input File");
        inputName(name);
        System.out.println("Input Name");
        clickSaveButton();
        System.out.println("Click Save Button");
        String result = getId(name);
        System.out.println("return: " + result);
        return result;
    }

    private void login() {
        try {
            findElement(inputLoginXPath).sendKeys(login);
            findElement(inputPasswordXPath).sendKeys(password);
            findElement(loginButtonXPath).click();
        } catch (NoSuchElementException ignored) {
        }
    }

    private void openPage(String url) {
        webDriver.get(url);
    }

    private void clickUploadButton() {
        var locator = By.xpath(uploadButtonXPath);
        var wait = new WebDriverWait(webDriver, 20);
        wait.until(presenceOfElementLocated(locator));
        findElement(uploadButtonXPath).click();
    }

    private void inputFile(String filePath) {
        var locator = By.xpath(inputFileXPath);
        var wait = new WebDriverWait(webDriver, 20);
        wait.until(presenceOfElementLocated(locator));
        var element = findElement(inputFileXPath);
        element.sendKeys(filePath);
    }

    private void inputName(String name) {
        var locator = By.xpath(inputNameXPath);
        var wait = new WebDriverWait(webDriver, 20);
        wait.until(and(elementToBeClickable(locator), presenceOfElementLocated(locator)));
        var element = findElement(inputNameXPath);
        element.sendKeys(name);
    }

    private void clickSaveButton() {
        findElement(sendButtonXPath).click();
    }

    private WebElement findElement(String xpath) {
        return webDriver.findElement(By.xpath(xpath));
    }

    private String getId(String fileName) {
        var wait = new WebDriverWait(webDriver, 20);
        wait.until(invisibilityOfElementLocated(By.xpath(inputNameXPath)));
        wait.until(webDriver -> webDriver.getPageSource().contains(fileName));
        return parseIdElement().getText();
    }

    private WebElement parseIdElement(){
        return webDriver
                .findElement(By.xpath("/html/body/div[1]/div[4]/div/table/tbody"))
                .findElements(By.tagName("tr")).get(0)
                .findElements(By.tagName("td")).get(0);
    }

    public void close() {
        webDriver.quit();
    }

    String getOS() {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    }

    public static void main(String[] args) {
        UploadRobot uploadRobot = new UploadRobot();
        File file = FileManager.getFileFromResources("audio\\temp\\Novyye-Avtomobilistam-Evakuiroval_po_oshibke2804385600906408537.mp3");
        String name = "evakuiroval_po_oshibke";
        //var res = uploadRobot.uploadFile(file, name);
        //System.out.println(res);
    }
}
