<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="topic"/>
  <xsl:param name="topicMeta"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:id">
    <xsl:value-of select="$topic//id"/>
  </xsl:template>
  <xsl:template match="nk4um:title">
    <xsl:value-of select="$topic//title"/>
  </xsl:template>
  <xsl:template match="nk4um:description">
    <xsl:value-of select="$topic//description"/>
  </xsl:template>
  
  <xsl:template match="nk4um:postCount">
    <xsl:value-of select="$topicMeta//post_count"/>
  </xsl:template>
  
  <xsl:template match="@*[contains(., '${nk4um:id}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:id\}', $topic//id)"/>
    </xsl:attribute>
  </xsl:template>
</xsl:stylesheet>
