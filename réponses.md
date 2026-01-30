==============================================
Analyse des responsabilités de chaque classe :
==============================================

Book :
Classe contenant toutes les informations relatives à un livre, nous avons de stocké
son isbn, son titre, l'auteur sous forme de string et enfin l'année.
Nous avons donc ici des champs simple sans couplage avec d'autres classes.
La classe livre peut donc exister indépendamment des autres.
Elle contient un constructeur simple avec des getteurs et un toString.

Borrower :
Cette classe contient uniquement le nom de l'emprunteur. Nous pouvons créer un objet Borrower
mais pas le lire ni le modifier via une méthode car cette classe expose publiquement son attribut
name sans getter/setter, ce qui permet sa modification directe. Cette classe a donc pour simple
responsabilité de stocker le nom d'un emprunteur.

Library :
Cette classe, comme son nom l'indique est censé représenter une librairie. Elle contient donc
une liste de livre et un dictionnaire avec comme clé le isbn du livre et en valeur un emprunteur.
On remarque que les attributs sont statiques et publiques.
Nous pouvons rechercher un livre à condition d'avoir son isbn et savoir si un livre est emprunté,
sans pour autant de méthode dédiée à l'emprunt.
La classe a donc pour responsabilité de stocker les livres d'une librairie, et à savoir si un livre est emprunté
ou non.

LibraryApp :
LibraryApp est une classe exécutable. Sa responsabilité première est celle d'exécuter l'interface de l'application 
(interaction homme-machine). Mais on voit que cette responsabilité principale devient floue car elle mêle la logique
métier censé être attribué à la librairie (par exemple ajouter un livre) avec sa responsabilité principale.

=============================
Analyse diagramme de classe :
=============================

On commence par analyser le couplage dans LibrabryApp.
Cette classe est fortement couplée à Library, Book et Borrower car comme on peut le voir sur le diagramme avec 
les flèches en pointillés «use», elle utilise directement leurs attributs et méthodes.
Et évidemment l'application fonctionne, mais elle rend l'application difficile à modifier/maintenir car tout
changement dans Library, Book ou Borrower risque d’impacter le code de LibraryApp.
Donc on a ici un couplage très problématique.

Pour les dépendances, LibraryApp dépend de toutes les autres classes pour fonctionner. Il agit non seulement en 
tant que point d'entrée (interface) et centralise beaucoup la logique métier des autres classes.
Library dépend de Book et Borrower via les collections books: List<Book> et dictionnaire loans: Map<String, Borrower>, ce qui fait que toutes les données de
l'application se trouve dans cette classe.
Un point aussi qui est positif c'est que les dépendances ne vont que dans un sens Book et Borrower ne connaissent pas Library, 
ça évite les boucles de dépendances.

Pour le principe d’encapsulation, Book le respecte bien car les attributs sont privés et accessibles uniquement via des getters.
En revanche dans Library, les attributs books et loans sont publics et statiques ce qui signifie que n’importe quelle classe peut 
les modifier directement, ce qui brise l’encapsulation et crée un risque d’incohérence des données (si tout le monde le modifie).
Et dans Borrower, l’attribut name est public. Pour respecter l'encapsulation il faudrait faire comme pour Book et mettre l'attribut
privé avec un getter pour respecter l’encapsulation.

==============================
Dette technique dans le code :
==============================    

Comme nous les avons un peu énoncé précédemment, la dette technique se trouve dans plusieurs points déjà énoncé.
Les attributs statiques dans Library (books, loans) laisse n'importe qui modifier directement les collections, ce 
qui crée un risque d'incohérence.
​
Nous avons aussi évoqué l'attribut name qui est publique et ne respecte pas l'encapsulation. Ce qui rend le code
sensible aux modifications.

Et enfin, la logique métier dans LibrabryApp. Comme dit précedemment, il gère la logique métier de Library. Toute la 
validation passe par l'interface au lieu d'être dans Library ce qui rend le code impossible à réutiliser et très compliqué
à maintenir et même à faire évoluer.
On peut aussi noter l'absence de gestion d'erreurs, surtout celle avec l'année du livre. Il n'y a pas de try-catch autour de
l'opération (parsing d'année), juste un defaulting to 0.

============================================================
3 risques majeurs pour la maintenabilité + recommandations :
============================================================

-------------------------------------------------------------
1. Les données de Library sont accessibles par tout le monde.
-------------------------------------------------------------
Le risque c'est que les attributs books et loans de la classe Library sont publics et statiques, ce qui permet à n'importe quelle partie
du code de faire par exemple Library.books.clear() ou Library.loans.put() directement. Cela peut causer des pertes de données et des 
bugs/incohérence difficiles à tracer.

Ce qu'il faudrait revoir :

    - Privatiser books et loans : private static List<Book> books
    - Créer des méthodes d'accès contrôlées, donc un CRUD complet pour les deux classes selon les besoins.

------------------------------------------------
​2. Logique métier non centralisée (mal répartie)
------------------------------------------------

La logique métier (validation de doublons, parsing d'année, vérification des emprunts) est écrite directement dans LibraryApp au lieu d'être 
dans Library. ça empêche la réutilisation du code, augmente le risque d'incohérences si d'autres points d'entrée sont ajoutés et rend donc les 
modifications complexes car il faut chercher la logique partout et s'assurer de ne rien oublié.
​

Ce qu'il faudrait revoir/ajouter :

    - Déplacer toute la logique métier dans Library : library.addBook(book) doit gérer la validation des doublons tout seul
    - Créer des méthodes métier propres : borrowBook(isbn, borrowerName) qui encapsule toutes les vérifications
    - LibraryApp doit uniquement gérer l'interface utilisateur (Scanner, System.out) et déléguer à Library
    - Ajouter une gestion d'erreurs avec des exceptions personnalisées (possible en java) : BookAlreadyBorrowedException,
    BookNotFoundException

---------------------------------------
3. Couplage fort et dépendances rigides
---------------------------------------

Comme on l'a vu, LibraryApp dépend directement de toutes les autres classes (Library, Book, Borrower) et manipule directement leurs structures. 
Les flèches <<use>> sur le diagramme montrent ce couplage. Si on veut changer la façon dont les emprunts sont stockés (par exemple passer d'une 
Map à une table en base de données), on devra modifier à la fois Library et LibraryApp car LibraryApp accède directement à Library.loans.containsKey(). 
Cela rend chaque évolution risquée et coûteuse en temps.

Ce qu'il faudrait revoir :

    - Créer une interface LibraryService qui définit les opérations métier (addBook, borrowBook, returnBook, listBooks)
    - Library implémente cette interface, LibraryApp ne connaît que l'interface

​ainsi, LibraryApp n'accéde jamais directement aux collections de Library, seulement via les méthodes publiques
Cela permet de changer l'implémentation de Library (vers une base de données par exemple) sans toucher à LibraryApp.
​
---------------------------------------
4. Nouvelle analyse après TD2B
---------------------------------------

## Tests de non-régression - Détection de bugs

### Coverage obtenu
- **Library** : 100% (46 instructions)
- **Book** : 100% (11 instructions)
- **Borrower** : 100% (6 instructions)
- **Total** : 48% (237/462 instructions)
- **LibraryApp** : 0% (interface console non testée)

**Conclusion** : Les tests de non-régression détectent efficacement les bugs.
