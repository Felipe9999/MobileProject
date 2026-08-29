# Agent Instructions

- **Language Policy**: The project UI must be in Spanish, but all code and communication with the user MUST be in English.
- **Build**: Use `./gradlew` for building. Ensure a compatible JDK (17+) is set in your environment.
- **Project Structure**:
    - `app/`: Main Android application code.
    - `no_git/`: Holds design prototypes and additional project info; not part of the Android build.
- **Entry Points**: 
    - `MainActivity` is the main entry point defined in `AndroidManifest.xml`.
- **Workflow**: 
    - Always follow `lint` -> `typecheck` (if applicable) -> `test` conventions.
    - Check `gradle/libs.versions.toml` for dependency management.
