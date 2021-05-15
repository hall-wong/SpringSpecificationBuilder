# SpringSpecificationBuilder
欢迎使用Spring Specification Builder，
本项目旨在减少开发人员在对Specification对象构建时的繁琐而单调的操作。

* 本项目在使用时需要继承SpringSpecificationBuilder，并通过泛型指定所要查询的持久类；
* 通过对继承类加上注解来达到对继承类注册的功能。注册采用懒加载模式，并会校验查询条件的合理性；
* SpringSpecificationBuilder中的`set`方法，通过反射来保持代码的动态性，使得继承类不需要声明查询所需的字段；
* 通过`build`方法获取Specification实例，这个实例是由SpringSpecificationImpl实现的。

具体使用方式如下：

```
@QueryRequirement(restrictions = {@Restriction(field = "myField",operator = Operator.NotNull)})
public class MySpecificationBuilder<MyEntity> extends SpringSpecificationBuilder{
    public static void main(String[]args){
        Specification<MyEntity> spec = new MySpecificationBuilder().set("myField").build();
        //使用spec对象进行查询
    }
}
```

_English part is translate by Google_

Welcome to the Spring Specification Builder, this project is designed to reduce the tedious and monotonous operation of developers in the construction of the Specification instance.

* This project needs to inherit SpringSpecificationBuilder when it is used, and specify the persistent class to be queried through generics.
* The function of registering an inherited class is achieved by annotating the inherited class. Registration uses lazy loading mode and will verify the reasonableness of the query conditions.
* The set method in SpringSpecificationBuilder keeps the dynamics of the code through reflection, so that the inherited class does not need to declare the fields required by the query.
* Get the Specification instance through the build method, which is implemented by SpringSpecificationImpl.

Example code as following:

```
@QueryRequirement(restrictions = {@Restriction(field = "myField",operator = Operator.NotNull)})
public class MySpecificationBuilder<MyEntity> extends SpringSpecificationBuilder{
    public static void main(String[]args){
        Specification<MyEntity> spec = new MySpecificationBuilder().set("myField").build();
        //Query with the specification instance
    }
}
```
