package uk.org.onegch.netkernel.layer2;

public class PrimaryArgByValue extends IPrimaryArg
{
  private Object argValue;

  public PrimaryArgByValue(Object argValue) {
    this.argValue= argValue;
  }

  public Object getArgValue() {
    return argValue;
  }

  public void setArgValue(Object argValue) {
    this.argValue= argValue;
  }
  
}
