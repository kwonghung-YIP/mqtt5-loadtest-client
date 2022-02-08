# Command for using HiveMQ mqtt-cli for testing

```bash
docker run -it deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/hivemq/mqtt-cli:4.7.0

docker run -it deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/hivemq/mqtt-cli:4.7.0 shell

docker run -it deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/hivemq/mqtt-cli:4.7.0 test -h localhost

docker run -it deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/hivemq/mqtt-cli:4.7.0 test -h 10.194.180.243 -p 51884 -u tempuser -pw 00000000

docker run -it deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/hivemq/mqtt-cli:4.7.0 con -v -ws -h 10.194.180.243 -p 51884 -u tempuser -pw 00000000

con -v -h 10.194.180.243 -p 51884 -u tempuser -pw 00000000

con -v -ws -h 10.194.180.243 -p 51884 -u tempuser -pw 00000000

con -v -h 10.194.117.223 -p 1883 -u tempuser -pw 00000000

pub -t "public/odds/20211001ST/ra/0/win/0" -ct "application/json" -m "{odds:'3.4'}"

pub -t "public/odds/20211001ST/ra/0/pla/1" -ct "application/json" -m "999"

pub -t "measure/latency" -ct "application/json" -m '{"count":10,"sentTime":"2021-09-29T16:50:23.343"}

con -v -ws -h 10.194.117.223 -p 51884 -u tempuser -pw 00000000

docker run -it \
  -e TZ=Asia/Hong_Kong -e SPRING_PROFILES_ACTIVE=dev1 \
  -v "/etc/timezone:/etc/timezone:ro" \
  -v "/etc/localtime:/etc/localtime:ro" \	
  deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/kwonghung/mqtt-basic-pahoclient:latest \
  --mqtt.broker-url=tcp://10.194.117.223:1883 --mqtt.username=tempuser --mqtt.password==00000000 \
  --measure-latency.fixed-rate=500
  
docker run -it \
  -e TZ=Asia/Hong_Kong -e SPRING_PROFILES_ACTIVE=dev1 \
  -v "/etc/timezone:/etc/timezone:ro" \
  -v "/etc/localtime:/etc/localtime:ro" \
  deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/kwonghung/mqtt-basic-pahoclient:latest \
  --mqtt.broker-url=ws://10.194.180.243:51884 --mqtt.username=tempuser --mqtt.password==00000000  

docker run -it deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/hivemq/mqtt-cli:4.7.0 shell  
con -v -ws -h 10.194.180.243 -p 51884 -u tempuser -pw 00000000
con -v -h 10.194.117.223 -p 1883 -u tempuser -pw 00000000
pub -t "public/odds/20211001ST/ra/0/win/0" -ct "application/json" -m "7.7"
pub -t "public/odds/20211001ST/ra/0/win/1" -ct "application/json" -m "6.8"
pub -t "public/odds/20211001ST/ra/0/win/2" -ct "application/json" -m "3.4"
pub -t "public/odds/20211001ST/ra/0/win/3" -ct "application/json" -m "1.2"
pub -t "public/odds/20211001ST/ra/0/win/4" -ct "application/json" -m "9.9"
pub -t "public/odds/20211001ST/ra/0/pla/0" -ct "application/json" -m "9.9"
pub -t "public/odds/20211001ST/ra/0/pla/1" -ct "application/json" -m "9.9"
pub -t "public/odds/20211001ST/ra/0/pla/2" -ct "application/json" -m "9.9"
pub -t "public/odds/20211001ST/ra/0/pla/3" -ct "application/json" -m "9.9"
pub -t "public/odds/20211001ST/ra/0/pla/4" -ct "application/json" -m "9.9"

pub -t "measure/latency" -m "Hello"
pub -t "test_topic" -ct "application/json" -m "good good"

sub -t hk/d/# -s -T

docker run -it deopcard.corp.hkjc.com/csp-svcmesh-docker-snapshot-local/mqtt/hivemq/mqtt-cli:4.7.0 \
  pub -h 10.194.117.223 -p 1883 -u tempuser -pw 00000000 \
  -t "public/odds/20211001ST/ra/0/win/0" -ct "application/json" -m "7.7"
```
