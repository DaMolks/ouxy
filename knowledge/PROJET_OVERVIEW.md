# Ouxy - Application Modulaire Android

## Aperçu Général

### Modèles de Données
- `Technician` : Profil du technicien
  - Attributs : nom, prénom, secteur, identifiant, chef
- `Site` : Informations du site
  - Attributs : nom, adresse, code S, client
- `Report` : Rapport d'intervention
  - Sections modulaires personnalisables
- `TimeEntry` : Suivi des temps

### Architecture Technique
- Langage : Kotlin
- Pattern : MVVM (Model-View-ViewModel)
- Persistance : Room Database
- Navigation : Navigation Component
- Asynchrone : Coroutines

### Fonctionnalités
- Gestion des sites d'intervention
- Création de rapports personnalisables
- Profil technicien
- Export PDF (prévu)
- Synchronisation Google Drive (prévu)

### Configuration Requise
- Android Studio Hedgehog ou supérieur
- SDK Android 34
- Gradle 8.2.1
- JDK 17

### État Actuel
- Base de données configurée
- Navigation de base implémentée
- Interface utilisateur initiale
- Modules principaux en développement