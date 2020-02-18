<div align="center">
	<a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README.MD"><code>English</code></a>
	<a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README_uk.MD"><code>Українська</code></a>
</div>
<hr>
<div align="center">
	<img src="https://github.com/VictorHilonenko/ServletProject/raw/master/src/main/webapp/images/logo_en.png">
</div>

# "MySpring" description

In this project a mini Spring like framework was developed (see package **beauty.scheduler.web.myspring**).

The main facade for the framework functionality is represented by several custom annotations:

#### @ServiceComponent
This annotation is used on **classes** that have to be injected as beans to other classes with **@InjectDependency** annotation.
The Spring analogs for that are **@Service** and **@Component** annotations.

#### @InjectDependency
This annotation is used on **fields** and the core of MySpring provides annotation-driven injection at runtime.
The Spring analogs for that is **@Autowired**.

#### @EndpointMethod
This annotation is used on **methods** and maps HTTP requests to handler methods of MVC and REST endpoints.
The Spring analogs for that is **@RequestMapping**.

#### @Restriction
This annotation is used on **methods** and provides annotation-driven security and behavior features of the web application.
There are Similar Spring functions in security configuration (it is provided by extending WebSecurityConfigurerAdapter abstract class which implements WebSecurityConfigurer interface).
Besides, @Restriction annotation provides not only permissions/restrictions but also redirections.

#### @DefaultTemplate
This annotation is used on **methods** and allows to make role-based specification for default JSP templates for a certain endpoint.
However, the default template can be redefined in @EndpointMethod by returning a String with any other JSP template path based on business logic.

#### @ParamName
This annotation is used on **method's parameters** that are described in urlPattern with curly braces {} and provides a convenient way of getting parameters passed in URI.
The Spring analogs for that is **@PathVariable**.

_**NOTE**: todo - describe **other injecting parameters possibilities** and **forms auto-assembling** features and **form-validation annotations**_

## Some examples:
<div align="center">
	<img src="https://raw.githubusercontent.com/VictorHilonenko/ServletProject/master/src/main/webapp/images/example1.png">
</div>
<div align="center">
	<img src="https://raw.githubusercontent.com/VictorHilonenko/ServletProject/master/src/main/webapp/images/example2.png">
</div>
<div align="center">
	<img src="https://raw.githubusercontent.com/VictorHilonenko/ServletProject/master/src/main/webapp/images/example3.png">
</div>
<div align="center">
	<img src="https://raw.githubusercontent.com/VictorHilonenko/ServletProject/master/src/main/webapp/images/example4.png">
</div>
<div align="center">
	<img src="https://raw.githubusercontent.com/VictorHilonenko/ServletProject/master/src/main/webapp/images/example5.png">
</div>