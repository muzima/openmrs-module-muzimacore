<xsl:stylesheet version='1.0' xmlns="http://www.w3.org/2002/xforms" xmlns:xsl='http://www.w3.org/1999/XSL/Transform' xmlns:xf="http://www.w3.org/2002/xforms" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:jr="http://openrosa.org/javarosa" xmlns:orx="http://openrosa.org/xforms/" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>
      <xsl:template match="*">
        <xsl:element name="{local-name()}">
          <xsl:apply-templates select="@*|node()" />
        </xsl:element>
      </xsl:template>
</xsl:stylesheet>
