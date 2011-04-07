<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
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
  
  <xsl:template match="nk4um:postedDate">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($lastPost//posted_date), '[MNn] [Do], [Y]')"/>
  </xsl:template>
  <xsl:template match="nk4um:postedTime">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($lastPost//posted_date), '[H01]:[m01]')"/>
  </xsl:template>
  <xsl:template match="nk4um:postedDateTime">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($lastPost//posted_date), '[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01].[f001]Z')"/>
  </xsl:template>
  <xsl:template match="nk4um:authorId">
    <xsl:value-of select="$lastPost//author_id"/>
  </xsl:template>
  
  <xsl:template match="@*[contains(., '${nk4um:id}')]">
    <xsl:attribute name="{name()}">
      <xsl:if test="$lastPost//id">
        <xsl:value-of select="replace(., '\$\{nk4um:id\}', $lastPost//id)"/>
      </xsl:if>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:function name="nk4um:clean-date">
    <xsl:param name="dateInput"/>
    
    <xsl:value-of select="replace($dateInput, ' ', 'T')"/>
  </xsl:function>
</xsl:stylesheet>
