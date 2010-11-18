<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="breadcrumbs"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:forumGroupTitle">
    <xsl:value-of select="$breadcrumbs//forum_group_title"/>
  </xsl:template>
  <xsl:template match="nk4um:forumTitle">
    <xsl:value-of select="$breadcrumbs//forum_title"/>
  </xsl:template>
  
  <xsl:template match="@*[contains(., '${nk4um:forumId}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:forumId\}', $breadcrumbs//forum_id)"/>
    </xsl:attribute>
  </xsl:template>
</xsl:stylesheet>
