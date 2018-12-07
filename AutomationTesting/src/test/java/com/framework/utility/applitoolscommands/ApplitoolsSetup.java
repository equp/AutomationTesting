package com.framework.utility.applitoolscommands;

import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.fluent.Target;
import com.framework.utility.ReadProperties;
import com.framework.utility.webdriverdata.SessionManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URISyntaxException;
import java.util.Properties;

public class ApplitoolsSetup {

    private static Eyes eyes;
    private static Properties properties;

    public ApplitoolsSetup() {
        properties = ReadProperties.getProperties();
    }

    public static Eyes initializeEyes(WebDriver driver) {

        // Applitools parameters configuration
        if (eyes == null && (properties.getProperty("DoNotTakeScreenshots").equalsIgnoreCase("false"))) {
            eyes = new Eyes();
            try {
                eyes.setServerUrl(properties.getProperty("ApplitoolsUrl"));
                eyes.setApiKey(properties.getProperty("ApplitoolsKey"));
                eyes.setMatchLevel(MatchLevel.STRICT);
                eyes.setStitchMode(StitchMode.CSS);
                eyes.setHideScrollbars(true);
                eyes.setIgnoreCaret(true);
                eyes.setForceFullPageScreenshot(true);

                if (!properties.getProperty("browser").equalsIgnoreCase("Remote"))
                    eyes.open(driver, "AusPost", SessionManager.getScenarioNameToLog(), new RectangleSize(1440, 630));
                else eyes.open(driver, "AusPost", SessionManager.getScenarioNameToLog());
            } catch (URISyntaxException e) {
                eyes = null;
                e.printStackTrace();
            }
        }
        return eyes;
    }

    public static Eyes getEyes() {
        return eyes;
    }

    public static void closeEyes() {
        if (eyes != null && (properties.getProperty("DoNotTakeScreenshots").equalsIgnoreCase("false"))) {
            try {
                eyes.close(false);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            } finally {
                eyes.abortIfNotClosed();
            }
        }
        eyes = null;
    }

    public static void checkWithRegion(WebElement region) {
        if (properties.getProperty("DoNotTakeScreenshots").equalsIgnoreCase("false")) eyes.checkRegion(region);
    }

    public static void checkWithRegion(String name, WebElement region) {
        if (properties.getProperty("DoNotTakeScreenshots").equalsIgnoreCase("false")) eyes.checkRegion(region, name);
    }

    public static void checkWindow(String name) {
        if (properties.getProperty("DoNotTakeScreenshots").equalsIgnoreCase("false")) {
            eyes.checkWindow(name);
        }
    }

    public static void checkWindow() {
        if (properties.getProperty("DoNotTakeScreenshots").equalsIgnoreCase("false")) eyes.checkWindow();
    }

    public static void checkTargetRegionWithLayoutSetting(String name, By findBy) {
        if (properties.getProperty("DoNotTakeScreenshots").equalsIgnoreCase("false"))
            eyes.check(name, Target.region(findBy).layout());
    }
}
