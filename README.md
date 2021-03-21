# SupplyManagerSystem - SMS - BackEnd - 1.1.0
A Projekt az ELTE IK - PTI BSc képzéshez készült szakdolgozatnak (BACKEND).

```
Hallgató: Vitrai Gábor
Témavezető: Dr. Szendrei Rudolf
```
### A szakdolgozat címe: Vállalati ügyviteli rendszer fejlesztése Spring segítségével
#### A szakdolgozat témája:
> A szakdolgozatomban egy, a vállalatok életében elengedhetetlen szoftvert kívánok elkészíteni, amely a valósághoz közel áll, és
> lehetőséget nyújt a kis és közepes vállalkozásoknak, hogy figyelemmel kísérjék az ügyeiket, megrendeléseiket és azok adatait,
> partnereit. A cél egy olyan ügyviteli rendszer elkészítése, melyben a regisztrált cégek, nyomon követhetik a kiadott ajánlataikat,
> illetve beszerzéseiket, és azokról részletes információt és ezek nyomon követésére kényelmes eszközöket kaphatnak. A program
> lényegi részét Java-ban írom, a Spring keretrendszer segítségével, továbbá készítek hozzá egy webes frontendet, ahol a
> bejelentkezett felhasználók kezelhetik az adatbázisban található adatokat, megrendeléseket. Az egyes cégek alkalmazottjainak
> lehetőségük lesz ajánlatot tenni más cégeknek, és ahogy a partner céggel halad a kommunikáció, az ügyintézők könnyen rögzíteni
> tudják az egyes lépések közötti állapotot. Az architektúra koncepciója miatt a dolgozatot a REST által nyújtott eszközök köré
> kívánom építeni Spring segítségével. Ezen felül más, az iparban is használt fejlesztői eszközöket is igénybe tervezek venni, mint
> például a Git és a Lombok.

#### The topic of the Thesis
> In my BSc thesis, I aim to create software that is essential in the life of companies, which is close to reality and provides an opportunity
> for small and medium-sized enterprises to monitor their affairs, orders, partners, and data. The aim is to set up a management system in which 
> registered companies can track their purchases and receive detailed information on them next to convenient tools for tracking them. (Mini CRM system) 
> I wrote the essential part of the program in Java, with the help of the Spring framework, and I created a web frontend, 
> where logged-in users can manage the data and orders in the database. 
> Employees from each company will have the opportunity to order from other companies, and as communication with the partner company progresses, 
> managers can easily record the status between each step. I have built my thesis around the tools provided by REST with the help of Spring.

**Bővítési lehetőségek:**

	- A megrendelések több terméket is tartalmazhatnak egységárral.
	- A több országban működő cégekneknek választható nyelv bevezetése felhasználónként. (EUR/HUF valuták bevezetése megrendelésekre)
	- Értesítések bevezetése
	- Útvonaltervezés implementálása
	- Képek, egyéb dokumentumok csatolása megrendelésekhez, előzményekhez.
	- Havi Jelentés generálása funkció implementálása
	- Email hitelesítés implementálása majd email címmel való belépés engedélyezése.    
    - HTTPS bevezetése
    
    
**Változtatások a fejlesztés során:**

    - Felhasználók csak a saját cégük által kibocsátott előzményeket tekinthetik meg ezért hozzárendelésre került a Creator (Készítő) mező minden előzményhez.
    - History endpointokat át kellett gondolni, így javarészt az OrderControlleren keresztül már módosíthatóak kényelmesen.
    - SQL lekérdezéseket kellett bevezetni + PostgreSQL adatbázist választottam a tervezett H2 helyett, az adatok perzisztálásáház
    - DTO-kat (Data Transfer Object) bevezettem a rendszer megkímélésének céljából.
    - Entitások kapcsolatai minimálisan megváltoztak a kezdeti tervekhez képest.
    
## Others - ENG:
### Usage:
 The Application is a REST API, there is no UI to use. To use, you will need to use the Dedicated Client or a REST client like ARC or POSTMAN. 

### Documentation + Client:
 The official 73 pages long documentation can be requested at me if required.
 Official Client: [SupplyManagerSystemClient](https://github.com/BSSB33/supplyManagerSystemClient)

### Compile and Running
 Spring application!
 Use Intellij IDEA for opening and developing this `MAVEN` project. Run the main method inside the package com.elte.SupplyManagerSystemApplication file
 Generate a WAR file with `Java 11` via IntelliJ or run the following code from the console: `mvn deploy -DskipTests`
 Deployment: Install and Run Tomcat server with `Java 11`, place the WAR file inside the `webapps` folder and use the endpoints given below.
 Generate JavaDOC with this command: `mvn javadoc:javadoc`
 
### JSON example for POST order endpoint:
```json
{
    "id": 1,
    "productName": "Intel Core I5 6550 Processor",
    "price": 50000.0,
    "status": "IN_STOCK",
    "buyer": {
        "id": 3
    },
    "buyerManager": {
        "id": 6
    },
    "seller": {
        "id": 1
    },
    "sellerManager": {
        "id": 5
    },
    "createdAt": "2020-11-08",
    "modifiedAt": "2020-11-08",
    "description": "A new processor for the Office computer.",
    "archived": false
}
```

### Database:
>PostgreSQL DB:
> dbName = supplydb
> tables: USER_TABLE, ORDER_TABLE, COMPANY_TABLE, HISTORY_TABLE 

### Create test users:
>Edit import.sql file or preupload db, with bcrypt encoded passwords

### Endpoints:
- UserController:
    - [x] [GET] get all the users : `/users`
    - [x] [GET] user by ID : `/users/{id}`
    - [x] [GET] get non director users : `/users/freeDirectors`
    - [x] [POST] register new user : `/users/register`
    - [x] [POST] login with a user : `/login`
    - [x] [PUT] update user by id : `/users/{id}`
    - [X] [PUT] enable user by id : `/users/{id}/enable`
    - [X] [PUT] disable user by id : `/users/{id}/disable`
    - [x] [DELETE] delete user by id : `/users/{id}`
    
- OrderController:
    - [x] [GET] get all the orders : `/orders`
    - [x] [GET] order by ID : `/orders/{id}`
    - [x] [GET] get sales (order) of the company the User works at : `/orders/sales`
    - [x] [GET] get purchases (order) of the company the User works at : `/orders/purchases`
    - [x] [GET] get histories of order by order ID : `/orders/{id}/histories`
    - [x] [POST] add new order : `/orders`
    - [x] [POST] add history to order by order ID : `/orders/{id}/histories`
    - [x] [PUT] update order by ID : `/orders/{id}`
    - [x] [DELETE] delete order by ID : `/orders/{id}`
    - [x] [GET] get monthly income of the company of the logged in user : `/stats/monthlyIncome`
    - [x] [GET] get monthly expanse of the company of the logged in user : `/stats/monthlyExpense`
    - [x] [GET] get partner related stats of the company of the logged in user : `/stats/partnersStat`
    - [x] [GET] get order counts in the data base : `/stats/orderCount`
    - [x] [GET] get user counts by role in the data base : `/stats/userCount`
    
- CompanyController:
    - [x] [GET] get all the companies : `/companies`
    - [x] [GET] get company by ID : `/companies/{id}`
    - [x] [GET] get company of the User : `/companies/mycompany`
    - [x] [PUT] update company by ID : `/companies/{id}`
    - [x] [PUT] enable company by ID : `/companies/{id}/enable`
    - [x] [PUT] disable company by ID : `/companies/{id}/disable`
    - [x] [POST] register new company : `/companies/register`
    - [x] [DELETE] delete company by ID : `/companies/{id}`
    
- HistoryController:
    - [x] [GET] get all the histories : `/histories`
    - [x] [GET] get history by ID : `/histories/{id}`
    - [x] [POST] register new history : `/histories`
    - [x] [DELETE] delete history by ID : `/histories/{id}`
    
- DefaultPageController:
    - [x] [GET] get feedback by running : `/`
    
    
### Additional Data:
 [JavaDOC](https://github.com/BSSB33/supplyManagerSystem/tree/release/RELEASE-1.0.0/docs)  
 [Tests](https://github.com/BSSB33/supplyManagerSystem/tree/release/RELEASE-1.0.0/docs/TestResults)
 
#### UML with Entities:
 
![UMLDiagram](https://github.com/BSSB33/supplyManagerSystem/blob/release/RELEASE-1.0.0/docs/UML/EntitiesUML.png "UML with Entities")

### Test Descriptions:
> Tests were created with MockMVC.
> The given tests work with the provided example database set. (import.sql)
    
