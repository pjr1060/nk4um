<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="post"/>
  <xsl:param name="user"/>
  <xsl:param name="userMeta"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:forumId">
    <xsl:value-of select="$post//forum_id"/>
  </xsl:template>
  <xsl:template match="nk4um:title">
    <xsl:value-of select="$post//title"/>
  </xsl:template>
  <xsl:template match="nk4um:content">
    <xsl:value-of select="$post//content"/>
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
        <xsl:value-of select="$user//display_name"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$user//email"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="nk4um:userId">
    <xsl:value-of select="$user//id"/>
  </xsl:template>
  <xsl:template match="nk4um:postCount">
    <xsl:value-of select="$userMeta//post_count"/>
  </xsl:template>
  
  <xsl:function name="nk4um:clean-date">
    <xsl:param name="dateInput"/>
    
    <xsl:value-of select="replace($dateInput, ' ', 'T')"/>
  </xsl:function>
</xsl:stylesheet>
