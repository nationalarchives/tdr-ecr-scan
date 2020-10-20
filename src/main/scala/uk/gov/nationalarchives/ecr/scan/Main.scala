package uk.gov.nationalarchives.ecr.scan

import uk.gov.nationalarchives.aws.utils.Clients.ecr
import uk.gov.nationalarchives.aws.utils.ECRUtils
import uk.gov.nationalarchives.aws.utils.ECRUtils.EcrImage

object Main extends App {
  val a = new Lambda().process(null, null)
  print(a)

}