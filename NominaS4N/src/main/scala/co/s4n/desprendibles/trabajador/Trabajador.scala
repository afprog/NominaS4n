package co.s4n.desprendibles.trabajador

class Trabajador (cedulaPar: Long, nombrePar: String, salarioPar: Double, auxilioPar: Double, epsPar: Double, afpPar: Double, seguroexequialPar: Double, vacacionesPar: Double, parqueaderoPar: Double, fondoPar: Double, retefuentePar: Double, otrosPar: Double, mailPar:String, primaPar : Double, iCesantiasPar : Double, diasPar : Int, salatioBasePar : Double, primaVacacionesParam : Double, auxiliotransParam : Double, cargoParam : String) {
  val nombre: String = nombrePar
  val cedula: Long = cedulaPar
  val salario: Double = salarioPar
  val auxilio: Double = auxilioPar
  val eps: Double = epsPar
  val afp: Double = afpPar
  val seguroexequial: Double = seguroexequialPar
  val vacaciones: Double = vacacionesPar
  val parqueadero: Double = parqueaderoPar
  val fondo: Double = fondoPar
  val retefuente: Double = retefuentePar
  val otros: Double = otrosPar
  val mail : String = mailPar
  val prima : Double = primaPar
  val dias : Int = diasPar
  val iCesantias : Double = iCesantiasPar
  val salarioBase : Double = salatioBasePar
  val primaVacaciones : Double = primaVacacionesParam
  val auxiliotransporte : Double = auxiliotransParam
  val cargo : String = cargoParam


  def deducciones(): Double = {
    eps + afp + seguroexequial + parqueadero + fondo + retefuente + otros 
  }

  def totalSalario(): Double = {
    salario + auxilio + vacaciones + prima + iCesantias + primaVacaciones + auxiliotransporte
  }

  def totalAPagar(): Double = {
    totalSalario - deducciones
  }
  
  override def toString() : String = {
    var string : String = "Class " + getClass().getName() + " nombre " + nombre + " cedula " + cedula + " salario " + salario + " auxilio " + auxilio + " eps " + eps + " afp " + afp + " seguroexequial " + seguroexequial + " vacaciones " + vacaciones + "primavacaciones" + primaVacaciones + " parqueadero " + parqueadero + " fondo " + fondo + " retefuente " + retefuente + " otros " + otros + " mail " + mail  + " prima " + prima + " iCesantias " + iCesantias + " dias " + dias + " salarioBase " + salarioBase + " auxiliotrans " + auxiliotransporte + "Cargo " + cargo
    return string
  }

}
