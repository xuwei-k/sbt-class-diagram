package diagram

object Reflect {

  private def getSuperClasses(clazz: Class[_]): List[Class[_]] = {

    @scala.annotation.tailrec
    def loop(o: Class[_], result: List[Class[_]]): List[Class[_]] = {
      val superClass = o.getSuperclass
      if (superClass == null) {
        result
      } else {
        loop(superClass, superClass :: result)
      }
    }

    loop(clazz, List(clazz))
  }

  def getAllClassAndTrait(classes: Class[_]*): List[Class[_]] = {

    def loop(c: Class[_], result: List[Class[_]]): List[Class[_]] = {
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
