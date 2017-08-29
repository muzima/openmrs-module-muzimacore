<xsl:stylesheet version='1.0' xmlns="http://www.w3.org/2002/xforms" xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
>
    <xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>
      <xsl:template match="*">
        <xsl:element name="{local-name()}">
          <xsl:apply-templates select="@*|node()" />
        </xsl:element>
      </xsl:template>
</xsl:stylesheet>
