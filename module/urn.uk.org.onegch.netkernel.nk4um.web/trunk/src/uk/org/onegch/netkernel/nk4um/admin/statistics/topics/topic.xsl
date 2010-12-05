<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                xmlns:nk4um="http://onegch.org.uk/netkernel/nk4um"
                version="2.0">
  <xsl:param name="topicMeta"/>
  
  <xsl:template match="/">
    <tr>
      <td><xsl:value-of select="//forum_group"/></td>
      <td><xsl:value-of select="//forum"/></td>
      <td><xsl:value-of select="//title"/></td>
      <td>
        <xrl:eval>
          <xrl:xpath>..</xrl:xpath>
          <xrl:identifier>nk4um:web:author:displayName</xrl:identifier>
          <xrl:argument name="id"><literal type="string"><xsl:value-of select="//author_id"/></literal></xrl:argument>
          <xrl:representation>java.lang.String</xrl:representation>
        </xrl:eval>
      </td>
      <td><xsl:value-of select="format-dateTime(nk4um:clean-date(//posted_date), '[D01]/[M01]/[Y] [H01]:[m01]')"/></td>
      <td><xsl:value-of select="$topicMeta//post_count"/></td>
      <td><xsl:value-of select="$topicMeta//view_count"/></td>
    </tr>
  </xsl:template>
  
  <xsl:function name="nk4um:clean-date">
    <xsl:param name="dateInput"/>
    
    <xsl:value-of select="replace($dateInput, ' ', 'T')"/>
  </xsl:function>
</xsl:stylesheet>
