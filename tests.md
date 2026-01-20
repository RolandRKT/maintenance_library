# Identifier les points critiques (ajout, emprunt, recherche)

En se basant sur ce qui avait été analysé lors du TD précédent, j'ai pu identifier les points suivants selon les mots clés donnés :

Pour l'ajout :
L'ajout de livre ne tient pas compte des doublons et ne renvoie qu'un warning tout en ajoutant le livre.

Pour l'emprunt je relève quelque chose qui est en lien (même si ce n'est pas uniquement en rapport avec l'emprunt) :
Il n'y a aucune validation des entrées vides. En entrée, on peut mettre des "" que ça soit pour l'ISBN ou le nom de l'emprunteur.

Pour la recherche :
La recherche par auteur est sensible à la casse donc si il y a un oublie de majuscule de tiret ou d'un caractère spécial, l'auteur n'est pas trouvé.

# Proposer une stratégie de non-régression pour les fonctions critiques

La stratégie de non-régression repose sur l'exécution systématique de tous les cas 
de tests après chaque modification du code (correction, ajout de fonctionnalité, refactoring). Cette approche permet de vérifier que les modifications n'ont pas affecté les parties déjà testées.

## Approche combinée Black box / White box

Le plan de tests proposé combine deux approches complémentaires :

**Tests en boîte noire** : Tests basés sur les spécifications, sans 
connaissance du code interne. Exemple : tester l'ajout d'un livre avec ISBN dupliqué, ou vide etc... (ref au tableau de tests) 
doit renvoyer une erreur ou confirmation selon le cas testé.

**Tests en boîte blanche** : Tests basés sur la structure du code pour 
s'assurer que toutes les branches (if, while, for) sont couvertes. Exemple : s'assurer 
que la boucle de recherche dans `Library.byIsbn()` est testée avec un livre existant 
ET inexistant (tests inclu dans le tableau des tests dans le tableau de test).

Pour cette partie, la stratégie reposerait sur le principe de black box et de white box.
Donc effectuer des tests qui joue uniquement sur les valeurs d'entrées (black box), et enfin des tests qui s'assurent de passer dans toutes les boucles (if, while, for...) et que les tests passent.

L'idée c'est que ces tests soient faits à chaque ajout de nouvelle fonctionnalité (ou push/merge si on met en place un git).

A noter que c'est avec ce principe que le tableau de tests a été effectué. En prenant cette démarche on s'assure que la modification ou évolution de code ne fasse pas régresser ce qui passait déjà les tests.

C'est en se basant sur ce principe que le tableau de test a été fait.

Cette stratégie garantit que l'évolution du code ne dégrade pas les fonctionnalités 
existantes, facilitant ainsi la maintenance de l'application.

## Mise en œuvre pratique

on peut faire un stockage des tests en mettant tous les cas de tests (entrées + résultats attendus) stocké pour être réutilisables. Comme on utilise maven (vu dans le pom.xml) on peut utiliser Junit et Maven pour exécuter les tests, et donc faire une automatisation.
Et enfin nous pouvons relancer tous les tests à chaque modification pour détecter les réegressions.
