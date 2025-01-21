# GDriveConsumerService
[Backend] Demo application using Google Drive API and OAuth2

## Prerequisites

- A Google Cloud account with a project ideally called `GDriveConsumerAPI` [check console here](https://console.cloud.google.com/). (else a reference update will be needed in application.properties)
  - Enable Google Drive API for the created project.
  - Create a new OAuth Client Id credentials, provide Google Drive permissions and Oauth Consent screen
    - For OAuth consent screen add restricted scope `./auth/drive` `See, edit, create, and delete all of your Google Drive files`
    - For Credentials select WebApplication and on Authorized redirect URIs add`http://localhost:8080/oauth`
  - Download the OAuth client file and store it under `src/main/resources/keys` as `gdrive_client.json`.

## Run Steps
- The project provides options to automatically build, run tests, run jacoco verifications and finally run the application.
- `./gradlew assemble` builds the project.
- `./gradlew test` runs the test suites.
- `./gradlew jacocoTestCoverageVerification` run the test report and checks code coverage.
- `./gradlew runApp` runs all of the above and starts the appplication if code coverage check passes.
- `./gradlew integrationTest` runs the integration tests, the app must be running for them to work, and due to some limitations 
the account must be logged in already. See GoogleOAuthIntegrationTest for more detail

## After run Steps

- Enter [http:/localhost:8080/](http:/localhost:8080/) to start navigation on the WebApplication. 


## Work Breakdown:
* `01/15` - Initial Project setup. Gathering of requirements, build of this document, UI mock document and Infrastructure considerations Document. - `4 hours.`
* `01/16` - Initial Implementations. Google Cloud Application Setup. Features Implementation for Listing and Create File operations. - `4 hours.`
* `01/17` - Finalize implementations Download Delete and Upload File - `4 hours.`
* `01/19-20` - Added Unit Tests, minor refactoring adding Jacoco code coverage check. - `8 hours`
* `01/20` - Updated UI to adhere more to the conceptual UI mock presented. - `4 hours`
* `01/21` - Added integration tests - `4 hours`

## Project Limitations:
This is a non-extensive list of elements that were left in the "backlog" considering there was not a set deadline for the project

* This project is single user, I implemented an static `USER_IDENTIFIER_KEY` that helps to save the user token inside the server as a file.
  * To improve this there are many options:
    * One option is to setup a database option (SQL or NoSQL) to store a record per user and generating dynamic identifiers and persist them along with their current tokens.
    * Another can be use session based authentication, like JWT, to store the credentials in the session.
    * Others involve using cloud storage options
    * And redis cache like options along with database option.
* GoogleDriveComponent could be further breakdown per operation into different components to reduce the amount of lines in the class and allow future enhancements.
* HomepageController could also be breakdown per mappings if required to enforce single responsibility principle.
* Basic UI could use notifications when uploading/downloading, pop ups for deletion confirmation (as shown in mock UI) and overall improve in layout

## Other Documents
At the root of this project I've added my version of the requirements as well as the MockUp of the proposed UI
* [UI Mock] Google Drive Consumer [Not final].pdf
* Application GoogleDrive Consumer.pdf
* ApplicationDemo.ver.1.mov
