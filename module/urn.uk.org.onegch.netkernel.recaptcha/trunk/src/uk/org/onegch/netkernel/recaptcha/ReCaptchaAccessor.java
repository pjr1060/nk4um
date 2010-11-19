package uk.org.onegch.netkernel.recaptcha;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

public class ReCaptchaAccessor extends StandardAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext) throws Exception {
    IHDSNode recaptchaConfig= aContext.source("res:/etc/system/recaptchaConfig.xml", IHDSNode.class);
    
    HDSBuilder reCaptchaBuilder= new HDSBuilder();
    reCaptchaBuilder.pushNode("div");
    reCaptchaBuilder.pushNode("script");
    reCaptchaBuilder.addNode("@type", "text/javascript");
    reCaptchaBuilder.addNode("@src", "http://www.google.com/recaptcha/api/challenge?k=" + recaptchaConfig.getFirstValue("//public_key"));
    reCaptchaBuilder.popNode();
    reCaptchaBuilder.pushNode("noscript");
    reCaptchaBuilder.pushNode("iframe");
    reCaptchaBuilder.addNode("@src", "http://www.google.com/recaptcha/api/noscript?k=" + recaptchaConfig.getFirstValue("//public_key"));
    reCaptchaBuilder.addNode("@height", "300");
    reCaptchaBuilder.addNode("@width", "500");
    reCaptchaBuilder.addNode("@frameborder", "0");
    reCaptchaBuilder.popNode();
    reCaptchaBuilder.addNode("br", null);
    reCaptchaBuilder.pushNode("textarea");
    reCaptchaBuilder.addNode("@name", "recaptcha_challenge_field");
    reCaptchaBuilder.addNode("@rows", "3");
    reCaptchaBuilder.addNode("@cols", "40");
    reCaptchaBuilder.popNode();
    reCaptchaBuilder.pushNode("input");
    reCaptchaBuilder.addNode("@type", "hidden");
    reCaptchaBuilder.addNode("@name", "recaptcha_response_field");
    reCaptchaBuilder.addNode("@value", "manual_challenge");
    
    aContext.createResponseFrom(reCaptchaBuilder.getRoot());
  }
}
