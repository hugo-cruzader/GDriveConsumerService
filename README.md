# GDriveConsumerService
[Backend] Demo application using Google Drive API and OAuth2

## Prerequisites

- A Google Cloud account with a project ideally called `GDriveConsumerAPI` [check console here](https://console.cloud.google.com/).
  - Enable Google Drive API for this project.
  - Create a new OAuth Client Id, provide Google Drive permissions and Oauth Consent screen
  - Download the created credentials and store them under `src/main/resources/keys` as `gdrive_client.json`.

## Run Steps
- The project provides options to automatically build, run tests, run jacoco verifications and finally run the application.
- `./gradlew assemble` builds the project.
- `./gradlew test` runs the test suites.
- `./gradlew jacocoTestCoverageVerification` run the test report and checks code coverage.
- `./gradlew runApp` runs all of the above and starts the appplication if code coverage check passes.  

## After run Steps

- Enter [http:/localhost:8080/](http:/localhost:8080/) to start navigation on the WebApplication. 
