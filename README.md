# demo-project

## Local

0. Start the Simplatform application

1. Install galasactl with Homebrew
```
brew tap galasa-dev/tap
brew install galasactl@0.36.0 --no-quarantine
galasactl --version
```

2. Initialise local environment
```
galasactl local init --log -
```

3. Create a Galasa project
```
galasactl project create --package dev.galasa.example.simbank --features login --obr --gradle --log -
```

4. Declare project dependencies
```
implementation 'dev.galasa:dev.galasa.zos.manager'
implementation 'dev.galasa:dev.galasa.zos3270.manager'
```

5. Build the project
```
gradle clean build publishToMavenLocal
```

6. Write the Galasa test
```
cp TestLogin.java.example dev.galasa.example.simbank/dev.galasa.example.simbank.login/src/main/java/dev/galasa/example/simbank/login/TestLogin.java
```

7. Rebuild the project
```
gradle clean build publishToMavenLocal
```

8. Add configurational properties
```
zos.dse.tag.SIMBANK.imageid=SIMBANK
zos.image.SIMBANK.ipv4.hostname=127.0.0.1
zos.image.SIMBANK.telnet.tls=false
zos.image.SIMBANK.telnet.port=2023
zos.image.SIMBANK.webnet.port=2080
zos.image.SIMBANK.credentials=MYSIMBANKUSER
```

9. Add credentials properties
```
secure.credentials.MYSIMBANKUSER.username=IBMUSER
secure.credentials.MYSIMBANKUSER.password=SYS1
```

10. Run the test locally
```
galasactl runs submit local --obr mvn:dev.galasa.example.simbank/dev.galasa.example.simbank.obr/0.0.1-SNAPSHOT/obr --class dev.galasa.example.simbank.login/dev.galasa.example.simbank.login.TestLogin --log -
```

11. Display the 3270 images from the RAS

### Automation

