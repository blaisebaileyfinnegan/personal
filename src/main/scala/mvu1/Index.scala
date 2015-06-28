package mvu1

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}

object Index extends js.JSApp {

  case class State(selected: Option[Int])

  val buttons = js.Array(
    "old website",
    "github"
  )

  val frustumHeight: Int = g.window.innerHeight.asInstanceOf[Int]
  val showHeight: Int = frustumHeight  * buttons.length
  val slideHeight: Int = (frustumHeight * 0.75).toInt

  class Backend($: BackendScope[Unit, State]) {
    def move(step: Int): Unit = $.modState { s =>
      val newVal = s.selected.getOrElse(-1) + step
      val finalVal =
        if (newVal < 0) buttons.length - 1
        else newVal

      State(Some(finalVal % buttons.length))
    }

    val actions = Map(
      "Up" -> (() => move(-1)),
      "Down" -> (() => move(1))
    ).withDefaultValue(() => ())

    def onKeyDown(e: js.Dynamic): Unit = {
      actions(e.keyIdentifier.asInstanceOf[String])()
    }
  }

  case class ButtonProps(name: String, index: Int, selected: Option[Int])
  class ButtonBackend($: BackendScope[ButtonProps, Unit]) {

    def getTranslateY(slide: Int): Int = {
      val selected = $.props.selected.getOrElse(0)
      (slide - selected) * frustumHeight
    }

  }

  def main(): Unit = {
    val Button = ReactComponentB[ButtonProps]("Button")
      .initialState(())
      .backend(new ButtonBackend(_))
      .render((P, S, B) =>
        <.div (
          ^.classSet1(
            "button",
            "selected" -> P.selected.contains(P.index)
          ),
          ^.height := slideHeight,
          ^.lineHeight := slideHeight + "px",
          ^.transform := s"translateY(${B.getTranslateY(P.index)}px)",
          P.name
        )
      )
      .build

    val Main = ReactComponentB[Unit]("Main")
      .initialState(State(None))
      .backend(new Backend(_))
      .render((P, S, B) =>
        <.div(
          ^.className := "main",
          buttons.zipWithIndex.map {
            case (n, i) => Button(ButtonProps(n, i, S.selected))
          }
        )
      )
      .componentDidMount(scope => {
        g.document.addEventListener("keydown", scope.backend.onKeyDown(_: js.Dynamic))
      })
      .buildU

    React.render(Main(), g.document.body.asInstanceOf[dom.Node])
  }
}
