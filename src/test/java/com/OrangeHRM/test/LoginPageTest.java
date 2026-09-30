package com.OrangeHRM.test;

import com.OrangeHRM.utilities.DataProviders;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.OrangeHRM.base.baseClass;
import com.OrangeHRM.pages.HomePage;
import com.OrangeHRM.pages.LoginPage;
import com.OrangeHRM.utilities.ExtentManager;

@Listeners(com.OrangeHRM.listeners.TestListener.class)
public class LoginPageTest extends baseClass{

	
	private LoginPage loginPage;
	private HomePage homePage;
	
	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage  = new HomePage(getDriver());
	}

	// this validLoginData dataProvider is a method in dataProvider class that will return data of sheet named validLoginData
	@Test(dataProvider="validLoginData", dataProviderClass = DataProviders.class)
	public void verifyValidLoginTest(String username, String password) {
		//ExtentManager.startTest("Valid Login Test");


		System.out.println("Running testMethod1 on thread: " + Thread.currentThread().getId());
		ExtentManager.logStep("Navigating to Login Page entering username and password");

		//without using dataProvider
		//loginPage.login("admin","admin123");

		//with using dataProvider
		loginPage.login(username, password);
		ExtentManager.logStep("Verifying Admin tab is visible or not");
		Assert.assertTrue(homePage.isAdminTabVisible(),"Admin tab should be visible after successfull login ");
		ExtentManager.logStep("Validation Successful");
		homePage.logout();
		ExtentManager.logStep("Logged out Successfully!");


		//staticWait(2);
		//Assert.assertTrue(loginPage.isLoginTextDisplyed(),"This is not login page");
	}
	
	@Test(dataProvider="inValidLoginData", dataProviderClass = DataProviders.class)
	public void inValidLoginTest(String username, String password) {
		//ExtentManager.startTest("In-valid Login Test!");
		ExtentManager.logStep("Navigating to Login Page entering username and password");
		//loginPage.login("admin","admin");
		//with using dataProvider
		loginPage.login(username, password);
		String expectedErrorMessage = "Invalid credentials";
		Assert.assertTrue(loginPage.verifyErrorMessage(expectedErrorMessage),"Test Failed: Invalid error message");
		ExtentManager.logStep("Validation Successful");
	}
}
