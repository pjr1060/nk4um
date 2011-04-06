<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:param name="externalModel" as="xs:boolean"/>

  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="fieldset[@userModel='builtin']">
    <xsl:if test="$externalModel = false()">
      <xsl:copy>
        <xsl:apply-templates select="@* except @userModel | node()"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>

  <xsl:template match="fieldset[@userModel='external']">
    <xsl:if test="$externalModel = true()">
      <xsl:copy>
        <xsl:apply-templates select="@* except @userModel | node()"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>