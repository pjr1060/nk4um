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

          <tr class="vis-title">
            <td class="vis-title" colspan="2">Security Settings</td>
          </tr>
          <tr>
            <td colspan="2" style="text-align: center;">
              <input type="checkbox" name="security_external">
                <xsl:if test="//security_external">
                    <xsl:attribute name="checked" select="'checked'"/>
                  </xsl:if>
              </input><label for="security_external">Use external security model</label>
            </td>
          </tr>
          <tr>
            <th>User table:</th>
            <td><input name="security_userTable" type="text" value="{//security_userTable}"/></td>
          </tr>
          <tr>
            <th>User ID column:</th>
            <td><input name="security_userTableId" type="text" value="{//security_userTableId}"/></td>
          </tr>
          <tr>
            <th>GateKeeper module URI:</th>
            <td><input name="security_gatekeeperModule" type="text" value="{//security_gatekeeperModule}"/></td>
          </tr>
          <tr>
            <th>Current user URI:</th>
            <td><input name="security_userIdResource" type="text" value="{//security_userIdResource}"/></td>
          </tr>
          <tr>
            <th>Login URL:</th>
            <td><input name="security_loginUrl" type="text" value="{//security_loginUrl}"/></td>
          </tr>
          <tr>
            <th>Logout URL:</th>
            <td><input name="security_logoutUrl" type="text" value="{//security_logoutUrl}"/></td>
          </tr>
          <tr>
            <th>Register URL:</th>
            <td><input name="security_registerUrl" type="text" value="{//security_registerUrl}"/></td>
          </tr>
          <tr>
            <th>Lost Password URL:</th>
            <td><input name="security_lostPasswordUrl" type="text" value="{//security_lostPasswordUrl}"/></td>
          </tr>
          <tr>
            <th>Change Password URL:</th>
            <td><input name="security_changePasswordUrl" type="text" value="{//security_changePasswordUrl}"/></td>
          </tr>
          <tr>
            <th>Edit Account URL:</th>
            <td><input name="security_editAccountUrl" type="text" value="{//security_editAccountUrl}"/></td>
          </tr>
        </table>
        
        <input type="submit" value="Update Configuration"/>
      </form>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
