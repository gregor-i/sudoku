package algorithms

import scala.annotation.tailrec
import scala.collection.immutable.Queue

object TreeTraversal {
  def depthFirstSearch[N](root: N, children: N => Iterable[N]): LazyList[N] = {
    def loop(acc: List[N]): Option[(N, List[N])] =
      acc match {
        case head :: tail =>
          Some((head, children(head).toList ++ tail))
        case Nil =>
          None
      }

    LazyList.unfold[N, List[N]](root :: Nil)(loop)
  }

  def breadthFirstSearch[N](root: N, children: N => Iterable[N]): LazyList[N] = {
    def loop(acc: Queue[N]): Option[(N, Queue[N])] =
      acc.headOption.map { head =>
        (head, acc.tail.enqueueAll(children(head)))
      }

    LazyList.unfold[N, Queue[N]](Queue(root))(loop)
  }

  // an optimized dfs. it only returns the leaves. there is no use for this function based on bfs
  def traverseLeaves[N](root: N, children: N => Iterable[N]): LazyList[N] = {
    @tailrec
    def loop(acc: List[N]): Option[(N, List[N])] =
      acc match {
        case head :: tail =>
          val c = children(head)
          if (c.isEmpty)
            Some((head, tail))
          else
            loop(c.toList ++ tail)
        case Nil =>
          None
      }

    LazyList.unfold[N, List[N]](root :: Nil)(loop)
  }
}
