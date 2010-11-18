<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="forumList"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:forum">
    <xsl:apply-templates select="$forumList//row" mode="forumList">
      <xsl:with-param name="forumTemplate" select="."/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="row" mode="forumList">
    <xsl:param name="forumTemplate"/>
    <xsl:apply-templates select="$forumTemplate/*" mode="forum">
      <xsl:with-param name="currentForum" select="."/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="@* | node()" mode="forum">
    <xsl:param name="currentForum"/>
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current">
        <xsl:with-param name="currentForum" select="$currentForum"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:id" mode="forum">
    <xsl:param name="currentForum" as="node()"/>
    <xsl:value-of select="$currentForum/id"/>
  </xsl:template>
</xsl:stylesheet>
