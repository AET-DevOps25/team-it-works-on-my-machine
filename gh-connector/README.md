Steps to run this:

1: create `src/main/java/resources/application-secret.properties` file and fill with secret key
```
oauth.client-id=<your-oauth-client-id>
oauth.client-secret=<your-oauth-client-secret>
```
Use this guide to generate these values: https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/creating-an-oauth-app

2: Run directly with `./gradlew build bootRun`

OR

3: Build the docker image

```bash
docker build -t gh-connector-image gh-connector
```

Run the docker image

```bash
docker run -p 8589:8589 gh-connector-image
```