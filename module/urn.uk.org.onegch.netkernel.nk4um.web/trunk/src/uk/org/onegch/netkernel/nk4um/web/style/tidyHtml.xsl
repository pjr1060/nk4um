<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                xmlns:saxon="http://saxon.sf.net/"
                version="2.0"
                exclude-result-prefixes="xrl nk4um saxon">
  <xsl:strip-space elements="*"/>
  <xsl:output exclude-result-prefixes="xrl nk4um saxon" />
  <xsl:template match="@* | node()">
    <xsl:copy copy-namespaces="no">
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="title">
    <xsl:copy copy-namespaces="no">
      <xsl:value-of select="normalize-space()"/>
    </xsl:copy>
  </xsl:template>
  
  <!-- strip XRL attributes -->
  <xsl:template match="@xrl:*"/>
  
  <!-- strip XRL elements -->
  <xsl:template match="xrl:*"/>
  
  <!-- strip nk4um attributes -->
  <xsl:template match="@nk4um:*"/>
  
  <!-- strip nk4um elements -->
  <xsl:template match="nk4um:*"/>
  
</xsl:stylesheet>
