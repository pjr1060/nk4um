<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:param name="url" as="xs:string"/>
  <xsl:param name="post"/>
  <xsl:param name="postContent"/>
  <xsl:param name="user"/>
  <xsl:param name="userMeta"/>
  
  <xsl:template match="@* | node()" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="nk4um:url">
    <xsl:value-of select="$url"/>
  </xsl:template>
  <xsl:template match="@*[contains(., '${nk4um:url}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:url\}', $url)"/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="nk4um:title">
    <xsl:choose>
      <xsl:when test="$post//title/text()">
        <xsl:value-of select="$post//title"/>
      </xsl:when>
      <xsl:when test="text()">
        <xsl:value-of select="text()"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="nk4um:content">
    <xsl:copy-of select="$postContent//xhtml:html/xhtml:body/*" />
  </xsl:template>
  <xsl:template match="nk4um:postedDateTime">
    <xsl:value-of select="format-dateTime(nk4um:clean-date($post//posted_date), '[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01].[f001]Z')"/>
  </xsl:template>
  <xsl:template match="nk4um:displayName">
    <xsl:choose>
      <xsl:when test="$user//display_name/text()">
        <xsl:value-of select="$user//display_name"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$user//email"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="nk4um:id">
    <xsl:value-of select="$post//id"/>
  </xsl:template>
  <xsl:template match="@*[contains(., '${nk4um:id}')]">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="replace(., '\$\{nk4um:id\}', $post//id)"/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:function name="nk4um:clean-date">
    <xsl:param name="dateInput"/>
    
    <xsl:value-of select="replace($dateInput, ' ', 'T')"/>
  </xsl:function>
</xsl:stylesheet>
