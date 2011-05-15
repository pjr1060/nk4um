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

package org.netkernelroc.nk4um.web;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import org.netkernelroc.mod.layer2.AccessorUtil;
import org.netkernelroc.mod.layer2.Layer2AccessorImpl;

public class DataTablesConfigAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    HDSBuilder configParam= new HDSBuilder();

    configParam.pushNode("config");

    if (aContext.exists("arg:moderator")) {
      configParam.addNode("moderator", aContext.source("arg:moderator"));
    }

    if (aContext.exists("arg:start")) {
      configParam.addNode("start", aContext.source("arg:start"));
    }
    if (aContext.exists("arg:length")) {
      configParam.addNode("length", aContext.source("arg:length"));
    }

    if (aContext.exists("arg:sortColumn") && aContext.exists("arg:sortDirection")) {
      configParam.pushNode("sorting");
      configParam.pushNode("sort");
      configParam.addNode("column", aContext.source("arg:sortColumn"));
      configParam.addNode("direction", aContext.source("arg:sortDirection"));
      configParam.popNode();
      configParam.popNode();
    }

    if (aContext.exists("arg:search")) {
      configParam.addNode("search", aContext.source("arg:search"));
    }

    aContext.createResponseFrom(configParam.getRoot());
  }
}
