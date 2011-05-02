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
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                version="2.0">
  <xsl:param name="topicMeta"/>
  
  <xsl:template match="/">
    <tr>
      <td><xsl:value-of select="//forum_group"/></td>
      <td><xsl:value-of select="//forum"/></td>
      <td><xsl:value-of select="//title"/></td>
      <td>
        <xrl:include>
          <xrl:identifier>nk4um:web:author:displayName</xrl:identifier>
          <xrl:argument name="id"><literal type="string"><xsl:value-of select="//author_id"/></literal></xrl:argument>
          <xrl:argument name="truncate"><literal type="boolean">true</literal></xrl:argument>
        </xrl:include>
      </td>
      <td><xsl:value-of select="format-dateTime(nk4um:clean-date(//posted_date), '[D01]/[M01]/[Y] [H01]:[m01]')"/></td>
      <td><xsl:value-of select="$topicMeta//post_count"/></td>
      <td><xsl:value-of select="$topicMeta//view_count"/></td>
    </tr>
  </xsl:template>
  
  <xsl:function name="nk4um:clean-date">
    <xsl:param name="dateInput"/>
    
    <xsl:value-of select="replace($dateInput, ' ', 'T')"/>
  </xsl:function>
</xsl:stylesheet>
