<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                exclude-result-prefixes="xs" version="2.0">
  
  <xsl:template match="/">
    <div>
      <xrl:include>
        <xrl:identifier>nk4um:admin:buttonBar</xrl:identifier>
      </xrl:include>
      <table class="data-table">
        <tr class="vis-title">
          <td colspan="2">Account Details</td>
        </tr>
        <tr>
          <th>id:</th>
          <td><xsl:value-of select="/resultset/row/id"/></td>
        </tr>
        <tr>
          <th>Email Address:</th>
          <td><a href="mailto:{/resultset/row/email}"><xsl:value-of select="/resultset/row/email"/></a></td>
        </tr>
        <tr class="vis-title">
          <td colspan="2">Forum Options</td>
        </tr>
        <tr>
          <th>Display Name:</th>
          <td>
            <xsl:choose>
            <xsl:when test="//display_name/text()">
              <xsl:value-of select="/resultset/row/display_name"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="/resultset/row/email"/>
            </xsl:otherwise>
          </xsl:choose>
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
