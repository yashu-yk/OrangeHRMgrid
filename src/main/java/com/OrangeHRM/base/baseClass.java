package com.OrangeHRM.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.OrangeHRM.utilities.ExtentManager;
import com.OrangeHRM.utilities.LoggerManager;
import com.OrangeHRM.actiondriver.actionDriver;
import org.testng.annotations.Parameters;

public class baseClass {

	private static Properties prop;
//	private WebDriver driver;
	
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<actionDriver> actionDriver = new ThreadLocal<>();
	public static final Logger logger = LoggerManager.getLogger(baseClass.class);
	
	@BeforeSuite
	public void loadConfig() throws IOException {
		// Load the configuration file
		prop = new Properties();
		FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "/src/main/resources/config.properties");
		prop.load(fis);
		logger.info("config.properties file loaded");	
	}
	// browser name is getting passed from testng.xml
	@BeforeMethod
	@Parameters("browser")
	public synchronized void setup(String browser) throws IOException {
		System.out.println("Setting up WebDriver for:" + this.getClass().getSimpleName());
		launchBrowser(browser);
		configBrowser();
		staticWait(2);
		// Sample logger message
		logger.info("WebDriver Initialized and Browser Maximized");
		logger.trace("This is a Trace message");
		logger.error("This is a error message");
		logger.debug("This is a debug message");
		logger.fatal("This is a fatal message");
		logger.warn("This is a warm message");

		// Initialize ActionDriver for the current Thread
				actionDriver.set(new actionDriver(getDriver()));
				logger.info("ActionDriver initlialized for thread: " + Thread.currentThread().getId());

	}
	
	public void launchBrowser(String browser) {

		//String browser = prop.getProperty("browser");
		boolean seleniumGrid = Boolean.parseBoolean(prop.getProperty("seleniumGrid"));
		String gridURL = prop.getProperty("gridURL");

		if (seleniumGrid) {
			try {
				if (browser.equalsIgnoreCase("chrome")) {
					ChromeOptions options = new ChromeOptions();
					options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else if (browser.equalsIgnoreCase("firefox")) {
					FirefoxOptions options = new FirefoxOptions();
					options.addArguments("-headless");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else if (browser.equalsIgnoreCase("edge")) {
					EdgeOptions options = new EdgeOptions();
					options.addArguments("--headless=new", "--disable-gpu","--no-sandbox","--disable-dev-shm-usage");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else {
					throw new IllegalArgumentException("Browser Not Supported: " + browser);
				}
				logger.info("RemoteWebDriver instance created for Grid in headless mode");
			} catch (MalformedURLException e) {
				throw new RuntimeException("Invalid Grid URL", e);
			}
		} else {

			if (browser.equalsIgnoreCase("chrome")) {

				// Create ChromeOptions
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless"); // Run Chrome in headless mode
				options.addArguments("--disable-gpu"); // Disable GPU for headless mode
				//options.addArguments("--window-size=1920,1080"); // Set window size
				options.addArguments("--disable-notifications"); // Disable browser notifications
				options.addArguments("--no-sandbox"); // Required for some CI environments like Jenkins
				options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resource-limited environments

				// driver = new ChromeDriver();
				driver.set(new ChromeDriver(options)); // New Changes as per Thread
				ExtentManager.registerDriver(getDriver());
				logger.info("ChromeDriver Instance is created.");
			} else if (browser.equalsIgnoreCase("firefox")) {

				// Create FirefoxOptions
				FirefoxOptions options = new FirefoxOptions();
				options.addArguments("--headless"); // Run Firefox in headless mode
				options.addArguments("--disable-gpu"); // Disable GPU rendering (useful for headless mode)
				options.addArguments("--width=1920"); // Set browser width
				options.addArguments("--height=1080"); // Set browser height
				options.addArguments("--disable-notifications"); // Disable browser notifications
				options.addArguments("--no-sandbox"); // Needed for CI/CD environments
				options.addArguments("--disable-dev-shm-usage"); // Prevent crashes in low-resource environments

				// driver = new FirefoxDriver();
				driver.set(new FirefoxDriver(options)); // New Changes as per Thread
				ExtentManager.registerDriver(getDriver());
				logger.info("FirefoxDriver Instance is created.");
			} else if (browser.equalsIgnoreCase("edge")) {

				EdgeOptions options = new EdgeOptions();
				options.addArguments("--headless"); // Run Edge in headless mode
				options.addArguments("--disable-gpu"); // Disable GPU acceleration
				options.addArguments("--window-size=1920,1080"); // Set window size
				options.addArguments("--disable-notifications"); // Disable pop-up notifications
				options.addArguments("--no-sandbox"); // Needed for CI/CD
				options.addArguments("--disable-dev-shm-usage"); // Prevent resource-limited crashes

				// driver = new EdgeDriver();
				driver.set(new EdgeDriver(options)); // New Changes as per Thread
				ExtentManager.registerDriver(getDriver());
				logger.info("EdgeDriver Instance is created.");
			} else {
				throw new IllegalArgumentException("Browser Not Supported:" + browser);
			}
		}
	}
	
	public void configBrowser() {
		// Implicit Wait
				int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
				//boolean seleniumGrid = Boolean.parseBoolean(System.getProperty("seleniumGrid", prop.getProperty("seleniumGrid")));
				getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

				// maximize the browser
				//getDriver().manage().window().maximize();

				//navigate to url
				try {
					getDriver().get(prop.getProperty("url"));
				} catch (Exception e) {
					System.out.println("Failed to navigte to the URL");
				}
				
	}
	
	// Getter Method for WebDriver
		public static WebDriver getDriver() {

			if (driver.get() == null) {
				System.out.println("WebDriver is not initialized");
				throw new IllegalStateException("WebDriver is not initialized");
			}
			return driver.get();

		}
		
		// Getter Method for ActionDriver
		public static actionDriver getActionDriver() {

			if (actionDriver.get() == null) {
				System.out.println("ActionDriver is not initialized");
				throw new IllegalStateException("ActionDriver is not initialized");
			}
			return actionDriver.get();

		}
	
		// Driver setter method
		public void setDriver(ThreadLocal<WebDriver> driver) {
			this.driver = driver;
		}
	
	public static Properties getProperties() {
		return prop;
	}
	
	public void setProperties(Properties prop) {
		this.prop = prop;
	}
	
	// Static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
		@AfterMethod
		public synchronized void tearDown() {
			if (getDriver() != null) {
				try {
					getDriver().quit();
				} catch (Exception e) {
					System.out.println("unable to quit the driver:" + e.getMessage());
				}
			}
			logger.info("WebDriver instance is closed.");
			driver.remove();
			actionDriver.remove();
			// driver = null;
			// actionDriver = null;
			 //ExtentManager.endTest(); 
			 //--This has been implemented in TestListener
		}
}
