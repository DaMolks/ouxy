# Structure d'un Module Ouxy

## Vue d'ensemble
Un module Ouxy est un repository GitHub contenant les fichiers suivants :

```
├── ouxy-module.json     # Manifest du module
├── module/              # Code source du module
│   └── build/           # Fichiers compilés
│       ├── module.jar     # Code du module
│       └── assets/       # Ressources optionnelles
├── screenshots/         # Screenshots pour le marketplace
├── LICENSE              # License du module
└── README.md            # Documentation du module
```

## Manifest (ouxy-module.json)
Le manifest définit les métadonnées du module :

```json
{
    "id": "module-id",               # Identifiant unique du module
    "name": "Nom du Module",         # Nom affiché dans l'interface
    "description": "Description",    # Description courte
    "version": "1.0.0",             # Version du module
    "minAppVersion": "1.0.0",       # Version minimum d'Ouxy requise
    "author": "NomAuteur",          # Auteur du module
    "entry": "ModuleClass",         # Point d'entrée du module
    "permissions": [                 # Permissions requises
        "STORAGE",
        "CAMERA"
    ],
    "dependencies": {                # Dépendances optionnelles
        "other-module": "^1.0.0"
    },
    "screenshots": [                 # Screenshots pour le marketplace
        "screenshots/screen1.png",
        "screenshots/screen2.png"
    ]
}
```

## Structure du Code Source
Le module doit fournir une classe qui implémente l'interface `OuxyModule` :

```kotlin
interface OuxyModule {
    // Appelé lors de l'initialisation du module
    fun initialize(context: ModuleContext)
    
    // Appelé pour obtenir la vue principale du module
    fun getMainView(): View
    
    // Appelé lors de la désinstallation
    fun cleanup()
}
```

## Conventions de Nommage
- ID du module : kebab-case (ex: "inventory-module")
- Nom de la classe principale : PascalCase (ex: "InventoryModule")
- Ressources : snake_case

## Configuration Gradle Requise
```gradle
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'ouxy.module'  // Plugin Ouxy pour les modules
}

ouxyModule {
    id = 'module-id'
    mainClass = 'com.example.ModuleClass'
}
```

## Validation
Lors de l'installation, Ouxy vérifie :
1. La présence et validité du manifest
2. La présence des fichiers requis
3. La compatibilité de version
4. Les dépendances requises

## Configuration Android Studio
Pour le développement, configurer :
1. Module Android Library
2. Dépendance sur le SDK Ouxy
3. Packaging en JAR pour distribution