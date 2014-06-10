package co.s4n.desprendibles.trabajador

import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import java.util.GregorianCalendar
import java.text.DecimalFormat

class CrearPdf (trabajadorPar : Trabajador) {
  
  var trabajador : Trabajador = trabajadorPar
  val document = new Document
  var decimalF = new DecimalFormat("$##,###,###")
  
  def creapDesprendible(path : String) {
    PdfWriter.getInstance(document,new FileOutputStream(path + trabajador.nombre + ".pdf"));
    document.open();
    document.add(encabezadoPdf());
    document.add(informacionTrabajador());
    document.add(new Paragraph("\n"))
    document.add(contenido());
    document.add(subTotal)
    document.add(new Paragraph("\n\n\n ______________________\nPaula Rodríguez\nLíder de Gestión Humana\nSeven4n"))
    document.close();
  }
  
  private def encabezadoPdf() : PdfPTable = {
    var fecha = new Date()
    val fechaFormateada = mesPagado(fecha)
	
    val table = new PdfPTable(3); // 3 columns.
    table.setWidths(Array(100,200,20));
    val boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
    val image = Image.getInstance("seven4n.png");
    val titulo = new Paragraph("COMPROBANTE DE NOMINA No. " + (fecha.getYear()+1900) + "-" + (fecha.getMonth()+1) + fechaFormateada.toUpperCase(), boldFont);
    val cell1 = new PdfPCell();
    val cell2 = new PdfPCell(titulo);
    val cell3 = new PdfPCell();
    cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell1.setImage(image);
    cell1.setBorderWidth(0);
    cell2.setBorderWidth(0);
    table.addCell(cell1);
    table.addCell(cell2);

    cell3.setBorderWidth(0);
    table.addCell(cell3);
    return table;
  }
  
  private def informacionTrabajador() : PdfPTable = {
    var table = new PdfPTable(4); 
    table.setWidths(Array(30,90,60,60));
    val boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
    val font = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
    var fecha = new Date()
    fecha.setMonth(fecha.getMonth()-1)
    var cal = new GregorianCalendar(fecha.getYear()+1900, fecha.getMonth(), 1); 
	var days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
	var mes = new SimpleDateFormat("MMMMM", new Locale("es","ES")).format(fecha)
	
    var strings = Array[String]("Nombre:",
		trabajador.nombre,
		"Periodo Pagado:",
		"01 al " + days + " de " + mes + " de " + cal.get(Calendar.YEAR),
		"Cédula:",
		trabajador.cedula.toString,
		"Centro de Costo:",
		" ",
		"Cargo:",
		trabajador.cargo,
		"Salario Básico:",
		decimalF.format(trabajador.salarioBase));
    val paragraphs = new Array[Paragraph](strings.size);
    for (string <- 0 until strings.size){
    	paragraphs(string) = new Paragraph(strings(string) , font); 
    }
    val cell = new PdfPCell();
    for (i <- 0 until paragraphs.size){
    	cell.setPhrase(paragraphs(i));
    	cell.setBorderWidth(0);
    	table.addCell(cell);
    }
    table
  }

  private def contenido(): PdfPTable = {
    var boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
    var font = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
    var table = new PdfPTable(4);
    table.setWidths(Array(120, 60, 60, 60))
    encabezadoContenido(boldFont, table)
    var aling: Int = Element.ALIGN_CENTER
    agregarFila(font, table, "Hora ordinaria diurna", " " + trabajador.dias, decimalF.format(trabajador.salario), " ", aling)
    agregarFila(font, table, "Auxilio extralegal de movilizacion", " " + trabajador.dias, decimalF.format(trabajador.auxilio), " ", aling)
    agregarFila(font, table, "Aporte EPS", " ", " ", decimalF.format(trabajador.eps), aling)
    agregarFila(font, table, "Aporte AFP", " ", " ", decimalF.format(trabajador.afp), aling)
    if (trabajador.auxiliotransporte > 0){
    	agregarFila(font, table, "Auxilio Transporte", " ", decimalF.format(trabajador.auxiliotransporte), " ", aling)
    }
    if (trabajador.seguroexequial > 0){
      agregarFila(font, table, "Seguro Exequial", " ", " ", decimalF.format(trabajador.seguroexequial), aling)
    }
    if (trabajador.vacaciones > 0){
      agregarFila(font, table, "Vacaciones Pagadas", " ", decimalF.format(trabajador.vacaciones), " ", aling)
    }
    if (trabajador.parqueadero > 0){
      agregarFila(font, table, "Parqueadero", " ", " ", decimalF.format(trabajador.parqueadero), aling)
    }
    if (trabajador.fondo > 0){
      agregarFila(font, table, "Fondo de Solidaridad", " ", " ", decimalF.format(trabajador.fondo), aling)
    }
    if (trabajador.retefuente > 0){
      agregarFila(font, table, "Rte Fuente", " ", " ", decimalF.format(trabajador.retefuente), aling)
    }
    if (trabajador.iCesantias > 0){
      agregarFila(font, table, "Intereses de Cesantias", " ", decimalF.format(trabajador.iCesantias), " ", aling)
    }
    if (trabajador.prima > 0){
      agregarFila(font, table, "Prima", " ", decimalF.format(trabajador.prima), " ", aling)
    }
    if (trabajador.otros > 0){
    	agregarFila(font, table, "Otros", " ", " ", decimalF.format(trabajador.otros), aling)
    }
    table
  }
  
  private def subTotal() : PdfPTable = {
    var boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
    var font = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
    var table = new PdfPTable(4);
    table.setWidths(Array(120, 60, 60, 60))
    agregarFila(font, table, " ", "SUBTOTAL", decimalF.format(trabajador.totalSalario), decimalF.format(trabajador.deducciones),Element.ALIGN_CENTER)
    agregarFila(boldFont, table, " ", "NETO A PAGAR", " ", decimalF.format(trabajador.totalAPagar),Element.ALIGN_CENTER)
    table
  }

  private def encabezadoContenido(boldFont:Font, table: PdfPTable) {
    agregarFila(boldFont, table, "CONCEPTO", "Dias Laborados", "Devengado", "Deducido",Element.ALIGN_CENTER)
  }

  private def agregarFila(font: Font, table: PdfPTable , concepto : String, dias : String, devengado : String, deducido : String, aling : Int): PdfPCell = {
    var cell = new PdfPCell();
    cell.setHorizontalAlignment(aling);
    cell.setPhrase(new Paragraph(concepto ,font))
    table.addCell(cell)
    cell.setPhrase(new Paragraph(dias ,font))
    table.addCell(cell)
    cell.setPhrase(new Paragraph(devengado ,font))
    table.addCell(cell)
    cell.setPhrase(new Paragraph(deducido , font))
    table.addCell(cell)
    cell
  }
  
  private def mesPagado(fecha: java.util.Date): String = {
    fecha.setMonth(fecha.getMonth()-1)
    var formateador = new SimpleDateFormat("'\nPAGO MES DE' MMMMM 'DE' yyyy", new Locale("es","ES"));
    var fechaFormateada = formateador.format(fecha)
    fechaFormateada
  }
}