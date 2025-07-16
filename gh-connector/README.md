Steps to run this directly on your machine:

### 1. Setup GH-Connector properties file

gradle is not able to read from a `.env` files. That is why we need to create another file, that contains the secred variables.
The file must be placed at `gh-connector/src/main/java/resources/application-secret.properties`.

```bash
oauth.client-id=<your-oauth-client-id>
oauth.client-secret=<your-oauth-client-secret>
```

### 2: Run it
Using this command
```
./gradlew build bootRun
```