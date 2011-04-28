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
      <table class="data-table">
        <tr class="vis-title">
          <td class="vis-title" colspan="2">Database to Import Statistics</td>
        </tr>
        <tr>
          <td class="label">User Count:</td>
          <td><xsl:value-of select="//user_count"/></td>
        </tr>
        <tr>
          <td class="label">Forum Group Count:</td>
          <td><xsl:value-of select="//forum_group_count"/></td>
        </tr>
        <tr>
          <td class="label">Forum Count:</td>
          <td><xsl:value-of select="//forum_count"/></td>
        </tr>
        <tr>
          <td class="label">Topic Count:</td>
          <td><xsl:value-of select="//topic_count"/></td>
        </tr>
        <tr>
          <td class="label">Post Count:</td>
          <td><xsl:value-of select="//post_count"/></td>
        </tr>
        <tr>
          <td class="label">View Count:</td>
          <td><xsl:value-of select="//hit_count"/></td>
        </tr>
        <tr>
          <td class="label">Subscriber Count:</td>
          <td><xsl:value-of select="//subscriber_count"/></td>
        </tr>
        <tr>
          <td class="label">Moderator Count:</td>
          <td><xsl:value-of select="//moderator_count"/></td>
        </tr>
      </table>
      <form action="doImport" method="POST">
        <input type="submit" value="Run Import"/>
      </form>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
