<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xrl="http://netkernel.org/xrl"
                exclude-result-prefixes="xs" version="2.0">
  
  <xsl:template match="/">
    <div>
      <form action="doEdit" method="POST">
        <table>
          <tr>
            <th>Order:</th>
            <td><input name="order" type="text" value="{/resultset/row/display_order}"/></td>
          </tr>
          <tr>
            <th>Title:</th>
            <td><input name="title" type="text" value="{/resultset/row/title}"/></td>
          </tr>
          <tr>
            <th style="vertical-align: top">Description:</th>
            <td><textarea name="description" cols="50" rows="4">
<xsl:value-of select="/resultset/row/description"/></textarea></td>
          </tr>
        </table>
        
        <input type="submit" value="Update Forum"/>
      </form>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
