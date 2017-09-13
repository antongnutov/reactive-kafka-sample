import sbt.Def

/**
  * Configures Scala for this project.
  */
object ScalaConfig {
  val compilerOptions = Def.setting(Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature","-unchecked",
    "-language:postfixOps", "-language:implicitConversions",
    "-Ywarn-dead-code",
    "-Xlint"
  ))

  val version = "2.12.3"
}