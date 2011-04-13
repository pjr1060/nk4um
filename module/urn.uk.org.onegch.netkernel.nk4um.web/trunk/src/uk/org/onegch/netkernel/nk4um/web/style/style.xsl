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
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="#all"
                version="2.0">
  
  <xsl:output method="xhtml"></xsl:output>
  
  <xsl:param name="content"/>
  
  <xsl:template match="@* | node()" mode="#all">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:title">
    <xsl:value-of select="$content/nk4um:page/nk4um:title"/>
  </xsl:template>
  <xsl:template match="nk4um:heading">
    <xsl:value-of select="$content/nk4um:page/nk4um:heading"/>
  </xsl:template>
  <xsl:template match="nk4um:tagLine">
    <xsl:value-of select="$content/nk4um:page/nk4um:tagLine"/>
  </xsl:template>
  
  <xsl:template match="nk4um:breadcrumbs">
    <xsl:apply-templates select="$content/nk4um:page/nk4um:breadcrumbs/*"/>
  </xsl:template>
  
  <xsl:template match="nk4um:content" mode="#default">
    <xsl:apply-templates select="$content/nk4um:page/nk4um:content/*"/>
  </xsl:template>
  
  <xsl:template match="nk4um:head" mode="#default">
    <xsl:apply-templates select="$content/nk4um:page/nk4um:head/*"/>
    <xsl:apply-templates select="$content//nk4um:snippet/nk4um:head/*"/>
  </xsl:template>
  
  <xsl:template match="nk4um:snippet">
    <xsl:apply-templates select="nk4um:content/*"/>
  </xsl:template>
  
  <xsl:template match="nk4um:group" mode="#default">
    <xsl:apply-templates select="node()" mode="#current"/>
  </xsl:template>
</xsl:stylesheet>