<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                exclude-result-prefixes="xs" version="2.0">
  
  <xsl:template match="/">
    <div>
      <form action="doEdit" method="POST">
        <table class="form-table">
          <tr class="vis-title">
            <td class="vis-title" colspan="2">Database Connection</td>
          </tr>
          <tr>
            <th>JDBC Driver:</th>
            <td><input name="jdbcDriver" type="text" value="{//jdbcDriver}"/></td>
          </tr>
          <tr>
            <th>JDBC Connection:</th>
            <td><input name="jdbcConnection" type="text" value="{//jdbcConnection}"/></td>
          </tr>
          <tr>
            <th>JDBC User:</th>
            <td><input name="jdbcUser" type="text" value="{//jdbcUser}"/></td>
          </tr>
          <tr>
            <th>JDBC Password:</th>
            <td><input name="jdbcPassword" type="text" value="{//jdbcPassword}"/></td>
          </tr>
          
          <tr class="vis-title">
            <td class="vis-title" colspan="2">SMTP Connection</td>
          </tr>
          <tr>
            <th>SMTP Gateway:</th>
            <td><input name="smtpGateway" type="text" value="{//smtpGateway}"/></td>
          </tr>
          <tr>
            <th>SMTP Port:</th>
            <td><input name="smtpPort" type="text" value="{//smtpPort}"/></td>
          </tr>
          <tr>
            <th>SMTP User:</th>
            <td><input name="smtpUser" type="text" value="{//smtpUser}"/></td>
          </tr>
          <tr>
            <th>SMTP Password:</th>
            <td><input name="smtpPassword" type="text" value="{//smtpPassword}"/></td>
          </tr>
          <tr>
            <th>SMTP Security:</th>
            <td>
              <select name="smtpSecurity">
                <option value="none">
                  <xsl:if test="//smtpSecurity='none'">
                    <xsl:attribute name="selected" select="'selected'"/>
                  </xsl:if>
                  None
                </option>
                <option value="ssl">
                  <xsl:if test="//smtpSecurity='ssl'">
                    <xsl:attribute name="selected" select="'selected'"/>
                  </xsl:if>
                  SSL
                </option>
                <option value="tls">
                  <xsl:if test="//smtpSecurity='tls'">
                    <xsl:attribute name="selected" select="'selected'"/>
                  </xsl:if>
                  TLS
                </option>
              </select>
            </td>
          </tr>
          <tr>
            <th>From Address:</th>
            <td><input name="smtpSender" type="text" value="{//smtpSender}"/></td>
          </tr>
        </table>
        
        <input type="submit" value="Update Configuration"/>
      </form>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
