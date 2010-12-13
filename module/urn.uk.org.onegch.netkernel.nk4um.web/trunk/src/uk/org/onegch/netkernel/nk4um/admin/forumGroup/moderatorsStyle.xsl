<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                exclude-result-prefixes="xs" version="2.0">
  
  <xsl:template match="/">
    <div>
      <table>
        <tr>
          <th>Display Name</th>
          <th>Email Address</th>
        </tr>
        <xsl:apply-templates select="//row"/>
        <form action="moderator/doAdd" method="post">
          <tr>
            <td>
              
            </td>
            <td>
              <input type="text" name="email" />
            </td>
            <td>
              <input type="submit" value="Add"/>
            </td>
          </tr>
        </form>
      </table>
        
    </div>
  </xsl:template>
  
  <xsl:template match="row">
    <tr>
      <td>
        <xsl:value-of select="display_name"/>
      </td>
      <td>
        <xsl:value-of select="email"/>
      </td>
    </tr>
  </xsl:template>
  
</xsl:stylesheet>
