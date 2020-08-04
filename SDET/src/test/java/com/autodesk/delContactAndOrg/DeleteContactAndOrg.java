package com.autodesk.delContactAndOrg;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.autodeskcrm.gerericutils.ExcelLib;
import com.autodeskcrm.gerericutils.FileLib;
import com.autodeskcrm.gerericutils.WebDriverUtils;

public class DeleteContactAndOrg {
	@Test
	public static void deleteConAndOrg() throws Throwable {

		WebDriverUtils web = new WebDriverUtils();
		FileLib fLib = new FileLib();
		ExcelLib excelLib = new ExcelLib();


		/* read data from property File */
		String USERNAME = fLib.getPropertyKeyValue("username");
		String PASSWORD = fLib.getPropertyKeyValue("password");
		String URL = fLib.getPropertyKeyValue("url");
		String BROWSER = fLib.getPropertyKeyValue("browser");

		/* read test script specific data*/
		String orgName = excelLib.getExcelData("contact", 1, 2)+ "_"+ web.getRamDomNum();
		String org_Type = excelLib.getExcelData("contact", 1, 3);
		String org_industry = excelLib.getExcelData("contact", 1, 4);
		String contactName = excelLib.getExcelData("contact", 1, 5);

		/*step 1 : launch the browser*/
		WebDriver driver = null;

		if(BROWSER.equals("chrome")) {
			driver= new ChromeDriver();
		} else if(BROWSER.equals("firefox")) {
			driver = new FirefoxDriver();
		}else if(BROWSER.equals("ie")) {
			driver = new InternetExplorerDriver();
		}else {
			driver = new FirefoxDriver();
		}


		web.waitForPagetoLoad(driver);
		driver.get(URL);
		//step1:-enter username and password
		driver.findElement(By.name("user_name")).sendKeys(USERNAME);
		driver.findElement(By.name("user_password")).sendKeys(PASSWORD,Keys.ENTER);

		WebDriverWait wait=new WebDriverWait(driver, 10);

		/*step 2 : navigate to Org page*/
		driver.findElement(By.linkText("Organizations")).click();


		/*step 3 : navigate to create new Org page*/
		driver.findElement(By.xpath("//img[@alt='Create Organization...']")).click();

		/*step 4 : create Org*/
		driver.findElement(By.name("accountname")).sendKeys(orgName);


		WebElement  swb1 = driver.findElement(By.name("accounttype"));
		web.select(swb1, org_Type);

		WebElement  swb2 = driver.findElement(By.name("industry"));
		web.select(swb2, org_industry);

		driver.findElement(By.xpath("//input[@title='Save [Alt+S]']")).click();

		/*step 5 : verify the Org*/
		String actOrgName = driver.findElement(By.xpath("//span[@class='dvHeaderText']")).getText();

		Assert.assertTrue(actOrgName.contains(orgName));


		/*step 6 : navigate to Contact page*/
		driver.findElement(By.linkText("Contacts")).click();

		/*step 7 : navigate to create new Contact page*/
		driver.findElement(By.xpath("//img[@alt='Create Contact...']")).click();

		/*step 8: creat new Contact page*/
		driver.findElement(By.name("lastname")).sendKeys(contactName);
		driver.findElement(By.xpath("//input[@name='account_name']/following-sibling::img")).click();

		//open new tab
		web.switchToNewTab(driver, "specific_contact_account_address");

		driver.findElement(By.name("search_text")).sendKeys(orgName);
		driver.findElement(By.name("search")).click();
		driver.findElement(By.linkText(orgName)).click();

		//come back to parent Window
		web.switchToNewTab(driver, "Administrator - Contacts");

		driver.findElement(By.xpath("//input[@title='Save [Alt+S]']")).click();

		/*step  9: verify the Org*/
		String actconatct = driver.findElement(By.xpath("//span[@class='dvHeaderText']")).getText();
		Assert.assertTrue(actconatct.contains(contactName));



		//step10:- search for the contact
		driver.findElement(By.linkText("Contacts")).click();
		WebElement in = driver.findElement(By.id("bas_searchfield"));
		web.select(in, "Last Name");

		driver.findElement(By.name("search_text")).sendKeys(contactName);
		driver.findElement(By.xpath("//input[@value=' Search Now ']")).click();
		WebElement status = driver.findElement(By.xpath("//img[contains(@src,'/status.gif')]"));
		wait.until(ExpectedConditions.invisibilityOf(status));

		//step11:- capture the organization name
		String orgname = driver.findElement(By.xpath("//a[@title='Organizations']")).getText();
		driver.findElement(By.xpath("//a[.='"+contactName+"']/ancestor::td//input[@name='selected_id']")).click();
		driver.findElement(By.xpath("//input[@value='Delete']")).click();
		//step4:- accept the alert
		web.alertOk(driver);

		//step12:- navigate to organization page and search the organization
		driver.findElement(By.linkText("Organizations")).click();
		WebElement inOrg=driver.findElement(By.id("bas_searchfield"));
		web.select(inOrg, "Organization Name");

		driver.findElement(By.name("search_text")).sendKeys(orgname);
		driver.findElement(By.xpath("//input[@value=' Search Now ']")).click();

		wait.until(ExpectedConditions.invisibilityOf(status));

		driver.findElement(By.xpath("//a[.='"+orgname+"']/ancestor::td//input[@name='selected_id']")).click();
		driver.findElement(By.xpath("//input[@value='Delete']")).click();

		//step13:- accept the alert
		web.alertOk(driver);
		
		//step14:- close the browser
		driver.close();

	}
}
