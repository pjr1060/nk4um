<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                version="2.0">
  <xsl:template match="/">
    <table id="moderationTable" class="data-table">
      <thead>
        <tr>
          <th>User</th>
          <th>Post</th>
          <th>Update</th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="//row"/>
      </tbody>
    </table>
  </xsl:template>
  
  <xsl:template match="row">
    <!--<xrl:include>-->
      <!--<xrl:identifier>active:java</xrl:identifier>-->
      <!--<xrl:argument name="class">uk.org.onegch.netkernel.nk4um.admin.user.list.UserAccessor</xrl:argument>-->
      <!--<xrl:argument name="id"><literal type="string"><xsl:value-of select="id"/></literal></xrl:argument>-->
    <!--</xrl:include>-->
  </xsl:template>
</xsl:stylesheet>
