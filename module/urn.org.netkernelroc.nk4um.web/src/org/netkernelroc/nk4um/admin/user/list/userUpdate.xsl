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
                version="2.0">
  
  <xsl:template match="/">
    <form action="/nk4um/user/{//id}/update" method="POST" style="display: inline-block; white-space: nowrap;">
      <select name="status">
        <option value="active">
          <xsl:if test="//status='active'">
            <xsl:attribute name="selected" select="'selected'"/>
          </xsl:if>
          Active
        </option>
        <option value="suspended">
          <xsl:if test="//status='suspended'">
            <xsl:attribute name="selected" select="'selected'"/>
          </xsl:if>
          Suspended
        </option>
        <option value="deleted">
          <xsl:if test="//status='deleted'">
            <xsl:attribute name="selected" select="'selected'"/>
          </xsl:if>
          Deleted
        </option>
      </select>
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
  </xsl:template>
</xsl:stylesheet>
