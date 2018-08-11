package sample;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class TestScript2 {
  @Test
  public void f() {
  }
  @BeforeTest
  public void beforeTest() {
	  System.out.println("Before Test-TestScript2");
  }

  @AfterTest
  public void afterTest() {
	  System.out.println("Before Test-TestScript2");
  }

}
