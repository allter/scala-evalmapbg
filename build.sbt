name := "scala-evalmapbg"
scalaVersion := "2.12.10"
scalacOptions += "-Ypartial-unification"
scalacOptions += "-language:higherKinds"
scalacOptions += "-feature"

// available for 2.12, 2.13
libraryDependencies += "co.fs2" %% "fs2-core" % "2.0.1" // For cats 2 and cats-effect 2
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.0.0"
