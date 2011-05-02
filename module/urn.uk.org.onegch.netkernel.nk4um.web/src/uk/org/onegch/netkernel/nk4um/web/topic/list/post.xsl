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
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="post"/>
  <xsl:param name="postContent"/>
  <xsl:param name="user"/>
  <xsl:param name="userMeta"/>
  <xsl:param name="moderator" as="xs:boolean"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:forumId">
    <xsl:value-of select="$post//forum_id"/>
  </xsl:template>
  
  <xsl:template match="nk4um:title">
    <xsl:choose>
      <xsl:when test="$post//title/text()">
        <xsl:value-of select="$post//title"/>
      </xsl:when>
      <xsl:when test="text()">
        <xsl:value-of select="text()"/>
      </xsl:when>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="@*[contains(., '${nk4um:postId}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:postId\}', $post//id)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="nk4um:content">
    <xsl:copy-of select="$postContent//xhtml:html/xhtml:body/*" />
  </xsl:template>
  <xsl:template match="nk4um:rawContent">
    <xsl:value-of select="$post//content" />
  </xsl:template>
  <xsl:template match="nk4um:postedDate">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($post//posted_date), '[MNn] [Do], [Y]')"/>
  </xsl:template>
  <xsl:template match="nk4um:postedTime">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($post//posted_date), '[H01]:[m01]')"/>
  </xsl:template>
  <xsl:template match="nk4um:postedDateTime">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($post//posted_date), '[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01].[f001]Z')"/>
  </xsl:template>
  <xsl:template match="nk4um:displayName">
    <xsl:choose>
      <xsl:when test="$user//display_name/text()">
        <xsl:value-of select="nk4um:truncate($user//display_name)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="nk4um:truncate($user//email)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="@*[contains(., '${nk4um:displayName}')]">
    <xsl:variable name="displayName">
      <xsl:choose>
        <xsl:when test="$user//display_name/text()">
          <xsl:value-of select="$user//display_name"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$user//email"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:displayName\}', $displayName)"/>
    </xsl:attribute>
  </xsl:template>
  <xsl:template match="nk4um:userId">
    <xsl:value-of select="$user//id"/>
  </xsl:template>
  <xsl:template match="nk4um:postCount">
    <xsl:value-of select="$userMeta//post_count"/>
  </xsl:template>
  
  <xsl:template match="nk4um:moderator">
    <xsl:if test="$moderator">
      <xsl:apply-templates select="node()"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="nk4um:id">
    <xsl:value-of select="$post//id"/>
  </xsl:template>
  <xsl:template match="@*[contains(., '${nk4um:id}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:id\}', $post//id)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="@*[contains(., '${nk4um:status}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:status\}', $post//status)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="select[@name='status']/option">
    <xsl:copy>
      <xsl:if test="@value=$post//status">
        <xsl:attribute name="selected" select="'selected'"/>
      </xsl:if>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
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
