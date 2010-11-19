<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                version="2.0">
  <xsl:param name="params"/>
  
  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="*[@name]">
    <xsl:variable name="name" select="@name"/>
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:attribute name="value" select="$params//*[local-name()=$name]"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
