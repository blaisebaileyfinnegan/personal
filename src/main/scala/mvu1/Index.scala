package mvu1

import japgolly.scalajs
import japgolly.scalajs.react
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import mvu1.Index.OldWebsite
import org.scalajs.dom
import org.scalajs.dom.raw.Element

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}

object Index extends js.JSApp {
  case class State(selected: Option[Int])

  val DefaultSlide = ReactComponentB[SlideProps]("Slide")
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

  val MichaelSlide = ReactComponentB[SlideProps]("Michael")
    .initialState(())
    .backend(new MichaelBackend(_))
    .render((P, S, B) =>
      <.div(
        ^.classSet1(
          "michael"
        ),
        "Michael Vu"
      )
    )
    .build


  abstract class SlideDefinition(val component: ReactComponentC[SlideProps, Unit, Any, Element])
  case object Michael extends SlideDefinition(MichaelSlide)
  case object OldWebsite extends SlideDefinition(DefaultSlide)
  case object Search extends SlideDefinition(DefaultSlide)
  val buttons: js.Array[SlideDefinition] = js.Array(
    Michael,
    OldWebsite,
    Search
  )

  val showHeight: Int = frustumHeight  * buttons.length

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

  case class SlideProps(name: String, index: Int, selected: Option[Int])
  class ButtonBackend($: BackendScope[SlideProps, Unit]) {
    def getTranslateY(slide: Int): Int = {
      val selected = $.props.selected.getOrElse(0)
      (slide - selected) * frustumHeight
    }
  }

  class MichaelBackend($: BackendScope[SlideProps, Unit]) {
  }

  def main(): Unit = {

    val Wrapper = ReactComponentB[Unit]("Main")
      .initialState(State(None))
      .backend(new Backend(_))
      .render((P, S, B) =>
        <.div(
          ^.className := "main",
          buttons.zipWithIndex.map {
            case (n, i) => n.component.jsCtor(
              scalajs.react.WrapObj(
                SlideProps(n.getClass.getName, i, S.selected)))
          }
        )
      )
      .componentDidMount(scope => {
        g.document.addEventListener("keydown", scope.backend.onKeyDown(_: js.Dynamic))
      })
      .buildU

    React.render(Wrapper(), g.document.body.asInstanceOf[dom.Node])
  }
}
