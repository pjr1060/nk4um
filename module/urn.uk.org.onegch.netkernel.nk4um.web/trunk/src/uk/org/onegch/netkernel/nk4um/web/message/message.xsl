<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="title"/>
  <xsl:param name="content"/>
  <xsl:param name="class"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:title">
    <xsl:value-of select="$title"/>
  </xsl:template>
  <xsl:template match="nk4um:content">
    <xsl:copy-of select="$content"/>
  </xsl:template>
  
  <xsl:template match="@*[contains(., '${nk4um:class}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:class\}', $class)"/>
    </xsl:attribute>
  </xsl:template>
</xsl:stylesheet>
