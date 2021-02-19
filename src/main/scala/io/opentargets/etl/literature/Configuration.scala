package io.opentargets.etl.literature

import com.typesafe.config.ConfigFactory
import pureconfig.ConfigReader.Result
import com.typesafe.scalalogging.LazyLogging
import io.opentargets.etl.literature.spark.Helpers.IOResourceConfig
import pureconfig._
import pureconfig.generic.auto._

object Configuration extends LazyLogging {
  lazy val config: Result[OTConfig] = load

  case class Common(defaultSteps: Seq[String],output: String, outputFormat: String)

  case class EMPCOutput( grounding: IOResourceConfig )

  case class EMPCSection(
                         otLuts: IOResourceConfig,
                         nmpAbstract: IOResourceConfig,
                         nmpFullText: IOResourceConfig,
                         outputs: EMPCOutput
                        )

  case class OTConfig(
                       sparkUri: Option[String],
                       common: Common,
                       empc: EMPCSection
                     )

  def load: ConfigReader.Result[OTConfig] = {
    logger.info("load configuration from file")
    val config = ConfigFactory.load()

    val obj = ConfigSource.fromConfig(config).load[OTConfig]
    logger.debug(s"configuration properly case classed ${obj.toString}")

    obj
  }
}
