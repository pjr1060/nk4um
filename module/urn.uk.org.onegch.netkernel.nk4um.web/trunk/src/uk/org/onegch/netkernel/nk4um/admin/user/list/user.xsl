<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" 
                version="2.0">
  <xsl:param name="userMeta"/>
  
  <xsl:template match="/">
    <tr>
      <td>
        <a href="/nk4um/user/{//id}/">
          <xsl:choose>
            <xsl:when test="//display_name/text()">
              <xsl:value-of select="//display_name"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="//email"/>
            </xsl:otherwise>
          </xsl:choose>
        </a>
      </td>
      <td>
        <a href="mailto:{//email}"><xsl:value-of select="//email"/></a></td>
      <td><xsl:value-of select="$userMeta//post_count"/></td>
      <td><xsl:value-of select="//activated"/></td>
      <td>
        <xsl:choose>
          <xsl:when test="//role_name/text()">
            <xsl:value-of select="//role_name"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>User</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <form action="/nk4um/user/{//id}/updateRole" method="POST" style="display: inline-block">
          <select name="role">
            <option value="User">
              <xsl:if test="//role_name='User'">
                <xsl:attribute name="selected" select="'selected'"/>
              </xsl:if>
              User
            </option>
            <option value="Administrator">
              <xsl:if test="//role_name='Administrator'">
                <xsl:attribute name="selected" select="'selected'"/>
              </xsl:if>
              Administrator
            </option>
          </select>
          <button type="submit">Update</button>
        </form>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
