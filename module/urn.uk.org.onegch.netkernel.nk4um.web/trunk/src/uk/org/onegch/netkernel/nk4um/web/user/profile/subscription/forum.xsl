<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="forum"/>
  <xsl:param name="subscribed" as="xs:boolean"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:id">
    <xsl:value-of select="$forum//id"/>
  </xsl:template>
  <xsl:template match="nk4um:title">
    <xsl:value-of select="$forum//title"/>
  </xsl:template>
  <xsl:template match="nk4um:description">
    <xsl:value-of select="$forum//description"/>
  </xsl:template>
  
  <xsl:template match="@*[contains(., '${nk4um:id}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:id\}', $forum//id)"/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="input[@type='checkbox']">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:if test="$subscribed">
        <xsl:attribute name="checked" select="'checked'"/>
      </xsl:if>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
