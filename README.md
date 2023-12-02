# Cardgame CardIt
---
---
## Installation

Benötigt:
- Maven
- Postgresql

Das Backend wahlweise von Hand herunterladen und mit der IDE der Wahl öffnen,
oder falls unterstützt mit der IDE direkt über die Versionskontrolle beziehen. 
Bei IntelliJ “Get from Version Controll” genannt.

Es muss eine neue Postgresql Datenbank angelegt werden mit dem Namen **carditDB**.
Zudem muss ein neuer Nutzer angelegt werden, welcher mit der Datenbank interagiert. 
Der Nutzer hat den Namen **cardit** mit dem Passwort **password**.
Der Owner der Datenbank **carditDB** wird nun noch auf den Nutzer **cardit** gesetzt.



In IntelliJ kann die Datenbank auch direkt eingepflegt werden.
Hierzu wird unter dem Reiter **Database** eine neue Datenbank hinzugefügt via New->Data Source->PostgreSQL

In dem nun offenen Fenster sind nun die Daten der Datenbank einzutragen:

Name: carditDB@localhost  
Host: localhost  
Port: 5432  
Authentification: User&Password  
User: cardit  
Password: password  
Save: Forever  
Database: carditDB  

Das Backend sollte nun zur Ausführung bereit sein.
