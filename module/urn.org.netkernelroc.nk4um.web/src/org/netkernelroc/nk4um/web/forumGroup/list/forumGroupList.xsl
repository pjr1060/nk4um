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
                xmlns:nk4um="http://netkernelroc.org/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="forumGroups"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:forumGroup">
    <xsl:apply-templates select="$forumGroups//row" mode="forumGroupList">
      <xsl:with-param name="forumGroupTemplate" select="."/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="row" mode="forumGroupList">
    <xsl:param name="forumGroupTemplate"/>
    <xsl:apply-templates select="$forumGroupTemplate/*" mode="forumGroup">
      <xsl:with-param name="currentForumGroup" select="."/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="@* | node()" mode="forumGroup">
    <xsl:param name="currentForumGroup"/>
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current">
        <xsl:with-param name="currentForumGroup" select="$currentForumGroup"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:id" mode="forumGroup">
    <xsl:param name="currentForumGroup" as="node()"/>
    <xsl:value-of select="$currentForumGroup/id"/>
  </xsl:template>
</xsl:stylesheet>
