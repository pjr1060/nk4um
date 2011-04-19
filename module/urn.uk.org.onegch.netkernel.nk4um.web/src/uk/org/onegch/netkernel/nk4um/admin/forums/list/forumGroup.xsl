<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (C) 2010-2011 by Chris Cormack
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy
  ~ of this software and associated documentation files (the "Software"), to deal
  ~ in the Software without restriction, including without limitation the rights
  ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  ~ copies of the Software, and to permit persons to whom the Software is
  ~ furnished to do so, subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in
  ~ all copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  ~ THE SOFTWARE.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:template match="/">
    <div style="clear: both; margin-bottom: 4em;">
      <h3><xsl:apply-templates select="//title"/></h3>
      <p><xsl:apply-templates select="//description"/></p>
      <div style="clear: both">
        <span style="padding-right: 5px; padding-left: 5px;"> 
          <a class="btn2" onclick="parent.location='/nk4um/forumGroup/{//id}/edit'"><i>&#160;</i><span><i>&#160;</i><span>&#160;</span>Edit Forum Group</span></a> 
        </span>
      </div>
      <div style="clear: both">
        <xrl:include>
          <xrl:identifier>nk4um:admin:forums:forumList</xrl:identifier>
          <xrl:argument name="id"><literal type="string"><xsl:value-of select="//id"/></literal></xrl:argument>
        </xrl:include>
      </div>
      <div style="clear: both">
        <span style="padding-right: 5px; padding-left: 5px;"> 
          <a class="btn2" onclick="parent.location='/nk4um/forumGroup/{//id}/forum/new'"><i>&#160;</i><span><i>&#160;</i><span>&#160;</span>Add Forum</span></a> 
        </span>
      </div>
    </div>
  </xsl:template>
</xsl:stylesheet>
