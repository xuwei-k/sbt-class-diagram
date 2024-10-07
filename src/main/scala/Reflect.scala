package diagram

object Reflect {

  private def getSuperClasses(clazz: Class[?]): List[Class[?]] = {

    @scala.annotation.tailrec
    def loop(o: Class[?], result: List[Class[?]]): List[Class[?]] = {
      val superClass = o.getSuperclass
      if (superClass == null) {
        result
      } else {
        loop(superClass, superClass :: result)
      }
    }

    loop(clazz, List(clazz))
  }

  def getAllClassAndTrait(classes: Class[?]*): List[Class[?]] = {

    def loop(c: Class[?], result: List[Class[?]]): List[Class[?]] = {
      val interfaces = c.getInterfaces.toList

      if (interfaces.size == 0) {
        result
      } else {
        interfaces.flatMap { i => loop(i, i :: result) }
      }
    }

    {
      {
        for {
          c <- classes
          clazz <- getSuperClasses(c)
          ret <- loop(clazz, List(clazz))
        } yield {
          ret
        }
      }.toSet -- classes
    }.toList
  }

}
