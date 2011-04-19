package uk.org.onegch.netkernel.layer2;

public class PrimaryArg extends IPrimaryArg
{
  private String argValue;

  public PrimaryArg(String argValue) {
    this.argValue= argValue;
  }

  public String getArgValue() {
    return argValue;
  }

  public void setArgValue(String argValue) {
    this.argValue= argValue;
  }
  
}
