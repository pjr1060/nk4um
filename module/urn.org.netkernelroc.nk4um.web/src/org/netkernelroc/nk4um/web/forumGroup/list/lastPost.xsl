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
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://netkernelroc.org/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="lastPost"/>
  
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="$lastPost//row">
        <xsl:apply-templates select="node()"/>
      </xsl:when>
      <xsl:otherwise>
        <div/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@*[contains(., '${nk4um:title}')]">
    <xsl:attribute name="{name()}">
      <xsl:if test="$lastPost//title">
        <xsl:value-of select="replace(., '\$\{nk4um:title\}', $lastPost//title)"/>
      </xsl:if>
    </xsl:attribute>
  </xsl:template>
  <xsl:template match="nk4um:truncated-title">
    <xsl:value-of select="nk4um:truncate($lastPost//title)"/>
  </xsl:template>
  <xsl:template match="nk4um:postedDate">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($lastPost//posted_date), '[MNn] [Do], [Y]')"/>
  </xsl:template>
  <xsl:template match="nk4um:postedTime">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($lastPost//posted_date), '[H01]:[m01]')"/>
  </xsl:template>
  <xsl:template match="nk4um:authorId">
    <xsl:value-of select="$lastPost//author_id"/>
  </xsl:template>
  
  <xsl:template match="@*[contains(., '${nk4um:topicId}')]">
    <xsl:attribute name="{name()}">
      <xsl:if test="$lastPost//forum_topic_id">
        <xsl:value-of select="replace(., '\$\{nk4um:topicId\}', $lastPost//forum_topic_id)"/>
      </xsl:if>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:function name="nk4um:clean-date">
    <xsl:param name="dateInput"/>
    
    <xsl:value-of select="replace($dateInput, ' ', 'T')"/>
  </xsl:function>
  
  <xsl:function name="nk4um:truncate" as="xs:string">
    <xsl:param name="string" as="xs:string"/>
    <xsl:variable name="max-length" select="25"/>
    
    <xsl:choose>
      <xsl:when test="string-length($string) gt 25">
        <xsl:value-of select="concat(substring($string, 1, 22), '...')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$string"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
</xsl:stylesheet>
