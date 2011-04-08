<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="postList"/>
  <xsl:param name="displayModeration" as="xs:boolean"/>
  <xsl:param name="moderator" as="xs:boolean"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:post">
    <xsl:apply-templates select="$postList//row" mode="postList">
      <xsl:with-param name="postTemplate" select="."/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="row" mode="postList">
    <xsl:param name="postTemplate"/>

    <xsl:if test="xs:boolean(visible/text()) or $moderator">
      <xsl:apply-templates select="$postTemplate/*" mode="post">
        <xsl:with-param name="currentPost" select="."/>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="@* | node()" mode="post">
    <xsl:param name="currentPost"/>
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current">
        <xsl:with-param name="currentPost" select="$currentPost"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:id" mode="post">
    <xsl:param name="currentPost" as="node()"/>
    <xsl:value-of select="$currentPost/id"/>
  </xsl:template>

  <xsl:template match="nk4um:displayModeration" mode="post">
    <xsl:value-of select="$displayModeration"/>
  </xsl:template>
</xsl:stylesheet>
