name := "phonebook"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
    "com.typesafe" % "scalalogging-slf4j_2.10" % "1.1.0",
    "org.slf4j" % "slf4j-log4j12" % "1.7.6"
)

unmanagedSourceDirectories in Compile <++= baseDirectory { base =>
  Seq(
    base / "src/main/resources",
    base / "src/test/resources"
  )
}


