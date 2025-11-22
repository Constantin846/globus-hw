# Globus

### Описание

Данный проект создан в учебных целях.

Приложение хранит список клиентов и их банковские счета в различных валютах.

### Запуск проекта

**<span style="color:orange">Внимание!</span>** Для запуска проекта необходимы git, maven и docker

* Склонируйте репозиторий: git clone https://github.com/Constantin846/globus-hw.git
* Запустите docker до создания исполняемого jar
* Откройте PowerShell или Terminal или CMD
* Перейдите в директорию проекта: cd {your_path}/globus-hw
* Создайте исполняемый jar: mvn package
* Создайте и запустите docker контейнеры: docker-compose -f docker-compose.yml up

Возможен запуск docker контейнеров по одному:

* Для начала запустите Kafka: docker-compose -f docker-compose-kafka.yml up
* Запустите базу данных: docker-compose -f docker-compose-db.yml up
* Запустите основное приложение: docker-compose -f docker-compose-app.yml up

### API

Ознакомиться с API можно после запуска приложения по ссылке http://localhost:8080/swagger-ui/index.html



