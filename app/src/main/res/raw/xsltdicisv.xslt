<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html>
	<style>
	.tableX tr:nth-child(even){
		background-color: #FFF4CD;
	}
	</style>
	<body>
		<table border='0' style='width:100%; font-family: arial; border-collapse: collapse;' class='tableX'>
		<tr style='color: #FFFFFF; background-color: #FFD200;'>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 50px;'>Clave</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 80px;'>Area de la UDA</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 120px;'>Unidad De Aprendizaje</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 40px;'>Horas / Sem</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 100px;'>Requisitos</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 40px;'>Grupo</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 90px;'>Lun</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 90px;'>Mar</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 90px;'>Mie</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 90px;'>Jue</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 90px;'>Vie</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 90px;'>Sab</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 50px;'>Aula</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 120px;'>Profesor</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 120px;'>Presidente</th>
			<th style='padding: 8px; border: 1px solid #FFCA08; text-align:left; min-width: 120px;'>Vocal</th>
		</tr>
		<xsl:for-each select="list/item">
		<tr>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Clave"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="AreaDeLaUda"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="UnidadDeAprendizaje"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="HorasSem"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Requisitos"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Grupo"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Lun"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Mar"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Mie"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Jue"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Vie"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Sab"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Aula"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Profesor"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Presidente"/></td>
			<td style='border: 1px solid #FFCA08; text-align: left; padding: 8px;'><xsl:value-of select="Vocal"/></td>
		</tr>
		</xsl:for-each>
	  </table>
	</body>
</html>
</xsl:template>
</xsl:stylesheet>