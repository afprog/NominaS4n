package co.s4n.desprendibles.trabajador

import java.util.HashMap
import scala.Array.canBuildFrom
import org.apache.log4j.Logger
import org.docx4j.openpackaging.packages.OpcPackage
import org.docx4j.openpackaging.parts.Part
import org.docx4j.openpackaging.parts.SpreadsheetML.SharedStrings
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart
import org.xlsx4j.sml.STCellType
import scala.util.Try
import scala.util.Success
import scala.util.Failure

class ProcesamientoXlsx (pathParam : String){
  
  
  var path : String  = pathParam
  var worksheets = Array [WorksheetPart]()
  var sharedStrings : SharedStrings  = new SharedStrings()
  var log = Logger.getLogger(getClass().getName())
  var trabajadores = Array[Trabajador]()
  
  def extraerTrabajadores() : Option[Array[Trabajador]] = {
        println(" PAth archivo " + path)
        abrirXlsx match {
      case Right(value) => Some(extraerInformacionArchivo(value))
      case Left(value) => println("Error")
      None
    }
  }
  
  private def  printInfo(p:Part, sb:StringBuilder, indent : String)  {
            sb.append("\n" + indent + "Part " + p.getPartName() + " [" + p.getClass().getName() + "] " );
            if (p.isInstanceOf[WorksheetPart]) {
              var w: WorksheetPart = p.asInstanceOf[WorksheetPart]
              worksheets = worksheets :+ w
            }
            else if (p.isInstanceOf[SharedStrings]) {
                    sharedStrings = p.asInstanceOf[SharedStrings];
            }
    }
   
    /**
     * This HashMap is intended to prevent loops.
     */
    var handled = new HashMap[Part, Part]
   
  private  def traverseRelationships(wordMLPackage : OpcPackage,
                    rp : RelationshipsPart,
                    sb : StringBuilder, indent : String)  {
           var relation = rp.getRelationships().getRelationship()
            for ( r <- 0 until relation.size() ) {
                log.info("For Relationship Id=" + relation.get(r).getId()
                                    + " Source is " + rp.getSourceP().getPartName()
                                    + ", Target is " + relation.get(r).getTarget() );
           
                    if (relation.get(r).getTargetMode() != null
                                    && relation.get(r).getTargetMode().equals("External") ) {
                           
                            sb.append("\n" + indent + "external resource " + relation.get(r).getTarget() 
                                               + " of type " + relation.get(r).getType() )
                    }else{
                      var part = rp.getPart(relation.get(r))
                      printInfo(part, sb, indent);
                      if (handled.get(part)!=null) {
                              sb.append(" [additional reference] ")
                      }else{
                        handled.put(part, part)
                        if (part.getRelationshipsPart()==null) {
                                // sb.append(".. no rels" );                                           
                        } else {
                                traverseRelationships(wordMLPackage, part.getRelationshipsPart(), sb, indent + "    ")
                        }
                      }
                    }
                                   
            }
           
           
    }
  
  private def abrirXlsx:Either[Throwable, org.docx4j.openpackaging.packages.OpcPackage] = {
    Try{
      org.docx4j.openpackaging.packages.OpcPackage.load(new java.io.File(path))
    }
    match {
          case Success(archivo) => 
            Right(archivo)
          case Failure( exception ) =>
            Left(exception)
    }
  }
  
  private def extraerInformacionArchivo(xlsxPkg:org.docx4j.openpackaging.packages.OpcPackage): Array[co.s4n.desprendibles.trabajador.Trabajador] = {
//        val xlsxPkg = org.docx4j.openpackaging.packages.OpcPackage.load(new java.io.File(path))
    var rp = xlsxPkg.getRelationshipsPart()
    //Part part = rp.getPart()
    var sb = new StringBuilder()
    printInfo(rp, sb, "");
    traverseRelationships(xlsxPkg, rp, sb, "    ");
    //System.out.println(sb.toString());
    println("worksheets " + this.worksheets.size)

    for(sheet <- worksheets) {
            System.out.println(sheet.getPartName().getName() );
            println(sheet.toString());
            var ws = sheet.getJaxbElement();
            var data = ws.getSheetData();
            val dataRow = data.getRow()
            var trabavadoresValidos : Boolean = false
            for ( r <- 0 until dataRow.size() ) {
                    System.out.println("row " + dataRow.get(r).getR() );
                    var valor : String = null
                    var cell = dataRow.get(r).getC().get(0);
                    if (cell.getT().equals(STCellType.S) ){
                        var i:Int = Integer.parseInt(cell.getV())
                      valor = sharedStrings.getJaxbElement().getSi().get(Integer.parseInt(cell.getV())).getT()
                    }
                    println("Nombre " + valor);
                    if((valor == null || valor.equals("TOTAL")) && trabavadoresValidos)
                      trabavadoresValidos = false
                    if(trabavadoresValidos){
                          var numerosCeldas = Array()
                          var cellRow = dataRow.get(r).getC()
                          var trabajador = new Trabajador(cellRow.get(1).getV().toLong, 
                          sharedStrings.getJaxbElement().getSi().get(Integer.parseInt(cellRow.get(0).getV())).getT(),
                          cellRow.get(11).getV().toDouble, 
                          cellRow.get(14).getV().toDouble, 
                          cellRow.get(16).getV().toDouble, 
                          cellRow.get(17).getV().toDouble, 
                          cellRow.get(23).getV().toDouble, 
                          cellRow.get(8).getV().toDouble, 
                          cellRow.get(22).getV().toDouble, 
                          cellRow.get(18).getV().toDouble, 
                          cellRow.get(21).getV().toDouble, 
                          cellRow.get(24).getV().toDouble,
                          sharedStrings.getJaxbElement().getSi().get(Integer.parseInt(cellRow.get(38).getV())).getT(),
                          cellRow.get(34).getV().toDouble,
                          cellRow.get(32).getV().toDouble,
                          cellRow.get(5).getV().toInt,
                          cellRow.get(6).getV().toDouble,
                          cellRow.get(10).getV().toDouble,
                          cellRow.get(12).getV().toDouble,
                          sharedStrings.getJaxbElement().getSi().get(Integer.parseInt(cellRow.get(39).getV())).getT())
                          println("Trabajador " + trabajador.toString)
                          trabajadores = trabajadores :+ trabajador
                    }
                    if(valor != null && valor.equals("NOMBRE"))
                      trabavadoresValidos = true
            }
    }
    trabajadores
  }

}