<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                version="2.0">
  <xsl:template match="/">
    <table class="data-table statsTable">
      <thead>
        <tr>
          <th>Forum Group</th>
          <th>Title</th>
          <th>Description</th>
          <th>Topic Count</th>
          <th>Post Count</th>
          <th>View Count</th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="//row"/>
      </tbody>
    </table>
  </xsl:template>
  
  <xsl:template match="row">
    <xrl:include>
      <xrl:identifier>active:java</xrl:identifier>
      <xrl:argument name="class">uk.org.onegch.netkernel.nk4um.admin.statistics.forums.ForumAccessor</xrl:argument>
      <xrl:argument name="id"><literal type="string"><xsl:value-of select="id"/></literal></xrl:argument>
    </xrl:include>
  </xsl:template>
</xsl:stylesheet>
