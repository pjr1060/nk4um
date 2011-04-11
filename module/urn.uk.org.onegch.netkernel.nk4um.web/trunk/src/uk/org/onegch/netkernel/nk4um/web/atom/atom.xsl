<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="title" as="xs:string"/>
  <xsl:param name="url" as="xs:string"/>
  <xsl:param name="lastPostTime" select="xs:string(current-dateTime())"/>
  <xsl:param name="postList"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="nk4um:title">
    <xsl:value-of select="$title"/>
  </xsl:template>

  <xsl:template match="nk4um:url">
    <xsl:value-of select="$url"/>
  </xsl:template>

  <xsl:template match="nk4um:lastPostTime">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($lastPostTime), '[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01].[f001]Z')"/>
  </xsl:template>

  <xsl:template match="nk4um:post">
    <xsl:variable name="post" select="*"/>
    <xsl:for-each select="$postList//row[xs:boolean(visible/text())=true()]">
      <xsl:apply-templates select="$post" mode="post">
        <xsl:with-param name="id" select="id"/>
      </xsl:apply-templates>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="@* | node()" mode="post">
    <xsl:param name="id"/>
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current">
        <xsl:with-param name="id" select="$id"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="nk4um:postId" mode="post">
    <xsl:param name="id"/>
    <xsl:value-of select="$id"/>
  </xsl:template>
  
  <xsl:function name="nk4um:clean-date">
    <xsl:param name="dateInput"/>
    
    <xsl:value-of select="replace($dateInput, ' ', 'T')"/>
  </xsl:function>
</xsl:stylesheet>
