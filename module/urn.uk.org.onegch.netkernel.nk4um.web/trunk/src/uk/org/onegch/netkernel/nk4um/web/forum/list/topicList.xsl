<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="topicList"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:topic">
    <xsl:apply-templates select="$topicList//row" mode="topicList">
      <xsl:with-param name="topicTemplate" select="."/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="row" mode="topicList">
    <xsl:param name="topicTemplate"/>
    <xsl:apply-templates select="$topicTemplate/*" mode="topic">
      <xsl:with-param name="currentTopic" select="."/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="@* | node()" mode="topic">
    <xsl:param name="currentTopic"/>
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current">
        <xsl:with-param name="currentTopic" select="$currentTopic"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:id" mode="topic">
    <xsl:param name="currentTopic" as="node()"/>
    <xsl:value-of select="$currentTopic/id"/>
  </xsl:template>
</xsl:stylesheet>
