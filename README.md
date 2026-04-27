# Systeme de Gestion des Etudiants

## 1. Presentation du projet

Ce projet a ete realise dans le cadre du module de Programmation Orientee Objet. Il s'agit d'une application Java Swing inspiree du sujet "Systeme de Gestion des Etudiants" destine a illustrer la gestion du parcours universitaire d'un etudiant.

L'application prend en charge :

- la gestion des etudiants ;
- la gestion des enseignants ;
- la gestion des modules ;
- l'inscription des etudiants dans les modules ;
- la saisie des notes de controle continu, d'examen et de rattrapage ;
- le calcul des moyennes avant et apres rattrapage ;
- l'identification des etudiants en rattrapage ;
- la determination du statut final `Diplome / Non diplome`.

## 2. Technologies utilisees

- Java 17
- Java Swing pour l'interface graphique
- Organisation orientee objet par packages
- Script SQL Server fourni dans `database/schema.sql`

## 3. Structure du projet

- `src/progresapp/model` : classes metier
- `src/progresapp/service` : logique metier et persistance locale
- `src/progresapp/ui` : interfaces graphiques
- `docs/conception.md` : UML et modele logique de donnees
- `database/schema.sql` : script de creation de la base SQL Server

## 4. Repartition des roles

### 4.1 Administrateur

L'administrateur peut :

- ajouter, supprimer et modifier un etudiant ;
- ajouter, supprimer et modifier un module ;
- ajouter, supprimer et modifier un enseignant ;
- inscrire un etudiant dans un ou plusieurs modules ;
- affecter les modules aux enseignants ;
- consulter la liste finale des etudiants diplomes apres rattrapage ;
- afficher le statut final de chaque etudiant ;
- visualiser les modules bloquants pour les etudiants non diplomes.

### 4.2 Enseignant

L'enseignant peut :

- consulter les modules qui lui sont affectes ;
- voir les etudiants inscrits dans chacun de ses modules ;
- saisir les notes de controle continu, d'examen et de rattrapage ;
- consulter uniquement la liste des etudiants en rattrapage dans ses propres modules.

### 4.3 Etudiant

L'etudiant peut :

- consulter son emploi du temps a partir des modules auxquels il est inscrit ;
- voir ses notes avant rattrapage ;
- voir ses notes apres rattrapage ;
- consulter sa moyenne generale ;
- voir son statut final `Diplome / Non diplome` ;
- voir les modules bloquants si son parcours n'est pas valide.

## 5. Regles de gestion

- un etudiant peut etre inscrit dans plusieurs modules ;
- une seule inscription est autorisee par etudiant et par module ;
- chaque note appartient a un seul etudiant et un seul module ;
- la moyenne avant rattrapage est calculee par :

```text
Moyenne = (CC * 40 + Examen * 60) / 100
```

- si la moyenne est inferieure a 10, le module passe en rattrapage ;
- apres rattrapage, la meilleure note est retenue ;
- un etudiant est considere `Diplome` seulement si tous les modules auxquels il est inscrit sont valides ;
- sinon, il est `Non diplome` et les modules restants sont affiches comme modules bloquants.

## 6. Comptes de demonstration

### Etudiants

- `2023-INFO-1452` / `etudiant123`
- `2023-INFO-1789` / `sara123`

### Enseignants

- `PROF-INFO-01` / `prof123`
- `PROF-INFO-02` / `prof456`

### Administrateur

- `ADMIN-01` / `admin123`

## 7. Fonctionnalites principales deja implementees

- authentification multi-roles
- ajout, modification et suppression des entites principales
- inscription d'etudiants dans les modules
- affectation des modules aux enseignants
- saisie des notes pedagogiques
- affichage des etudiants en rattrapage
- affichage de la liste finale des diplomes
- affichage du statut final et des blocages

## 8. Execution du projet

Compiler :

```bash
javac -d out src/progresapp/Main.java src/progresapp/model/*.java src/progresapp/service/*.java src/progresapp/ui/*.java
```

Lancer :

```bash
java -cp out progresapp.Main
```

## 9. Contenu academique pour le rapport

Le projet contient deja des elements utiles pour le rapport :

- une structure orientee objet claire ;
- une separation entre modele, service et interface ;
- un document de conception UML / MLD ;
- un script de base de donnees SQL Server.

Fichiers utiles :

- [docs/conception.md](</C:/Users/hp/OneDrive/Documents/New project/docs/conception.md>)
- [database/schema.sql](</C:/Users/hp/OneDrive/Documents/New project/database/schema.sql>)

## 10. Limites actuelles

- l'application utilise encore une persistance locale Java pour la demonstration ;
- la base SQL Server est preparee, mais pas encore branchee directement a l'application via JDBC.

## 11. Perspectives d'amelioration

- connecter l'application a SQL Server avec JDBC ;
- ajouter la recherche et le filtrage dans les tableaux ;
- renforcer la validation des formulaires ;
- generer un rapport ou un releve de notes exportable ;
- ajouter la gestion de plusieurs promotions et sections.
