import AssemblyKeys._ 

seq(assemblySettings: _*)

name := "NominaS4N"

version := "1.0"

scalaVersion := "2.10.2"


unmanagedBase := baseDirectory.value / "lib"

