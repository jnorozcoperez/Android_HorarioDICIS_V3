<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html>
	<body>
		<h1 style="text-align:center; font-family: arial;">DIVISIÓN DE INGENIERÍA CAMPUS IRAPUATO-SALAMANCA <xsl:value-of select="list/Carrera"/> SEDE: SALAMANCA</h1>
		<table border='0' style='width:100%; font-family: arial; border-collapse: collapse;' class='tableX'>
			<tr style='color: #000000;'>
				<th style='background-color: #FFFF00; padding: 8px; border: 1px solid #000000; text-align:left; min-width: 60px;'>Clave</th>
				<th style='background-color: #FFFF00; padding: 8px; border: 1px solid #000000; text-align:left; min-width: 120px;'>Unidad De Aprendizaje</th>
				<th style='background-color: #FFFF00; padding: 8px; border: 1px solid #000000; text-align:left; min-width: 60px;'>Hrs / Sem</th>
				<th style='background-color: #FFFF00; padding: 8px; border: 1px solid #000000; text-align:left; min-width: 40px;'>Grupo</th>
				<th style='background-color: #00FF00; padding: 8px; border: 1px solid #000000; text-align:center; min-width: 60px;'>Lun</th>
				<th style='background-color: #00FF00; padding: 8px; border: 1px solid #000000; text-align:center; min-width: 60px;'>Mar</th>
				<th style='background-color: #00FF00; padding: 8px; border: 1px solid #000000; text-align:center; min-width: 60px;'>Mie</th>
				<th style='background-color: #00FF00; padding: 8px; border: 1px solid #000000; text-align:center; min-width: 60px;'>Jue</th>
				<th style='background-color: #00FF00; padding: 8px; border: 1px solid #000000; text-align:center; min-width: 60px;'>Vie</th>
				<th style='background-color: #00FF00; padding: 8px; border: 1px solid #000000; text-align:center; min-width: 60px;'>Sab</th>
				<th style='background-color: #00FF00; padding: 8px; border: 1px solid #000000; text-align:center; min-width: 60px;'>Aula</th>
				<th style='background-color: #00FF00; padding: 8px; border: 1px solid #000000; text-align:left; min-width: 120px;'>Profesor</th>
			</tr>
		<xsl:for-each select="list/item">
		<tr>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: left; padding: 8px;'><xsl:value-of select="Clave"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: left; padding: 8px;'><xsl:value-of select="UnidadDeAprendizaje"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: left; padding: 8px;'><xsl:value-of select="HorasSem"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: left; padding: 8px;'><xsl:value-of select="Grupo"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: center; padding: 8px;'><xsl:value-of select="Lun"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: center; padding: 8px;'><xsl:value-of select="Mar"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: center; padding: 8px;'><xsl:value-of select="Mie"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: center; padding: 8px;'><xsl:value-of select="Jue"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: center; padding: 8px;'><xsl:value-of select="Vie"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: center; padding: 8px;'><xsl:value-of select="Sab"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: center; padding: 8px;'><xsl:value-of select="Aula"/></td>
			<td style='background-color: #F5F5F5; border: 1px solid #000000; text-align: left; padding: 8px;'><xsl:value-of select="Profesor"/></td>
		</tr>
		</xsl:for-each>
	  </table>
	</body>
</html>
</xsl:template>
</xsl:stylesheet>