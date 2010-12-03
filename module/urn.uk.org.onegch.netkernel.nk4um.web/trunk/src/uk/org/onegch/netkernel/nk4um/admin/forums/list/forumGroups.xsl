<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                exclude-result-prefixes="xs"
                version="2.0">
  <xsl:template match="/">
    <div>
      <xrl:include>
        <xrl:identifier>nk4um:admin:buttonBar</xrl:identifier>
      </xrl:include>
      <br/>
      <div>
        <span style="padding-right: 5px; padding-left: 5px;"> 
          <a class="btn2" onclick="parent.location='/nk4um/forumGroup/new'"><i>&#160;</i><span><i>&#160;</i><span>&#160;</span>Add Forum Group</span></a> 
        </span>
      </div>
      
      <br style="clear: both;"/>
      <xsl:apply-templates select="//row"/>
    </div>
  </xsl:template>
  
  <xsl:template match="row">
    <xrl:include>
      <xrl:identifier>nk4um:admin:forums:forumGroup</xrl:identifier>
      <xrl:argument name="id"><literal type="string"><xsl:value-of select="id"/></literal></xrl:argument>
    </xrl:include>
  </xsl:template>
</xsl:stylesheet>
