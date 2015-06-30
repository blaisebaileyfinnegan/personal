import scala.scalajs.js.Dynamic.{global => g}

package object mvu1 {
  val frustumHeight: Int = g.window.innerHeight.asInstanceOf[Int]
  val slideHeight: Int = (frustumHeight * 0.75).toInt
}
