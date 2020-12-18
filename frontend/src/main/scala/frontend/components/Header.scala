package frontend.components

import frontend.Context
import frontend.pages.{LandingPageState, PuzzleState, SolverState}
import snabbdom.{Event, Node, Snabbdom}

import scala.util.Random

object Header {

  def renderHeader()(implicit context: Context[_]): Node =
    Node("div.top-bar")
      .child(
        Node("div")
          .child(
            Node("div")
              .child(Node("figure.image.is-32x32").child(Node("img").attr("src", Images.logo)))
              .child(Node("span").text("Sudoku"))
              .event[Event]("click", _ => context.update(LandingPageState()))
          )
          .child(
            Node("div")
              .child(
                Node("a.navbar-item")
                  .child(Icons.icon(Icons.play).classes("mr-1"))
                  .text("Play")
                  .event[Event]("click", _ => context.update(PuzzleState.loading(Random.nextInt())))
              )
          )
          .child(
            Node("div")
              .child(
                Node("a.navbar-item")
                  .child(Icons.icon(Icons.solve).classes("mr-1"))
                  .text("Solve")
                  .event[Event]("click", _ => context.update(SolverState.empty()))
              )
          )
      )
}
