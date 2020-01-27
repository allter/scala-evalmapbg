# scala-evalmapbg
evalMapBackground for fs2 Streams

Add something like this to your build.sbt:

```
lazy val root = (project in file(".")).dependsOn(evalMapBackground)

lazy val evalMapBackground = RootProject(uri("https://github.com/allter/scala-evalmapbg.git"))
```
