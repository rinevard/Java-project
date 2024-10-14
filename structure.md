```
protein-sequence-management/
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── FileUpload.vue
│   │   │   ├── DataTable.vue
│   │   │   └── SearchBar.vue
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vue.config.js
│
backend/
    ├── src/
    │   └── main/
    │       ├── java/
    │       │   └── com/
    │       │       └── protein/
    │       │           ├── controller/
    │       │           │   └── SequenceController.java
    │       │           ├── data/
    │       │           │   ├── Sequence.java
    │       │           │   └── SequenceRepository.java
    │       │           ├── parser/
    │       │           │   └── SequenceParser.java
    │       │           └── ProteinSequenceManagementApplication.java
    │       └── resources/
    │           └── application.properties
    ├── pom.xml
    └── sequences.db
```
