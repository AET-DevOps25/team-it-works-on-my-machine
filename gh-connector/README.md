Steps to run this:
1: create `src/main/java/resources/application-secret.properties` file and fill with secret key
```
oauth.client-secret=your-client-secret
oauth.client-id=your-client-id
```
2. run with `./gradlew build bootRun`