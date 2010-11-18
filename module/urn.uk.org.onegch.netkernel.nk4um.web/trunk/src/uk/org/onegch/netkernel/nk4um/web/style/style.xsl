<?xml version="1.0" encoding="UTF-8"?>
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