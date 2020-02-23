<div align="center">
	<a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README.MD"><code>English</code></a>
	<a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README_uk.MD"><code>Українська</code></a>
</div>
<hr>
<div align="center">
	<img src="https://github.com/VictorHilonenko/ServletProject/raw/master/src/main/webapp/images/logo_uk.png">
</div>


# Опис "MySpring"

У цьому проекті був розроблений міні-фреймворк типу Spring (див. пакет **beauty.scheduler.web.myspring**).

Головний фасад для функціональної основи представлений кількома спеціальними анотаціями:

#### @ServiceComponent
Ця анотація застосовується до **класів**, які повинні бути впроваджені як залежності до полів інших класів, що мають анотацію **InjectDependency**.
Аналогом у Spring для цього функціоналу є анотації **@Service** та **@Component**.

#### @InjectDependency
Ця анотація застосовується до **полів**, в які ядро MySpring забезпечує впровадження залежності під час виконання.
Аналогом у Spring для цього є **@Autowired**.

#### @EndpointMethod
Ця анотація застосовується до **методів** та відображає HTTP-запити на оброблювальні методи кінцевих точок JSP/HTML та REST/JSON.
Аналогами Spring для цього є **@RequestMapping** та **@RestController**.

#### @Restriction
Ця анотація застосовується до **методів** та забезпечує функції обмеження доступу та поведінки веб-додатка.
Існують аналогічні функції Spring в конфігурації безпеки (вона забезпечується розширенням абстрактного класу WebSecurityConfigurerAdapter, який реалізує інтерфейс WebSecurityConfigurer).
Крім того, анотація **@Restriction** забезпечує не тільки дозволи / обмеження, але й перенаправлення на інші сторінки за вказаними у анотаціях правилами.

#### @DefaultTemplate
Ця анотація застосовується до **методів** і дозволяє зробити специфікацію на основі ролей для шаблонів JSP-сторінок за замовчуванням для відповідної кінцевої точки.
Також шаблон, який буде використаний для відображення сторінки можна перевизначити в конкретному **методі**, повернувши оператором **return** рядок з будь-яким іншим шляхом до шаблону JSP-сторінки на основі бізнес-логіки.

#### @ParamName
Ця анотація застосовується до **параметрів** методу, які описані в параметрі **urlPattern** між фігурними дужками {} та забезпечує зручний спосіб отримання значень параметрів, переданих в URI.
Аналогом Spring для цього є **@PathVariable**.

## Деякі приклади:

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