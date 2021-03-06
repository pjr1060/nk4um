<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (C) 2010-2011 by Chris Cormack
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy
  ~ of this software and associated documentation files (the "Software"), to deal
  ~ in the Software without restriction, including without limitation the rights
  ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  ~ copies of the Software, and to permit persons to whom the Software is
  ~ furnished to do so, subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in
  ~ all copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  ~ THE SOFTWARE.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                exclude-result-prefixes="xs" version="2.0">
  <xsl:param name="default-site-salt"/>
  
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
            <td class="vis-title" colspan="2">Options</td>
          </tr>
          <tr>
            <td colspan="2" style="text-align: center;">
              <input type="checkbox" name="new_user_moderated">
                <xsl:if test="//new_user_moderated">
                    <xsl:attribute name="checked" select="'checked'"/>
                  </xsl:if>
              </input><label for="new_user_moderated">New user posts moderated</label>
            </td>
          </tr>
          <tr>
            <th>Base HTTP URL:</th>
            <td><input name="base_url" type="text" value="{//base_url}"/></td>
          </tr>
          <tr>
            <th>Site Password Salt:</th>
            <td><input name="site_password_salt" type="text" value="{if (//site_password_salt) then //site_password_salt else $default-site-salt}"/></td>
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

          <tr class="vis-title">
            <td class="vis-title" colspan="2">Email Test Mode</td>
          </tr>
          <tr>
            <td colspan="2" style="text-align: center;">
              <input type="checkbox" name="developer_email_mode">
                <xsl:if test="//developer_email_mode">
                    <xsl:attribute name="checked" select="'checked'"/>
                  </xsl:if>
              </input><label for="developer_email_mode">Don't send emails to users' email address</label>
            </td>
          </tr>
          <tr>
            <th>Email Override Address:</th>
            <td><input name="developer_email" type="text" value="{//developer_email}"/></td>
          </tr>
        </table>
        
        <input type="submit" value="Update Configuration"/>
      </form>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
