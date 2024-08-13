# Installation on the Contabo VPS

## Installing Docker

- Install docker, following instructions at https://docs.docker.com/engine/install/ubuntu/
- Manage Docker as a non-root user following instructions at https://docs.docker.com/engine/install/linux-postinstall/
- 

## Installing Prerequisites

Install the following packages:

```bash
sudo apt-get install -y default-jre
sudo apt-get install -y maven
```
Install github cli: https://github.com/cli/cli/blob/trunk/docs/install_linux.md

## Installing Java

- Create a user account `adifproc`
- Clone the repository into `/home/adifproc/code`
- Create a directory for the log files `/home/adifproc/logs`
- Add the user to the docker group `sudo usermod -aG docker adifproc`

## Boot time configuration

Configure docker to start on boot: 

```bash
sudo systemctl enable docker.service
sudo systemctl enable containerd.service`
```
Configure a watchdog to start and monitor the adifproc application:

```bash
sudo systemctl enable watchdog.service
sudo systemctl start watchdog.service
```

## Installing the application

Clone the repository into `/home/adifproc/code`


## Environment Variables
| Environment Variable   | Value          |
|------------------------|----------------|
| POSTGRES_DB            | postgres       |
| POSTGRES_HOST          | localhost      |
| POSTGRES_PASSWORD      | <db_password>  |
| POSTGRES_USERNAME      | adifproc       |
| QRZ_PASSWORD           | <qrz_password> |
| QRZ_USERNAME           | <qrz_username> |
| SPRING_PROFILES_ACTIVE | prod           |


## Environment Variables on command line

Create a run script `adifproc.sh` with the following contents:

```bash
#!/bin/bash

# Set the environment variables
export POSTGRES_DB=postgres
export POSTGRES_HOST=localhost
export POSTGRES_PASSWORD=mysecretpassword
export POSTGRES_USERNAME=adifproc
export QRZ_PASSWORD=<qrz_password>
export QRZ_USERNAME=<qrz_username>
export SPRING_PROFILES_ACTIVE=prod

# Run the application
java -jar /home/adifproc/code/target/adif-processor-1.4.11-SNAPSHOT-jar-with-dependencies.jar
```

## Postgres Docker Container

Instructions here: https://www.docker.com/blog/how-to-use-the-postgres-docker-official-image/

```bash
docker pull postgres
docker run --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -d postgres
```

## SSL Certificate

Following these instructions: https://certbot.eff.org/instructions?ws=nginx&os=ubuntufocal
As superuser:

```bash
apt install nginx
apt install snapd
snap install --classic certbot
```

### Nginx Configuration for reverse proxy to java application

```bash
sudo vim /etc/nginx/sites-available/adif-processor
```

Add the following contents:

```
server {
    listen 80;
    server_name adif-processor.example.com;
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

```bash
sudo service nginx restart
```

### Certbot Configuration

```bash
sudo certbot --nginx
```