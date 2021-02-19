package io.opentargets.etl.literature.spark

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions._

object Helpers extends LazyLogging {
  type IOResourceConfigurations = Map[String, IOResourceConfig]
  type IOResources = Map[String, IOResource]

  case class IOResource(data: DataFrame, configuration: IOResourceConfig)
  case class IOResourceConfigOption(k: String, v: String)
  case class IOResourceConfig(
                               format: String,
                               path: String,
                               options: Option[Seq[IOResourceConfigOption]] = None,
                               partitionBy: Option[Seq[String]] = None
                             )


  /** It normalises data from a specific match String cases. */
  def normalise(c: Column): Column = {
    // https://www.rapidtables.com/math/symbols/greek_alphabet.html
    translate(rtrim(lower(translate(trim(trim(c), "."), "/`''[]{}()- ", "")), "s"),
      "αβγδεζηικλμνξπτυω",
      "abgdezhiklmnxptuo")
  }
  /** It creates an hashmap of dataframes.
   *   Es. inputsDataFrame {"disease", Dataframe} , {"target", Dataframe}
   *   Reading is the first step in the pipeline
   */
  def readFrom(
                inputFileConf: IOResourceConfigurations
              )(implicit session: SparkSession): IOResources = {
    logger.info("Load files into a Map of names and IOResource")
    for {
      (key, formatAndPath) <- inputFileConf
    } yield key -> IOResource(loadFileToDF(formatAndPath), formatAndPath)
  }

  def loadFileToDF(pathInfo: IOResourceConfig)(implicit session: SparkSession): DataFrame = {
    logger.info(s"load dataset ${pathInfo.path} with ${pathInfo.toString}")

    pathInfo.options.foldLeft(session.read.format(pathInfo.format)) {
      case ops =>
        val options = ops._2.map(c => c.k -> c.v).toMap
        ops._1.options(options)
    }.load(pathInfo.path)
  }


  /** generate a spark session given the arguments if sparkUri is None then try to get from env
   * otherwise it will set the master explicitely
   * @param appName the app name
   * @param sparkUri uri for the spark env master if None then it will try to get from yarn
   * @return a sparksession object
   */
  def getOrCreateSparkSession(appName: String, sparkUri: Option[String]): SparkSession = {
    logger.info(s"create spark session with uri:'${sparkUri.toString}'")
    val sparkConf: SparkConf = new SparkConf()
      .setAppName(appName)
      .set("spark.driver.maxResultSize", "0")
      .set("spark.debug.maxToStringFields", "2000")

    // if some uri then setmaster must be set otherwise
    // it tries to get from env if any yarn running
    val conf = sparkUri match {
      case Some(uri) if uri.nonEmpty => sparkConf.setMaster(uri)
      case _                         => sparkConf
    }

    SparkSession.builder
      .config(conf)
      .getOrCreate
  }

}
