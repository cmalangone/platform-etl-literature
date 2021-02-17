package io.opentargets.etl.literature.spark

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.SparkConf
import org.apache.spark.sql._

object Helpers extends LazyLogging {

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
