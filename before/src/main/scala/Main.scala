import scala.concurrent._
import scala.concurrent.duration._

object Main extends App {
  implicit val ec = ExecutionContext.global
  try {
    Await.result(foo(), 2.seconds)
  } catch {
    case ex: Throwable => ex.printStackTrace
  }

  def foo() = {
    Future {
      Thread.sleep(100)
      Await.result(bar(), 2.seconds)
    }
  }

  def bar() = {
    Future {
      Thread.sleep(200)
      Await.result(blowUp(), 2.seconds)
    }
  }

  def blowUp() = {
    Future {
      Thread.sleep(200)
      throw new Exception("nested error in async code")
    }
  }
}
