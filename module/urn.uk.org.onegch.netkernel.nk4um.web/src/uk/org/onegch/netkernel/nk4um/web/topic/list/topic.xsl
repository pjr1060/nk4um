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
                xmlns:xrl="http://netkernel.org/xrl"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="topic"/>
  <xsl:param name="topicMeta"/>
  <xsl:param name="url" as="xs:string"/>
  <xsl:param name="moderator" as="xs:boolean"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:id">
    <xsl:value-of select="$topic//id"/>
  </xsl:template>

  <xsl:template match="nk4um:url">
    <xsl:value-of select="$url"/>
  </xsl:template>

  <xsl:template match="nk4um:lastPostTime">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($topicMeta//last_post_time), '[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01].[f001]Z')"/>
  </xsl:template>
  
  <xsl:template match="@*[contains(., '${nk4um:id}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:id\}', $topic//id)"/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="@*[contains(., '${nk4um:status}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:status\}', $topic//status)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="@*[contains(., '${nk4um:title}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:title\}', $topic//title)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="nk4um:moderator">
    <xsl:if test="$moderator">
      <xsl:apply-templates select="node()"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="select[@name='status']/option">
    <xsl:copy>
      <xsl:if test="@value=$topic//status">
        <xsl:attribute name="selected" select="'selected'"/>
      </xsl:if>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="input[@name='locked']">
    <xsl:copy>
      <xsl:if test="xs:boolean($topic//locked)">
        <xsl:attribute name="checked" select="'checked'"/>
      </xsl:if>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:function name="nk4um:clean-date">
    <xsl:param name="dateInput"/>

    <xsl:value-of select="replace($dateInput, ' ', 'T')"/>
  </xsl:function>
</xsl:stylesheet>
