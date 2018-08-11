package example;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class TestScript1 {
  @Test
  public void f() {
	  assert("abc".equalsIgnoreCase("xyz"));
	  System.out.println("testscript1 before test");
  }
  
  @Test
  public void p() {
	  assert(1==1);
	  System.out.println("testscript1 after test");

  }

}
