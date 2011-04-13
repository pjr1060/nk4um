/*
 * Copyright (C) 2010-2011 by Chris Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
