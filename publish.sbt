import sbtrelease._
import sbtrelease.ReleaseStateTransformations._
import com.typesafe.sbt.pgp.PgpKeys
import xerial.sbt.Sonatype

Sonatype.sonatypeSettings

val sonatypeURL =
"https://oss.sonatype.org/service/local/repositories/"

val updateReadme: State => State = { state: State =>
  val extracted = Project.extract(state)
  val scalaV = "2.10"
  val sbtV = "0.13"
  val v = extracted get version
  val org =  extracted get organization
  val n = extracted get name
  val snapshotOrRelease = if(extracted get isSnapshot) "snapshots" else "releases"
  val readme = "README.md"
  val readmeFile = file(readme)
  val newReadme = Predef.augmentString(IO.read(readmeFile)).lines.map{ line =>
    val matchReleaseOrSnapshot = line.contains("SNAPSHOT") == v.contains("SNAPSHOT")
    if(line.startsWith("addSbtPlugin") && matchReleaseOrSnapshot){
      s"""addSbtPlugin("${org}" % "${n}" % "$v")"""
    }else if(line.contains(sonatypeURL) && matchReleaseOrSnapshot){
      s"- [API Documentation](${sonatypeURL}${snapshotOrRelease}/archive/${org.replace('.','/')}/${n}_${scalaV}_${sbtV}/${v}/${n}-${v}-javadoc.jar/!/index.html)"
    }else line
  }.mkString("", "\n", "\n")
  IO.write(readmeFile, newReadme)
  val git = new Git(extracted get baseDirectory)
  git.add(readme) ! state.log
  git.commit(message = "update " + readme, sign = false) ! state.log
  "git diff HEAD^" ! state.log
  state
}

commands += Command.command("updateReadme")(updateReadme)

val updateReadmeProcess: ReleaseStep = updateReadme

def crossSbtCommand(command: String): Seq[ReleaseStep] = {
  def set(v: String) = releaseStepCommand("set sbtVersion in pluginCrossBuild := \"" + v + "\"")
  List(
    set("0.13.15"),
    releaseStepCommand(command),
    set("1.0.0-M6"),
    releaseStepCommand(command)
  )
}

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean
) ++ Seq[Seq[ReleaseStep]](
  crossSbtCommand("test"),
  crossSbtCommand("scripted")
).flatten ++ Seq[ReleaseStep](
  setReleaseVersion,
  commitReleaseVersion,
  updateReadmeProcess,
  tagRelease
) ++ crossSbtCommand("publishSigned") ++ Seq[ReleaseStep](
  setNextVersion,
  commitNextVersion,
  updateReadmeProcess,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)

pomPostProcess := { node =>
  import scala.xml._
  import scala.xml.transform._
  def stripIf(f: Node => Boolean) = new RewriteRule {
    override def transform(n: Node) =
      if (f(n)) NodeSeq.Empty else n
  }
  val stripTestScope = stripIf { n => n.label == "dependency" && (n \ "scope").text == "test" }
  new RuleTransformer(stripTestScope).transform(node)(0)
}

credentials ++= PartialFunction.condOpt(sys.env.get("SONATYPE_USER") -> sys.env.get("SONATYPE_PASSWORD")){
  case (Some(user), Some(pass)) =>
    Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
}.toList
