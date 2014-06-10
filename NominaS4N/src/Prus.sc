object Prus {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  var pru = Array[String]()                       //> pru  : Array[String] = Array()
  pru :+ "asdaf"                                  //> res0: Array[String] = Array(asdaf)
  
  var s:String = ""                               //> s  : String = ""
  println(s)                                      //> 
  if(s==null || s.isEmpty())
  println("Si")                                   //> Si
}