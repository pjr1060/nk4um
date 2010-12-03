<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:template match="/">
    <div style="clear: both; margin-bottom: 4em;">
      <h3><xsl:apply-templates select="//title"/></h3>
      <p><xsl:apply-templates select="//description"/></p>
      <div style="clear: both">
        <span style="padding-right: 5px; padding-left: 5px;"> 
          <a class="btn2" onclick="parent.location='/nk4um/forumGroup/{//id}/edit'"><i>&#160;</i><span><i>&#160;</i><span>&#160;</span>Edit Forum Group</span></a> 
        </span>
      </div>
      <div style="clear: both">
        <xrl:include>
          <xrl:identifier>nk4um:admin:forums:forumList</xrl:identifier>
          <xrl:argument name="id"><literal type="string"><xsl:value-of select="//id"/></literal></xrl:argument>
        </xrl:include>
      </div>
      <div style="clear: both">
        <span style="padding-right: 5px; padding-left: 5px;"> 
          <a class="btn2" onclick="parent.location='/nk4um/forumGroup/{//id}/forum/new'"><i>&#160;</i><span><i>&#160;</i><span>&#160;</span>Add Forum</span></a> 
        </span>
      </div>
    </div>
  </xsl:template>
</xsl:stylesheet>
