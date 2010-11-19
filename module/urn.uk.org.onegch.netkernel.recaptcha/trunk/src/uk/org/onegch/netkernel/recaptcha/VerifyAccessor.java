package uk.org.onegch.netkernel.recaptcha;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

public class VerifyAccessor extends StandardAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext) throws Exception {
    IHDSNode recaptchaConfig= aContext.source("res:/etc/system/recaptchaConfig.xml", IHDSNode.class);
    
    ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
    reCaptcha.setPrivateKey((String)recaptchaConfig.getFirstValue("//private_key"));
    
    ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(aContext.source("arg:remoteAddr", String.class),
                                                                aContext.source("arg:challenge", String.class),
                                                                aContext.source("arg:response", String.class));
    
    aContext.createResponseFrom(reCaptchaResponse.isValid());
    System.out.println(reCaptchaResponse.getErrorMessage());
  }
}
