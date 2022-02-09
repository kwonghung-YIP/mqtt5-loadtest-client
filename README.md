# Generate Odds Push data for testing

## Pull from docker hub and push to jFrog
```bash
docker pull kwonghung/mqtt-loadtest-client:latest
docker tag kwonghung/mqtt-loadtest-client:latest deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/kwonghung/odds-simulator:v0.2
docker push deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/kwonghung/odds-simulator:v0.2
```

## Run as docker image

### Generate WIN/PLA Odds
```bash
docker run -it \
  -e TZ=Asia/Hong_Kong -e SPRING_PROFILES_ACTIVE=case5 \
  -v "/etc/timezone:/etc/timezone:ro" \
  -v "/etc/localtime:/etc/localtime:ro" \
  deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/kwonghung/odds-simulator:v0.1 \
  --mqtt.broker-url=tcp://10.194.117.223:1883 --mqtt.username=tempuser --mqtt.password=00000000 \
  --win-odds.compress=true --win-odds.fixed-rate=2000 \
  --win-odds.topic=hk/d/prdt/wager/evt/01/upd/racing/20201006/s1/01/win/odds/full \
  --pla-odds.compress=true --pla-odds.fixed-rate=2000 \
  --pla-odds.topic=hk/d/prdt/wager/evt/01/upd/racing/20201006/s1/01/pla/odds/full \
  --odds.noOfHorse=14
```

### Generate DBL Odds
```bash
docker run -it \
  -e TZ=Asia/Hong_Kong -e SPRING_PROFILES_ACTIVE=case4 \
  -v "/etc/timezone:/etc/timezone:ro" \
  -v "/etc/localtime:/etc/localtime:ro" \
  kwonghung/mqtt-loadtest-client:latest \
  --mqtt.broker-url=tcp://10.194.117.223:1883 --mqtt.username=tempuser --mqtt.password=00000000 \
  --dbl-odds.fixed-rate=1000 \
  --dbl-odds.topic-zip=hk/d/prdt/wager/evt/01/upd/racing/20201006/s1/01/dbl/odds/full
```  

### Generate QIN Odds
```bash
docker run -it \
  -e TZ=Asia/Hong_Kong -e SPRING_PROFILES_ACTIVE=case6 \
  -v "/etc/timezone:/etc/timezone:ro" \
  -v "/etc/localtime:/etc/localtime:ro" \
  deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/kwonghung/odds-simulator:v0.2 \
  --mqtt.broker-url=tcp://10.194.117.223:1883 --mqtt.username=tempuser --mqtt.password=00000000 \
  --qin-odds.fixed-rate=2000 --qin-odds.f1=12 --qin-odds.f2=12 \
  --qin-odds.topic-zip=hk/d/prdt/wager/evt/01/upd/racing/20201006/s1/01/qin/odds/full
```

### Generate QPL Odds
```bash
docker run -it \
  -e TZ=Asia/Hong_Kong -e SPRING_PROFILES_ACTIVE=case6 \
  -v "/etc/timezone:/etc/timezone:ro" \
  -v "/etc/localtime:/etc/localtime:ro" \
  deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/kwonghung/odds-simulator:v0.2 \
  --mqtt.broker-url=tcp://10.194.117.223:1883 --mqtt.username=tempuser --mqtt.password=00000000 \
  --qin-odds.fixed-rate=2000 --qin-odds.f1=12 --qin-odds.f2=12 \
  --qin-odds.topic-zip=hk/d/prdt/wager/evt/01/upd/racing/20201006/s1/01/qpl/odds/full
```

### Other test cases
```bash
docker run -it \
  -e TZ=Asia/Hong_Kong -e SPRING_PROFILES_ACTIVE=case1 \
  -v "/etc/timezone:/etc/timezone:ro" \
  -v "/etc/localtime:/etc/localtime:ro" \
  deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/kwonghung/mqtt-loadtest-client:latest \
  --mqtt.broker-url=tcp://10.194.117.223:1883 --mqtt.username=tempuser --mqtt.password==00000000 \
  --odds.win.fixed-rate=1200 --odds.pla.fixed-rate=1000 --odds.update-whuch-horse:3
```

```bash
docker run -it \
  -e TZ=Asia/Hong_Kong -e SPRING_PROFILES_ACTIVE=case2 \
  -v "/etc/timezone:/etc/timezone:ro" \
  -v "/etc/localtime:/etc/localtime:ro" \
  deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/kwonghung/mqtt-loadtest-client:latest \
  --mqtt.broker-url=tcp://10.194.117.223:1883 --mqtt.username=tempuser --mqtt.password==00000000  \
  --odds-all.win.fixed-rate=4000 --odds-all.pla.init-delay=2000 --odds-all.pla.fixed-rate=4000
```

```bash
docker run -it \
  -e TZ=Asia/Hong_Kong -e SPRING_PROFILES_ACTIVE=case3 \
  -v "/etc/timezone:/etc/timezone:ro" \
  -v "/etc/localtime:/etc/localtime:ro" \
  deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/kwonghung/mqtt-loadtest-client:latest \
  --mqtt.broker-url=tcp://10.194.117.223:1883 --mqtt.username=tempuser --mqtt.password==00000000 \
  --odds-all.win.fixed-rate=8000 --odds-all.pla.init-delay=4000 --odds-all.pla.fixed-rate=8000
```  

```cmd
cls

SET SYSPROPS="-Dmqtt.broker-url=tcp://10.194.117.223:1883 -Dmqtt.username=tempuser -Dmqtt.password=00000000"
SET PROFILE="-Dspring.profiles.active=case6"

java %PROFILE% %SYSPROPS% -jar mqtt5-loadtest-client-0.0.1-SNAPSHOT.jar 
```
