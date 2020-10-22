## TDR Image Scanner

This is run periodically inside a lambda which is triggered by a cloudwatch event rule on a schedule.

The scanning is in three steps.
* List the repositories in ECR
* List each image for those repositories
* Scan each image

Running locally
* You need to create a Main object and call the lambda function in order to run it locally.

```scala
package uk.gov.nationalarchives.ecr.scan

import java.io.ByteArrayOutputStream

object Main extends App {
  val output: ByteArrayOutputStream = new ByteArrayOutputStream()
  val lambda: Unit = new Lambda().process(null, output)
  val response = output.toByteArray.map(_.toChar).mkString
}
```
