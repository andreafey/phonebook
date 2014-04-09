name := "phonebook"

scalaVersion := "2.10.3"

addCommandAlias("phonebook", "run")

libraryDependencies ++= Seq(
   "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
   "junit" % "junit" % "4.8.1" % "test"
    )

unmanagedSourceDirectories in Compile <++= baseDirectory { base =>
  Seq(
    base / "src/main/resources",
    base / "src/test/resources"
  )
}
