package com.gfk.senbot.framework.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.openqa.selenium.WebDriver;

public class SeleniumManagerTest {

    /**
     * Is the timeout given to the constructor is test to a value, the vallue
     * has to be set in the object
     * 
     * @throws IOException
     */
    @Test
    public void testSeleniumManager_timeoutSetting() throws IOException {
        SeleniumManager localSeleniumManager = new SeleniumManager("http://www.gfk.com", "http://someHub", false, "FF,LATEST,WINDOWS", 1000, 800, 5);
        assertEquals(5, localSeleniumManager.getTimeout());
    }

    /**
     * 
     * @throws IOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSenBotContext_missingHubWhileOnGrid() throws IOException {
        new SeleniumManager("http://www.gfk.com", "", true, "FF,LATEST,WINDOWS", 1000, 800, 5);
    }

    @Test
    public void testSenBotContext_missingHubWhileGridDisabled() throws IOException {
        new SeleniumManager("http://www.gfk.com", "", false, "FF,LATEST,WINDOWS", 1000, 800, 5);
    }

    @Test
    public void testSenBotContext_hubProperlySet() throws IOException {
        String expectedHub = "http://some_hub";
        SeleniumManager manager = new SeleniumManager("http://www.gfk.com", expectedHub, false, "FF,LATEST,WINDOWS", 1000, 800, 5);
        assertEquals(new URL(expectedHub), manager.getSeleniumHub());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSenBotContext_missingSeleniumTestTarget() throws IOException {
        new SeleniumManager("http://www.gfk.com", "http://someHub", false, "", 1000, 800, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSenBotContext_misconfiguredSeleniumTarget() throws IOException {
        new SeleniumManager("http://www.gfk.com", "http://someHub", false, "FF,LATEST,WINDOWS;CH,LATEST", 1000, 800, 5);
    }

    @Test(expected = MalformedURLException.class)
    public void testSenBotContext_mallformattedHubUrl() throws IOException {
        new SeleniumManager("http://www.gfk.com", "I'm an invalid URL", false, "FF,LATEST,WINDOWS", 1000, 800, 5);
    }

    @Test
    public void testSenBotContext_blankImplicitWait() throws IOException {
    	SeleniumManager seleniumManager = new SeleniumManager("http://www.gfk.com", "http://someHub", false, "FF,LATEST,WINDOWS", 1000, 800, 5, "");
    	assertNull(seleniumManager.getImplicitTimeout());
    }

    @Test
    public void testSenBotContext_implicitWait() throws IOException {
    	SeleniumManager seleniumManager = new SeleniumManager("http://www.gfk.com", "http://someHub", false, "FF,LATEST,WINDOWS", 1000, 800, 5, "4");
    	assertEquals(new Integer(4), seleniumManager.getImplicitTimeout());
    }

    @Test
    public void testSenBotContext_seleniumTestEnvironmentTargetCreation() throws IOException {
        SeleniumManager seleniumManager = new SeleniumManager("http://www.gfk.com", "http://someHub", false, "FF,LATEST,ANY;CH,LATEST,ANY", 1000, 800, 5);

        assertFalse(seleniumManager.getSeleniumTestEnvironments().isEmpty());
        assertEquals("FF", seleniumManager.getSeleniumTestEnvironments().get(0).getBrowser());
        assertEquals("LATEST", seleniumManager.getSeleniumTestEnvironments().get(0).getBrowserVersion());
        assertEquals("ANY", seleniumManager.getSeleniumTestEnvironments().get(0).getOS().name());
        assertNotNull(seleniumManager.getSeleniumTestEnvironments().get(0).getWebDriver());

        assertEquals("CH", seleniumManager.getSeleniumTestEnvironments().get(1).getBrowser());
        assertEquals("LATEST", seleniumManager.getSeleniumTestEnvironments().get(1).getBrowserVersion());
        assertEquals("ANY", seleniumManager.getSeleniumTestEnvironments().get(1).getOS().name());
        assertNotNull(seleniumManager.getSeleniumTestEnvironments().get(1).getWebDriver());

        seleniumManager.cleanUp();

        assertTrue(seleniumManager.getSeleniumTestEnvironments().isEmpty());
    }

    @Test
    public void testGetWebDriver_firstCall() {
        assertNull(SenBotContext.getSeleniumDriver());
    }
    
}
