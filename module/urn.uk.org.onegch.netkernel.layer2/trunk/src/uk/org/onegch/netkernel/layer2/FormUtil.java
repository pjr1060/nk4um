package uk.org.onegch.netkernel.layer2;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.NKFException;

public class FormUtil extends AccessorUtil
{
  public FormUtil(INKFRequestContext context) {
    super(context);
  }
  
  public boolean validateString(String identifier) throws NKFException {
    return validateString(identifier, -1, -1);
  }
  
  public boolean validateString(String identifier, int minLength, int maxLength) throws NKFException {
    if (context.exists(identifier)) {
      String value= context.source(identifier, String.class);
      
      if (minLength != -1 && value.length() < minLength) {
        return false;
      }

      if (maxLength != -1 && value.length() > maxLength) {
        return false;
      }
      
      return true;
    } else {
      return false;
    }
  }
  
  public boolean validateEmail(String identifier, boolean required) throws NKFException {
    if (context.exists(identifier)) {
      String emailAddress= context.source(identifier, String.class);
      return emailAddress.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w+)+$");
    } else {
      return !required;
    }
  }
}
