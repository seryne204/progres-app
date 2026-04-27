# Application Universitaire Inspiree de PROGRES

## 1. Presentation generale

Ce projet a ete realise dans le cadre d'un travail pratique de programmation orientee objet. Il s'agit d'une application de bureau developpee en Java Swing et inspiree du portail universitaire PROGRES utilise dans l'enseignement superieur.

L'objectif principal est de proposer une interface simple permettant de representer deux profils d'utilisateurs :

- l'etudiant, qui consulte ses informations pedagogiques ;
- le professeur, qui consulte ses sections, son emploi du temps et saisit les notes des etudiants.

## 2. Objectifs pedagogiques

Ce projet permet de mettre en pratique plusieurs notions importantes de la programmation orientee objet :

- la modelisation par classes ;
- l'encapsulation des donnees ;
- la separation entre la couche metier, la couche de persistance et la couche interface ;
- la reutilisation des objets ;
- l'organisation d'une application en packages.

## 3. Fonctionnalites de l'application

### 3.1 Espace etudiant

- authentification par identifiant et mot de passe ;
- affichage du tableau de bord ;
- consultation des notes ;
- consultation de l'emploi du temps ;
- consultation des absences ;
- deconnexion pour revenir a la page de connexion.

### 3.2 Espace professeur

- authentification par identifiant et mot de passe ;
- affichage des sections affectees ;
- consultation de l'emploi du temps du professeur ;
- saisie et modification des notes ;
- selection de l'etudiant a partir d'une liste deroulante ;
- restriction de la modification aux seuls modules enseignes par le professeur ;
- deconnexion pour revenir a la page de connexion.

## 4. Comptes de demonstration

### 4.1 Comptes etudiants

- `2023-INFO-1452` / `etudiant123`
- `2023-INFO-1789` / `sara123`

### 4.2 Compte professeur

- `PROF-INFO-01` / `prof123`

## 5. Architecture du projet

Le projet est structure selon plusieurs packages :

- `progresapp.model` : contient les classes metier ;
- `progresapp.service` : contient la logique de gestion et la sauvegarde ;
- `progresapp.ui` : contient les interfaces graphiques ;
- `progresapp` : contient la classe principale de lancement.

## 6. Description des classes principales

### 6.1 Classes metier

- `Student` : represente un etudiant ;
- `Professor` : represente un professeur ;
- `Course` : represente un module ;
- `Grade` : represente une note associee a un module ;
- `ScheduleEntry` : represente une seance dans l'emploi du temps de l'etudiant ;
- `ProfessorScheduleEntry` : represente une seance dans l'emploi du temps du professeur ;
- `Absence` : represente une absence ;
- `StudentRecord` : regroupe toutes les donnees d'un etudiant ;
- `ProfessorRecord` : regroupe les donnees du professeur.

### 6.2 Classes de service

- `UniversityService` : gere l'authentification, l'acces aux donnees, le calcul de la moyenne, les credits et la mise a jour des notes ;
- `DataStore` : assure la lecture et l'ecriture des donnees dans un fichier texte local.

### 6.3 Classes d'interface graphique

- `LoginFrame` : fenetre de connexion ;
- `ProgresDashboard` : espace etudiant ;
- `ProfessorDashboard` : espace professeur.

## 7. Persistance des donnees

Les donnees des etudiants sont sauvegardees dans le fichier suivant :

`data/student-data.txt`

Ce fichier contient les informations necessaires au chargement des comptes etudiants ainsi que leurs notes, emplois du temps et absences.

## 8. Compilation et execution

Depuis le dossier du projet, utiliser les commandes suivantes :

```bash
javac -d out src/progresapp/Main.java src/progresapp/model/*.java src/progresapp/service/*.java src/progresapp/ui/*.java
java -cp out progresapp.Main
```

## 9. Resultats obtenus

L'application finale permet :

- la simulation d'un mini portail universitaire ;
- la gestion de deux roles distincts ;
- l'utilisation de plusieurs classes objet specialisees ;
- la consultation et la modification de donnees dans une interface graphique ;
- la sauvegarde locale des notes modifiees.

## 10. Perspectives d'amelioration

Parmi les evolutions possibles :

- ajouter plusieurs professeurs ;
- ajouter plusieurs sections avec de vrais groupes d'etudiants ;
- connecter l'application a une base de donnees ;
- ajouter la gestion des examens, des releves de notes et des documents administratifs ;
- generer automatiquement des statistiques pedagogiques.
