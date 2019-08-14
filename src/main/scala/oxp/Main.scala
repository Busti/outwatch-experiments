package oxp

import cats.effect.{ExitCode, IO, IOApp}
import monix.execution.Scheduler.Implicits.global
import outwatch.dom._
import outwatch.dom.dsl._
import snabbdom.VNodeProxy

object Main extends IOApp {
  def counter =
    for {
      count <- Handler.create(0)
    } yield
      div(cls := "counter")(
        h2(count.map(c => s"The button has been $c clicked times.")),
        button(onClick(count.map(_ + 1)) --> count)("click me!")
      )

  val counterProxy: IO[VNodeProxy] = counter.flatMap(OutWatch.toSnabbdom)

  val root: VNode = div(
    for {
      counterInstance <- counterProxy
    } yield Seq[VDomModifier](
      h1("Hello World!"),
      div(counterInstance)
    )
  )

  override def run(args: List[String]): IO[ExitCode] =
    for {
      - <- IO { println("Hello from scala.js") }
      _ <- OutWatch.renderReplace("#app", root)
    } yield ExitCode.Success
}
