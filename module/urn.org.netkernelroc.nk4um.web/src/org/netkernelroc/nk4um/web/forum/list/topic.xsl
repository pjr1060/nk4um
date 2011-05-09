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
  <xsl:param name="topic"/>
  <xsl:param name="topicMeta"/>
  <xsl:param name="moderator" as="xs:boolean"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:id">
    <xsl:value-of select="$topic//id"/>
  </xsl:template>
  <xsl:template match="nk4um:title">
    <xsl:value-of select="$topic//title"/>
  </xsl:template>
  <xsl:template match="nk4um:description">
    <xsl:value-of select="$topic//description"/>
  </xsl:template>
  <xsl:template match="nk4um:status">
    <xsl:variable name="status">
      <xsl:choose>
        <xsl:when test="$topic//status='active' and $topic//visible=false()">
          <xsl:text>empty</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$topic//status"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:value-of select="$status"/>
  </xsl:template>
  <xsl:template match="nk4um:statusOrder">
    <xsl:variable name="statusOrder">
      <xsl:choose>
        <xsl:when test="$topic//status='active' and $topic//visible=false()">
          <xsl:text>2.5</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$topic//status_order"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:value-of select="$statusOrder"/>
  </xsl:template>
  
  <xsl:template match="nk4um:postCount">
    <xsl:value-of select="$topicMeta//post_count"/>
  </xsl:template>
  <xsl:template match="nk4um:viewCount">
    <xsl:value-of select="$topicMeta//view_count"/>
  </xsl:template>
  
  <xsl:template match="@*[contains(., '${nk4um:id}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:id\}', $topic//id)"/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="@*[contains(., '${nk4um:status}')]">
    <xsl:variable name="status">
      <xsl:choose>
        <xsl:when test="$topic//status='active' and $topic//visible=false()">
          <xsl:text>empty</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$topic//status"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:status\}', $status)"/>
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
</xsl:stylesheet>
