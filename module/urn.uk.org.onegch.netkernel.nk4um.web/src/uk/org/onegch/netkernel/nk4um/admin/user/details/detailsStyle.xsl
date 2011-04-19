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
