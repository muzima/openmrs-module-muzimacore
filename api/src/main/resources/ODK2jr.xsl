<xsl:stylesheet version='1.0' xmlns="http://www.w3.org/2002/xforms"
                xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:h="http://www.w3.org/1999/xhtml"
        >
    <xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="*">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates select="node()|@*"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="@*">
        <xsl:attribute name="{local-name()}">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="/xforms | /xf:xforms">
        <h:html xmlns="http://www.w3.org/2002/xforms"
                xmlns:h="http://www.w3.org/1999/xhtml"
                >
            <xsl:apply-templates/>
        </h:html>
    </xsl:template>

    <xsl:template match="head | xf:head">
        <h:head>
            <xsl:apply-templates/>
        </h:head>
    </xsl:template>

    <xsl:template match="title | xf:title">
        <h:title>
            <xsl:apply-templates/>
        </h:title>
    </xsl:template>

    <xsl:template match="model | xf:model">
        <model>
            <xsl:apply-templates/>
        </model>
    </xsl:template>

    <xsl:template match="instance | xf:instance">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="form">
        <form>
            <xsl:apply-templates/>
        </form>
    </xsl:template>

    <xsl:template match="hint"></xsl:template>

    <xsl:template match="body | xf:body">
        <h:body>
            <xsl:apply-templates/>
        </h:body>
    </xsl:template>

    <xsl:template match="@multiple">
        <xsl:attribute name="template">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

</xsl:stylesheet>
