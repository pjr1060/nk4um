<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="forumGroups"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:forumGroup">
    <xsl:apply-templates select="$forumGroups//row" mode="forumGroupList">
      <xsl:with-param name="forumGroupTemplate" select="."/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="row" mode="forumGroupList">
    <xsl:param name="forumGroupTemplate"/>
    <xsl:apply-templates select="$forumGroupTemplate/*" mode="forumGroup">
      <xsl:with-param name="currentForumGroup" select="."/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="@* | node()" mode="forumGroup">
    <xsl:param name="currentForumGroup"/>
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current">
        <xsl:with-param name="currentForumGroup" select="$currentForumGroup"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:id" mode="forumGroup">
    <xsl:param name="currentForumGroup" as="node()"/>
    <xsl:value-of select="$currentForumGroup/id"/>
  </xsl:template>
</xsl:stylesheet>
