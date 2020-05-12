# SupplyManagerSystem - SMS - BackEnd - 1.0.0
A Projekt az ELTE IK - PTI BSc képzéshez készült szakdolgozatnak.

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

**Bővítési lehetőségek:**
	-A megrendelések több terméket is tartalmazhatnak egységárral
	-A több országban működő cégekneknek választható nyelv bevezetése felhasználónként. (EUR/HUF valuták bevezetése megrendelésekre)
	-Értesítések bevezetése
	-Útvonaltervezés implementálása
	-Képek, egyéb dokumentumok csatolása megrendelésekhez, előzményekhez.
	-Havi Jelentés generálása funkció implementálása
	-Email hitelesítés implementálása majd email címmel való belépés engedélyezése.    
    -HTTPS bevezetése
    
    
**Változtatások a fejlesztés során:**
    - Felhasználók csak a saját cégük által kibocsátott előzményeket tekinthetik meg ezért hozzárendelésre került a Creator (Készítő) mező minden előzményhez.
    - History endpointokat át kellett gondolni, így javarészt az OrderControlleren keresztül már módosíthatóak kényelmesen.
    - SQL lekérdezéseket kellett bevezetni + PostgreSQL adatbázist választottam a tervezett H2 helyett, az adatok perzisztálásáház
    - DTO-kat (Data Transfer Object) bevezettem a rendszer megkímélésének céljából.
    - Entitások kapcsolatai minimálisan megváltoztak a kezdeti tervekhez képest.
    
## More - ENG:

### Usage:
 The Application is a REST API, there is no UI to use. To use you will need to use the Dedicated Client or a REST client like ARC or POSTMAN. 
 Client: [SupplyManagerSystemClient](https://github.com/BSSB33/supplyManagerSystemClient)

### Compile and Running
 Use Intellij IDEA for opening and developing this `MAVEN` project. Run the main method inside the package com.elte.SupplyManagerSystemApplication file
 Generate a WAR file with `Java 11` via IntelliJ or run the following code form the console: `mvn deploy -DskipTests`
 Deployment: Install and Run Tomcat server with `Java 11`, place the WAR file inside the `webapps` folder and use the endpoints given below.
 Generate JavaDOC with this command: `mvn javadoc:javadoc`
 
### JSON examples for PUT and POST endpoints:
> TODO

### Database:
>PostgreSQL DB:
> dbName = supplydb
> tables: USER_TABLE, ORDER_TABLE, COMPANY_TABLE, HISTORY_TABLE 

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
	- TODO statistics endpoints
    
- CompanyController:
    - [x] [GET] get all the companies : `/companies`
    - [x] [GET] get company by ID : `/companies/{id}`
    - [x] [GET] get company of the User : `/companies/mycompany`
    - [x] [PUT] update company by ID : `/companies/{id}`
    - [x] [POST] register new company : `/companies/register`
    - [x] [DELETE] delete company by ID : `/companies/{id}`
    
- HistoryController:
    - [x] [GET] get all the histories : `/histories`
    - [x] [GET] get history by ID : `/histories/{id}`
    - [x] [POST] register new history : `/histories`
    - [x] [DELETE] delete history by ID : `/histories/{id}`
    
- DefaultPageController:
    - [x] [GET] get feedback of running : `/`
    
    
### Additional Data:
 [JavaDOC](https://github.com/BSSB33/supplyManagerSystem/tree/release/RELEASE-1.0.0/docs)  
 [Tests](https://github.com/BSSB33/supplyManagerSystem/tree/release/RELEASE-1.0.0/docs/TestResults)
 
#### UML with Entities:
 
![UMLDiagram](https://github.com/BSSB33/supplyManagerSystem/blob/release/RELEASE-1.0.0/docs/UML/EntitiesUML.png "UML with Entities")



### Test Descriptions:
> TODO
    
