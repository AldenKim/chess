# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared tests`     | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

### Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Chess Server Design/Sequence Diagram
- https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDAFIRJoDiwAtihdUIo6+QsWCk4UFMDApufAVSEiG40gBkkAZzAKU2pStyjGE5gCUUAcx1gospBDRGQwk2qYwNEWy8rKbqpiXj7WEACuYK7u9CHm8AgyUACC2NjIII7OMcFmpAAissApIG7a2pgAJsUARsDaKDBVtZiY1lCR2DAAxGjAVACeMFa2ug4Ezr0A7gAWSHKYiKikALQAfOQBlABcMADaAAoA8mQAKgC6MAD0EY1QADpoAN53lP18ADQwuBXT0FVvigeMAkAgAL6YJSUGAbEY2OwTJz+QRuPbSMZyKAACjeUA+KG+v20-yggJgwNBCAAlJhRojsijAk04UUwCUygZtHtrCgwABVe64+4E2lsjnlbSwzYtPZkACiGnlcDOMDxBJgADNOjw1fdMOLSpLYXD6eNGTE9mgIggEHSEebJkyhNKYIbORU9iBpLIUILKML3rxCT8GiSAWLikaua7ZTAAJIAOQVFlV6uDRLDpPJlLBCcTZ2OetoZvsFoCLtZUY93Jg3pkchSUVmgfxwcj7OjFVjtT2SZTaZFGZgwGbZwgAGt0PnCyPmwbq8bVqaHWWnZa52BZuOp2h7ZikTkK24TZtoVA9qOtzv0FDtrBl5tlugwHsAEwABg-T2eV+3k-QSF0CqNoOi6XpoAkXlvF8DgZnmRZnzWM97z2I5TkuG5GgqZEf3TL5Qz+AFIVAAgqF9LZqFoc9pTCDgN1mCQqiSOi0FbAlMyIslaVIpByLkGC-FyViYmld1JR5Pl-RxfCUB48A+Io8SuQXTsax7OVFWVQcgwI4lsy1HViygVSJRjR8ROPFA9mk8VqhkBT+KaZSKkwYAEGYaSYHlAAPOwpSYmBDizAEYAAWVkEBZgMTAYBHRylMXFS4ss1EUA2Fza3rX0my3dj21it0ku7OE437eVU2MjjN3-XcZyLP9Csy09BPoqzLzHAC9ziqoHLIxK1MlQrUuZFrz3fL88KHAi-xvNBITimjH0SFZXxgT9vxeWTvlmrqgIQRoYC8goIAMGBEwgZhfLsGBoCCkKyXCnQQTAKLhtg50T2W8aYAAFg-ABGJ5+X6ZtoCQAAvFAQMW+8WqQtb-qBtAQb-cGoZA4D7N4pzWs+4Repxijz0wLH2k6CJuh6DoUGnMJImYHo5gWYQEddH79gKLSznlK5rmw7RcNB68urchKBJJomBPpqJhN8BnRIy4ra2kHgIBoXKW123d5P6gTMrvKiH3WGXonamBGLQZiUFN7FtfQDszJKmVezdbn5RgAAxCxjjCmqYAAdQACQqj37bQGAAF4arm0yuylCz5dl83tAiGtsfF5zlcwU3FbhH6Np-GAACI+AqYBeWLvZi9NmBU5rTUbWLmBDZoB84QRibNueEuy+0CuUCrkva-ryVG4QZvIUJzPBIVqyM71poSbJsDKd6aQqm8G79ClJmENZ5AX3Z1CDk4eVMOuaxgyecOxcXyi29bmENi0XQd4YpiWLsHe7c6nW78UtLb+wZDDz1fnoEBit1iZUkmAFItof660AVnQaMY47qVKq7BUSoVQwCvnweMBRvjMzkNJaqtQECgAnGQ4c+CUCJmDF7H2fs6GVGahZYBZcNysLstPe+BtwHvysqeAuk0Xgl1YUPfYzxi50MIUPAGb4ADMv1vjFxIX6aag8djF2LmoihVCaF8CrrotRdCGHGJ0cXcEFxIRLQ7ofDAXci6yJAVImRciCgKOUaokuGijHaNMSXAxIBqFaJMXoiRwYLGBOsbYheyCt5v0gfPPhiTl5W1AhTKm2AIhQAyE0KQDYmj6HgizJYjjmD5xPuhc4fM6E3z-ugH85jgwkRniTJa6wim+n0B-K2SQelyH0L-EWu5vitL4Eg3GQz5DBlyLMvpwilaoM9HWH0wzgyjNqugCZ0SCpSxQU7NhysNL5gHPVPBICYDYn8VovZfAYnUhgAANRSBofk8oyA3KCZMuS6ClwbEWfM82niEm4wNsCxQyzqlG2cS8VxBCvF7EUSouxcNlqd3WmIjxwZ5Eop8VPPqiSoX8FScS3GGSQLk3Aj0QYKBbQQGmKwdgEdSk9BCROCpq1j5woOIcfkF8GnCx2WgH8CM4AQAQNAB5KBCHtPvp0jFmw2AcCWWlPYltraqq4Fs8O3wJVSplVcpF0yKI6vVcyTAFqQVpVdDAmAAArVlIz9UrRfJK6VUBZWELNfrbO7CXaaRwaqO5ukQycoCUw32JqzrB1DrGwhUdE0FABeZOENroUap+JQ+lUBtC8IpQNY5bkPIwFKddXQAUrbnUujAeBPoqjDDOMAXchVM1krtVWVZtZKY1E2XwbZc0DWVM9cazxtI4qHKKj2pqpzME2UOAUFIPNY1SgVKqAAPCAI1UB1hRujuHQOIcLAe08cmsFcV2EZpdba5kexR7JWaEW-1s64odrzihPlG1Cr2KfJUrupMDolMYSdAwaAADkV0-K6FurABtMgm0wBbW299t6s2jW+iff6H5rhIzwx+JRTwYA91LlyAeQ9i7yigJ0C8MB6ib2kAARwiAYMAtwRXo2htcdyjbhjsl3JPX9GKHGrT2DhgjAMCNEYjqRvuFHq7Udo3sBjMBmOsd0BxtGUBIbcd44h-jrb0BCaxmk3GH7yXTqpVk2lHRgDDEQMkEc6RMiMjKYhSpvK25oS5kqHmfMWgAMpfeJ+xtEjJDSBkQg5Zs1asGUkPAkXXNOmxH6wpCXUguei+ueejnEtZayDlrtmwHUgAy0l7LyJUtBeLfHNNztmiu18+fD23sY14nqwnINbs-OteYWuzrZzmurra37Rq17-0RYKzF+9dc05DTM7V9OeXMtRcK8iT9D9djYu7mR8uldq4rec2ttzj6Kjj2saF+GAGds-j2-3A7JcjvAGm06ObDcm5EunStir62jxpXBcTELWMgA
