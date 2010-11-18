package uk.org.onegch.netkernel.layer2;

public abstract class IArg
{
  private String argName;

  public IArg(String argName) {
    super();
    this.argName= argName;
  }

  public String getArgName() {
    return argName;
  }

  public void setArgName(String argName) {
    this.argName= argName;
  }
}
