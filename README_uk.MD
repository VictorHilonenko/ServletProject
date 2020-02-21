<div align="center">
	<a href="https://github.com/VictorHilonenko/ServletProject/tree/master/README.MD"><code>English</code></a>
	<a href="https://github.com/VictorHilonenko/ServletProject/tree/master/README_uk.MD"><code>Українська</code></a>
</div>
<hr>
<div align="center">
	<img src="https://github.com/VictorHilonenko/ServletProject/raw/master/src/main/webapp/images/logo_uk.png">
</div>
<hr>
<div align="center">
	<a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README_uk.MD"><code><strong>NOTE: див. також опис розділу "MySpring" цього сервлетного проекту</strong></code></a>
</div>

# Система **Розклад Салону Краси**  
Потрібно створити сторінку, 
яка дозволяє переглянути розклад роботи співробітників салону,
так щоб можна було записатися на прийом.
**Записи** клієнтів повинен бачити тільки **Адміністратор** і **Майстер**, інші бачать тільки зайняте місце.
Після надання послуг **Клієнт** залишає відгук.
Пропозиція про написання відгуку приходить на **електронну пошту**.
**Адміністратор** може читати відгуки про колег.

## **_Уточнення_** умов, **_розширення_** завдання
До початку розробки уточнено та узгоджено такі умови:

1. Майстер і Адміністратор може бути **не один**.
1. У майстрів є **спеціальності**.
1. Вважаємо, що **робочий час** майстрів = робочому часу салону (його встановлено **з 8 до 20** без вихідних).
1. Час надання кожної послуги = **1 година**
1. Якщо на вибраний час всі майстри **вже зайняті**, клієнт не зможе на нього записатися.
1. Клієнти і Майстри на «зайнятому місці» бачать своє Ім’я, якщо це їх «Запис», Адміністратори бачать **розширену** інформацію.
1. Майстер встановлює ознаку **«Послугу надано»** в «записах» де він є виконавцем. Саме після цього **відправляється пошта** і у Клієнта з’являється **можливість надати відгук**.
1. Відгуки можуть читати всі зареєстровані користувачі, однак тільки **адміністратор** може бачити **всі відгуки**, Майстер і Клієнт можуть бачити тільки відгуки, **де вони є** відповідними **учасниками**.

<div align="center">
	<a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README_uk.MD"><code><strong>NOTE: див. також опис розділу "MySpring" цього сервлетного проекту</strong></code></a>
</div>

## Технології
1. DB - MySql
1. Java 8
1. Maven

## Установка  

1. Клонувати проект (git clone git://github.com/VictorHilonenko/ServletProject.git BeautyScheduler)
1. Ви можете залишити налаштування у `/src/main/webapp/META-INF/context.xml` "як є" і почати роботу одразу
1. Або виконати наступні кроки:
- В разі потреби встановити MySQL 
- Встановити базу даних: schema.sql з папки DUMP_DB
- Наповнити базу даними: populate.sql з папки DUMP_DB
- Налаштувати `/src/main/webapp/META-INF/context.xml` у відповідності до параметрів БД

## Запуск

1. Виконати команду термінала: ```mvn clean tomcat7:run``` або додати конфігурацію і запустити програму з IDE  
1. Перейти за посиланням: [```localhost:8989```](http://localhost:8989/)

#### Список вже зареєстрованих користувачів та їх паролі:
<table>
	<tr>
		<th>Login</th><th>Password</th>
	</tr>
	<tr>
		<td>admin@mail.com</td><td>admin</td>
	</tr>
	<tr>
		<td>lindsey@mail.com</td><td>user</td>
	</tr>
	<tr>
		<td>caitlin@mail.com</td><td>user</td>
	</tr>
	<tr>
		<td>jennie@mail.com</td><td>user</td>
	</tr>
	<tr>
		<td>julia@mail.com</td><td>master</td>
	</tr>
	<tr>
		<td>tanya@mail.com</td><td>master</td>
	</tr>
	<tr>
		<td>nata@mail.com</td><td>master</td>
	</tr>
	<tr>
		<td>josy@mail.com</td><td>master</td>
	</tr>
</table>


