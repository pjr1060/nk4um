<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="forum"/>
  <xsl:param name="moderator" as="xs:boolean"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:id">
    <xsl:value-of select="$forum//id"/>
  </xsl:template>
  
  <xsl:template match="nk4um:isModerator">
    <xsl:value-of select="$moderator"/>
  </xsl:template>
  
  <xsl:template match="nk4um:moderator">
    <xsl:if test="$moderator">
      <xsl:apply-templates select="node()"/>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
