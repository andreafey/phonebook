<<<<<<< HEAD
name := "phonebook2"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
   "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
   "junit" % "junit" % "4.8.1" % "test"
    )
=======
name := "phonebook"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
    "com.typesafe" % "scalalogging-slf4j_2.10" % "1.1.0",
    "org.slf4j" % "slf4j-log4j12" % "1.7.6"
)
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c

unmanagedSourceDirectories in Compile <++= baseDirectory { base =>
  Seq(
    base / "src/main/resources",
    base / "src/test/resources"
  )
}
<<<<<<< HEAD
=======


>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
