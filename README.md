# sbt-class-diagram

### requirement

- <https://www.graphviz.org/>

### install

#### `build.sbt`

```scala
enablePlugins(ClassDiagramPlugin)
```

#### `project/plugin.sbt`

##### latest stable version

```scala
addSbtPlugin("com.github.xuwei-k" % "sbt-class-diagram" % "0.3.0")
```

### sample

#### scala.collection.immutable.List

![List](https://raw.githubusercontent.com/xuwei-k/sbt-class-diagram/master/sample/list.png)


#### scalaz 7.2.x

[![Scalaz](https://xuwei-k.github.io/scalaz-docs/diagram1.svg)](https://xuwei-k.github.io/scalaz-docs/diagram1.svg)
