<xsl:stylesheet version='1.0' xmlns="http://www.w3.org/2002/xforms"
                xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:h="http://www.w3.org/1999/xhtml"
        >
    <xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>
    <xsl:template match="/">
        <xsl:apply-templates select="@*|node()"/>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/xf:xforms">
        <h:html xmlns="http://www.w3.org/2002/xforms"
                xmlns:h="http://www.w3.org/1999/xhtml"
                >
            <xsl:apply-templates select="@*|node()"/>
        </h:html>
    </xsl:template>

    <xsl:template match="xf:model">
        <h:head>
            <model>
                <xsl:apply-templates select="@*|node()"/>
            </model>
        </h:head>
    </xsl:template>

    <xsl:template match="xf:instance">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xf:xforms/xf:group">
        <h:body>
            <xsl:apply-templates select="@*|node()"/>
        </h:body>
    </xsl:template>
    <!--
      <xsl:template match="*">
        <xsl:element name="{local-name()}">
          <xsl:apply-templates select="@*|node()" />
        </xsl:element>
      </xsl:template> -->

    <xsl:template match="@multiple">
       <xsl:attribute name="template">
          <xsl:value-of select="."/>
       </xsl:attribute>
    </xsl:template>
</xsl:stylesheet>
