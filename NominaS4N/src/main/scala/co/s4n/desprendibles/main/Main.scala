package co.s4n.desprendibles.main

import java.io.FileOutputStream
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import co.s4n.desprendibles.trabajador.CrearPdf
import co.s4n.desprendibles.trabajador.Trabajador
import org.xlsx4j.sml.Cell
import org.xlsx4j.sml.Row
import org.xlsx4j.sml.STCellType
import org.xlsx4j.sml.SheetData
import org.xlsx4j.sml.Worksheet
import co.s4n.desprendibles.trabajador.ProcesamientoXlsx
import co.s4n.desprendibles.trabajador.ProcesamientoXlsx
import co.s4n.smtp.EmailVO
import co.s4n.smtp.exceptions.SMTPPermanentFailureException
import co.s4n.smtp.MailService
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.io.File
import java.util.GregorianCalendar
import java.util.Calendar
import scala.util.Try

object Main {

  def main(args: Array[String]): Unit = {
    
//    println("Ingresar el nombre del archivo")
//    val periodo = readLine
    var enviar : Boolean = false
    var prueba : Boolean = false
    if(args.size > 0){
    	if(args(0).equals("enviar")){
    	  enviar = true
    	}
    	if(args(0).equals("prueba")){
    	  println("Sientro a prueba");
    	  prueba = true
    	}
    }      
    println("ingrese el nombre del archivo .xlsx")
    val ruta = readLine
    val pathArchivo = new File(ruta + ".xlsx")
    if(!pathArchivo.exists()){
      println("El Archivo no existe o el nombre es incorrecto.");;
    }else{
    	val fecha = new Date()
    	fecha.setMonth(fecha.getMonth()-1)
    	val cal = new GregorianCalendar(fecha.getYear()+1900, fecha.getMonth(), 1); 
    	val days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
    	val formateador = new SimpleDateFormat("MMMMM", new Locale("es","ES")).format(fecha)
    			val rutaDestino = formateador.format(fecha) + System.getProperty("file.separator")
    			val folder = new File(rutaDestino)
    	if(!folder.exists()){
    		folder.mkdir()
    	}
    	val archivoXls  = new ProcesamientoXlsx(ruta + ".xlsx")
    	val trabajadores = archivoXls.extraerTrabajadores match{
		  case Some(value) => procesarTrabajadores(enviar, fecha, formateador, rutaDestino, value)
				  procesarTrabajadores(enviar, fecha, formateador, rutaDestino, value)
				  if(prueba){
					  val email = new EmailVO("paularodriguez@seven4n.com", "aalzate86@gmail.com", null , "Desprendible de nómina Prueba" , "Prueba Correo" , rutaDestino + value(0).nombre + ".pdf")
					  val mailSender = new MailService()
					  mailSender.send(email)
				  }
		  case None => println("No se puede extraer informacion de trabajadores")
		}
    	  
    }
  }
  
  private def procesarTrabajadores(enviar: Boolean, fecha: java.util.Date, formateador: String, rutaDestino: String, trabajadores: Array[co.s4n.desprendibles.trabajador.Trabajador]): Unit = {
    val mailSender = new MailService();
    for(trabajador <- trabajadores){
    	val pdfCreator = new CrearPdf(trabajador);
    	pdfCreator.creapDesprendible(rutaDestino)
    	if(enviar){    		  
    		fecha.setMonth(fecha.getMonth()-1)
    		val fechaFormateada = formateador.format(fecha)
    		val textoEmail : String = "Buenos días " + trabajador.nombre + ".\nTe envío el desprendible de nómina correspodiente al mes de " + fechaFormateada + ".\nGracias."
    		val email = new EmailVO("paularodriguez@seven4n.com", trabajador.mail, null , "Desprendible de nómina " + fechaFormateada , textoEmail , rutaDestino + trabajador.nombre + ".pdf");
    		println(trabajador.nombre + " - " + trabajador.totalAPagar + " - " + trabajador.totalSalario + " - " + trabajador.deducciones);
    		mailSender.send(email);
    	}
    }
  }
}