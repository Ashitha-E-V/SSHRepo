package example;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class TestScript1 {
  @Test
  public void f() {
	  assert("abc".equalsIgnoreCase("xyz"));
  }
  
  @Test
  public void p() {
	  assert(1==1);
  }

}
