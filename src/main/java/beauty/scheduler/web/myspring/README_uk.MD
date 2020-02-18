<div align="center">
	<a href="https://github.com/VictorHilonenko/ServletProject/tree/master/src/main/java/beauty/scheduler/web/myspring/README.md"><code>English</code></a>
	<a href="https://github.com/VictorHilonenko/ServletProject/tree/master/src/main/java/beauty/scheduler/web/myspring/README_uk.md"><code>Українська</code></a>
</div>
<hr>
<div align="center">
	<img src="src/main/webapp/images/logo_uk.png">
</div>

# "MySpring" description

In this project was developed a mini Spring like framework, see package **beauty.scheduler.web.myspring**.

The main facade for the framework functionality is represented by several custom annotations:

#### @ServiceComponent
This annotation is applied on **classes** that have to be injected as beans to other classes with **@InjectDependency** annotation.
The Spring analogs for that are **@Service** and **@Component** annotations.

#### @InjectDependency
This annotation is applied on **fields** and the core of MySpring provides annotation-driven injection at runtime.
The Spring analogs for that is **@Autowired**.

#### @EndpointMethod
This annotation is applied on **methods** and maps HTTP requests to handler methods of MVC and REST endpoints.
The Spring analogs for that is **@RequestMapping**.

#### @Restriction
This annotation is applied on **methods** and provides annotation-driven security and behavior features of the web application.
Some similar functions of Spring are in security configuration (provided through extending of WebSecurityConfigurerAdapter abstract class which implements WebSecurityConfigurer interface).
Besides @Restriction annotation provides not only permissions/restrictions but also redirections.

#### @DefaultTemplate
This annotation is applied on **methods** and allows to make role-based specification for default JSP templates for a certain endpoint.
However the default template can be redefined in @EndpointMethod by returning a String with any other JSP template path based on business logic.

#### @ParamName
This annotation is applied on **method's parameters** that has described in urlPattern with curly braces {} and provides a convenient way of getting parameters passed in URI.
The Spring analogs for that is **@PathVariable**.

_**NOTE**: todo - describe **other injecting parameters possibilities** and **forms auto-assemling** features_ and **form-validation annotations**

## Some examples:

<div align="center">
	<img src="src/main/webapp/images/example1.png">
</div>
<div align="center">
	<img src="src/main/webapp/images/example2.png">
</div>
<div align="center">
	<img src="src/main/webapp/images/example3.png">
</div>
<div align="center">
	<img src="src/main/webapp/images/example4.png">
</div>
<div align="center">
	<img src="src/main/webapp/images/example5.png">
</div>