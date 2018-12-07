package com.framework.stepdefinitions;

import com.framework.utility.applitoolscommands.ApplitoolsSetup;
import com.framework.utility.browsermanagement.SessionManager;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import javafx.application.Application;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.net.MalformedURLException;

public class Hooks {

    public Hooks() {
        new SessionManager();
        new ApplitoolsSetup();
    }

    private void beforeSetup(String urlString, Scenario scenario) throws MalformedURLException {
        SessionManager.setUpDriver(urlString, scenario);
        ApplitoolsSetup.initializeEyes(SessionManager.getDriver());
        System.out.println("\nBrowser session initiated for " + urlString + "\n");
    }

    private void takeScreenshotOnFailure(Scenario scenario) throws Exception {
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) SessionManager.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/png");
        }
    }

    @Before("@HomeUrl")
    public void beforeHomeLaunch(Scenario scenario) throws MalformedURLException {
        beforeSetup("HomeUrl", scenario);
    }

    @Before("@IpgUrl")
    public void beforeIpgLaunch(Scenario scenario) throws MalformedURLException {
        beforeSetup("IpgUrl", scenario);
    }

    @Before("@DigitalIdUrl")
    public void beforeDigitalIdLaunch(Scenario scenario) throws MalformedURLException {
        beforeSetup("DigitalIdUrl", scenario);
    }

    @After
    public void afterHook(Scenario scenario) throws Exception {
        takeScreenshotOnFailure(scenario);
        SessionManager.closeDriver();
        ApplitoolsSetup.closeEyes();
        System.out.println("\nBrowser session terminated\n");
    }
}
