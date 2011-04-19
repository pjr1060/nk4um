package uk.org.onegch.netkernel.layer2;

public class ArgByValue extends IArg
{
  private Object argValue;

  public ArgByValue(String argName, Object argValue) {
    super(argName);
    this.argValue= argValue;
  }

  public Object getArgValue() {
    return argValue;
  }

  public void setArgValue(Object argValue) {
    this.argValue= argValue;
  }
  
}
