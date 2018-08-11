package multipleTest;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class TestScript3 {
  @Test
  public void f() {
  }
  @BeforeTest
  public void beforeTest() {
	  System.out.println("Before Test---TestScript3");
  }

  @AfterTest
  public void afterTest() {
	  System.out.println("After Test---TestScript3");
  }

}
