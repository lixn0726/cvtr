# CVTR

CVTR是单词Convertor的缩写，作为一个Java程序员，我们经常会遇见需要将A类的对象转换成另一个类B的对象的情况。而现在其实有很多好用的工具，比如说Mapstruct，Spring的BeanUtils等等。但是对于我来说，它们其实都是在编译期间帮我们实现了转换，也就是说不会直接的显示在编辑器上，而它们到底是如何转换的也是需要我们从源码才能得知的。所以，为了自己的方便，我折腾了这么个Idea插件，虽然并不完美，它也不够聪明，但是对我来说基本上可以满足使用。

对于我们经常遇见的场景，如上面所说，经常是将一个类转换成另一个类，比如下面这样的类：

`SourceObject.java`

![SourceObject Definition](https://github.com/lixn0726/cvtr/blob/master/img/SourceInnerObject.png)

`SourceInnerObject.java`

![SourceInnerObject Definition](https://github.com/lixn0726/cvtr/blob/master/img/SourceInnerObject.png)

`TargetObject.java`

![TargetObject Definition](https://github.com/lixn0726/cvtr/blob/master/img/TargetObject.png)

`TargetInnerObject.java`

![TargetInnerObject Definition](https://github.com/lixn0726/cvtr/blob/master/img/TargetInnerObject.png)

`ThirdLevelInnerObject.java`

![ThirdLevelInnerObject Definition](https://github.com/lixn0726/cvtr/blob/master/img/ThirdLevelInnerObject.png)

假设这就是我们所有的类，这样的类定义应该也基本覆盖了我们很大一部分的使用场景。

然后，像类命名一样，我们需要把SourceObject转换成TargetObject，我们一般都是在SourceObject中直接定义一个方法，如下：

![raw-method](https://github.com/lixn0726/cvtr/blob/master/img/raw-method.png)

并且可以看到，这里还有额外的一些参数，这些都是可能用得上的属性，然后，我们只需要选中这个方法名，并右键点击，就可以看到一个选项如下：

![click method](https://github.com/lixn0726/cvtr/blob/master/img/click.png)

点击这个**==Convert Here==**即可。然后我们就可以看到类转换的代码被自动生成到了我们的方法体中，如下：

![convert result](https://github.com/lixn0726/cvtr/blob/master/img/convert.png)

就像我前面所说，这个工具并不是那么的完美，甚至可能有些场景下会很难用，但是它的核心使命是去减少Java开发的工作量，并且将这些转换的代码可视化，便于Debug。每一个匹配的字段都会被自动填充，而没有匹配的则会留下一个空白的括号，这就需要我们手动去填充里面的字段。

并且，这是该插件的第一个版本，后续或许我会做一些优化和新功能；也有可能我就这么一直用下去了。大家可以把代码拉到本地自己按需修改。其中的逻辑也是很容易就可以理解的，希望我的代码也足够的简洁易懂。