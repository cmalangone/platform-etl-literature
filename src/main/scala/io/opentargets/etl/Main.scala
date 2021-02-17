package io.opentargets.etl

import com.typesafe.scalalogging.LazyLogging
import io.opentargets.etl.literature._

object Main {

  def main(args: Array[String]): Unit = {
    ETL(args)
  }
}

object ETL extends LazyLogging {

  def applySingleStep(step: String): Unit ={
      print(step)
  }

  def apply(steps: Seq[String]): Unit = {

    ETLSessionContext() match {
      case Right(otContext) =>
        implicit val ctxt: ETLSessionContext = otContext
        logger.debug(ctxt.configuration.toString)

        val etlSteps =
          if (steps.isEmpty) otContext.configuration.common.defaultSteps
          else steps

        val unknownSteps = etlSteps filterNot otContext.configuration.common.defaultSteps.contains
        val knownSteps = etlSteps filter otContext.configuration.common.defaultSteps.contains

        logger.info(s"valid steps to execute: $knownSteps")
        if (unknownSteps.nonEmpty) logger.warn(s"invalid steps to skip: $unknownSteps")

        knownSteps.foreach { step =>
          logger.debug(s"step to run: '$step'")
          ETL.applySingleStep(step)
        }


      case Left(ex) => logger.error(ex.prettyPrint())
    }
  }
}
