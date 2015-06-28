package mvu1

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom

import scala.scalajs.js

object Backup extends js.JSApp {
  case class State(input: String, log: String)

  class Backend($: BackendScope[Unit, State]) {
    def message(s: String) = {
      $.modState(state => State("", state.log + state.input + "\n"))
    }

    def onChange(e: ReactEventI) = {
      $.modState(state => state.copy(input = e.target.value))
    }

    def onKeyPress(e: ReactKeyboardEventI) = {
      if (e.key == "Enter") {
        message($.state.input)
      }
    }
  }

  def main(): Unit = {
    val component = ReactComponentB[Unit]("Example")
      .initialState(State("", ""))
      .backend(new Backend(_))
      .render((_, S, B) =>
        <.div(
          <.pre(S.log),
          <.input(
            ^.onKeyPress ==> B.onKeyPress,
            ^.onChange ==> B.onChange,
            ^.value := S.input
          )
        )
      )
      .buildU

    React.render(component(), js.Dynamic.global.document.getElementById("main").asInstanceOf[dom.Node])
  }
}
