# platform-etl-literature
This is a project structure for scala


sbt compile
sbt run
sbt package

scala target/scala-2.12/io-opentargets-etl-literature_2.12-0.1.jar

If you add the assembly plugin inside project/assembly.sbt
   addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.10")

Run the command:
   sbt assembly
   java -jar io-opentargets-etl-literature-assembly-0.1.jar
