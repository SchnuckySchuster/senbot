package com.gfk.senbot.framework.context;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager of all selenium related configuration and runtime variables.
 * The selenium driver is initiated here.
 * Also the settings for running on the grid or on the local machine.
 * 
 * @author joostschouten
 *
 */
public class SeleniumManager {

    private static Logger                log                      = LoggerFactory.getLogger(SeleniumManager.class);

    private String                       defaultDomain            = null;
    private URL                          seleniumHub              = null;
    private boolean                      runOnGrid                = false;
    private ArrayList<TestEnvironment>   seleniumTestEnvironments = new ArrayList<TestEnvironment>();

    private final Integer                defaultWindowWidth;
    private final Integer                defaultWindowHeight;
    private int                          timeout;
    private Integer                      implicitTimeout;

    private Map<Thread, TestEnvironment> associatedEnvironment    = new HashMap<Thread, TestEnvironment>();

    public SeleniumManager(String defaultDomain, String seleniumHubIP, boolean runOnGrid, String target, int defaultWindowWidth, int defaultWindowHeight, int aTimeout) throws IOException {
        this(defaultDomain, seleniumHubIP, runOnGrid, target, defaultWindowWidth, defaultWindowWidth, aTimeout, null);
    }

    /**
     * {@link SeleniumManager} constructor managed by spring
     * 
     * @param defaultDomain domain the whole selenium related SenBot will work from
     * @param seleniumHubIP if running on a grid this is the hub ip to be used
     * @param runOnGrid run the tests on a grid if true and on a single box if false
     * @param target The target environements to run the selenium tests on
     * @param defaultWindowWidth browser window width to start the browser with
     * @param defaultWindowHeight browser window height to start the browser with
     * @param aTimeout implicit timeout to be used by selenium when performing a dom lookup or page refresh
     * @throws IOException
     */
    public SeleniumManager(String defaultDomain, String seleniumHubIP, boolean runOnGrid, String target, int defaultWindowWidth, int defaultWindowHeight, int aTimeout, String implicitTimeout)
            throws IOException {

        this.defaultDomain = defaultDomain;
        this.defaultWindowWidth = defaultWindowWidth;
        this.defaultWindowHeight = defaultWindowHeight;
        this.timeout = aTimeout;
        this.runOnGrid = runOnGrid;
        if (!StringUtils.isBlank(implicitTimeout)) {
            this.implicitTimeout = Integer.parseInt(implicitTimeout);
        }
        this.seleniumHub = (StringUtils.isBlank(seleniumHubIP)) ? null : new URL(seleniumHubIP);

        if (runOnGrid && StringUtils.isBlank(seleniumHubIP)) {
            throw new IllegalArgumentException("The selenium hub IP property cannot be blank when senbot is running in grid mode. Refer to senbot-runner.properties");
        }

        if (StringUtils.isBlank(target)) {
            throw new IllegalArgumentException("The selenium target environment property cannot be blank. Refer to senbot-runner.properties");
        } else {
            for (String ii : target.split(";")) {
                if (ii.split(",").length != 3) {
                    throw new IllegalArgumentException("Each selenium target environment defenition should contain three parts (browser, version, OS). Refer to senbot-runner.properties");
                }
                String[] targetEnv = ii.split(",");
                TestEnvironment testEnvironment = new TestEnvironment(targetEnv[0].trim(), targetEnv[1].trim(), Platform.valueOf(targetEnv[2].trim()));
                seleniumTestEnvironments.add(testEnvironment);
            }
        }
    }

    /**
     * Cleaning up
     */
    protected synchronized void cleanUp() {
        for (TestEnvironment env : seleniumTestEnvironments) {
            env.cleanupAllDrivers();
        }
        seleniumTestEnvironments.clear();
    }

    /**
     * @return the deault domain
     */
    public String getDefaultDomain() {
        return defaultDomain;
    }

    /**
     * @return The URL of the Selenium Hub
     */
    public URL getSeleniumHub() {
        return seleniumHub;
    }

    /**
     * @return True if test runs on Selenium Grid
     */
    public boolean isRunOnGrid() {
        return runOnGrid;
    }

    /**
     * @return List of all test environments managed by the SeleniumManager 
     */
    public List<TestEnvironment> getSeleniumTestEnvironments() {
        return seleniumTestEnvironments;
    }

    /**
     * 
     * Associates Thread with TestEnvironment
     * 
     * @param environment {@link TestEnvironment} to associate with the current {@link Thread}
     * @throws Exception 
     */
    public void associateTestEnvironment(TestEnvironment aTestEnvironment) throws Exception {

        log.debug("Associate TestEnvironment: " + aTestEnvironment.toPrettyString() + " with thread: " + Thread.currentThread().toString());

        associatedEnvironment.put(Thread.currentThread(), aTestEnvironment);
    }

    /**
     * Deletes association of Thread with TestEnvironment
     * 
     * @throws InterruptedException
     */
    public void deAssociateTestEnvironment() throws InterruptedException {
        Thread currentThread = Thread.currentThread();
        TestEnvironment removed = associatedEnvironment.remove(currentThread);

        log.debug("Deassociated TestEnvironment: " + removed.toPrettyString() + " from thread: " + currentThread.toString());

    }

    /**
     * @return {@link TestEnvironment} associated with the current {@link Thread}
     */
    public TestEnvironment getAssociatedTestEnvironment() {
        return associatedEnvironment.get(Thread.currentThread());
    }

    /**
     * @return Timeout in [s]
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @return defaultWindowWidth in [pixel]
     */
    public int getDefaultWindowWidth() {
        return defaultWindowWidth;
    }

    /**
     * @return defaultWindowHeight in [pixel]
     */
    public int getDefaultWindowHeight() {
        return defaultWindowHeight;
    }

    public Integer getImplicitTimeout() {
        return implicitTimeout;
    }

}
