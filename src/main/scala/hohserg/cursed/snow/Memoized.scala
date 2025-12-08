package hohserg.cursed.snow

import java.util.function.{Function=>JFunction}
import java.util.concurrent.{ConcurrentHashMap=>JConcurrentHashMap}

object Memoized {
  def apply[A, B](f: A => B): A => B = {
    val cache = new JConcurrentHashMap[A, B]()
    val javaFunctionWrapper = new JFunction[A, B] {
      override def apply(t: A): B = f(t)
    }
    cache.computeIfAbsent(_, javaFunctionWrapper)
  }
}
