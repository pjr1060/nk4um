<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" 
                version="2.0">
  <xsl:param name="forumGroupMeta"/>
  
  <xsl:template match="/">
    <tr>
      <td><xsl:value-of select="//title"/></td>
      <td><xsl:value-of select="//description"/></td>
      <td><xsl:value-of select="$forumGroupMeta//forum_count"/></td>
      <td><xsl:value-of select="$forumGroupMeta//topic_count"/></td>
      <td><xsl:value-of select="$forumGroupMeta//post_count"/></td>
      <td><xsl:value-of select="$forumGroupMeta//view_count"/></td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
