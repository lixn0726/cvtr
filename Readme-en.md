# CVTR

CVTR is the abbreviation of Convertor. As a Java developer, we often need to convert instance of class A to another instance of class B with some additional fields.
And there are many useful tools like Mapstruct, Spring BeanUtils and so on. But for me, they are all doing things behind the editor, so we don't really know how they
help us to do this conversion. So, for my own convenience, i build this plugin in a few days. It's not that smart and useful but is enough for myself.

In the scene that we always met, like there is a class containing some fields and we want to do a conversion from another class, assuming we have defined two classes like below:

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

That's all the classes that we need for this test. And i think it can cover most of your cases.

And then, we want to convert the `SourceObject` to the `TargetObject` , All we need to do is defining a method in the `SourceObject`. Like that:

![raw-method](https://github.com/lixn0726/cvtr/blob/master/img/raw-method.png)

And there are some additional parameters that we may need. And now, we can easily generate code when we select the methods' name by our mouse or cursor, do the right click and we will see a menu option like below:

![click method](https://github.com/lixn0726/cvtr/blob/master/img/click.png)

CLICK THAT. And you will see all the auto-generated code for this conversion method in your methods' body like below:

![convert result](https://raw.github.com/lixn0726/cvtr/blob/master/img/convert.png)

As i said, this tool is not that perfect, all it can do is to reduce our workload and make all the conversion code visible. Every matched variable will be filled automatically, and the variable that cannot be matched will left an empty bracket in the editor, and you should do it by your self.

And it's the very first version of this plugin, maybe i will do some optimization later. Or maybe i will just leave it as a not bad tool. You cannot get any promise from me hahaha. And you can do some self-optimization for your self to use it. Everything you need to do is clone this project and modify the code as you want. The logic process of this plugin is easy to understand and i hope my code is clear enough for you guys.

