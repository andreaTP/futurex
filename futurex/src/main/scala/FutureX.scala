import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object FutureX {

  def apply(body: () => Unit): Future[Unit] = macro applyImpl

  def applyImpl(c: Context)(body: c.Expr[() => Unit]) = {
    import c.universe._

    c.Expr[Future[Unit]](q"""{
        val fut = scala.concurrent.Future($body())
        fut.onComplete {
            case scala.util.Failure(ex) if ex.getMessage() == "nested error in async code" =>
              val file = implicitly[sourcecode.File]
              val line = implicitly[sourcecode.Line]
              val msg = file.value + ":" + line.value
              throw new Exception(msg, ex)
            case _ =>
          }
        fut
    }""")
  }
}

object AwaitX {

  def result(f: Future[Unit], timeout: FiniteDuration): Unit = macro resultImpl

  def resultImpl(
      c: Context)(f: c.Expr[Future[Unit]], timeout: c.Expr[FiniteDuration]) = {
    import c.universe._

    c.Expr[Unit](q"""{
        try {
          Await.result($f, $timeout)
        } catch {
          case ex: Throwable if ex.getMessage() == "nested error in async code" =>
            val file = implicitly[sourcecode.File]
            val line = implicitly[sourcecode.Line]
            val msg = file.value + ":" + line.value
            throw new Exception(msg, ex)
        }
    }""")
  }

}
