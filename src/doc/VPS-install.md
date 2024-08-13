# Installation on the Contabo VPS

## Installing Docker

- Install docker, following instructions at https://docs.docker.com/engine/install/ubuntu/
- Manage Docker as a non-root user following instructions at https://docs.docker.com/engine/install/linux-postinstall/
- 

## Installing Prerequisites

Install the following packages:

```bash
apt-get install -y default-jre
apt-get install -y maven
apt install -y postgresql-client-common
apt install -y postgresql-client
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

Create a run script `adifproc.sh` wi#!/bin/bash

WORKDIR=/home/adifproc/code/adifproc/adifweb
JAVA_OPTIONS=""
APP_OPTIONS=""

export POSTGRES_DB=postgres
export POSTGRES_HOST=localhost
export POSTGRES_PASSWORD=mysecretpassword
export POSTGRES_PORT=5432
export POSTGRES_USERNAME=postgres
export QRZ_PASSWORD=mark4qrzasm0nom
export QRZ_USERNAME=M0NOM
export SPRING_PROFILES_ACTIVE=prod
export LOGDIR=/home/adifproc/logs

cd $WORKDIR
mvn -Pprod spring-boot:run >> $LOGDIR/adifproc.log 2>&1 &
```

Create a systemd unit file `adifproc.service` in `/etc/systemd/system` with the following contents:

```
[Unit]
Description=ADIF Processor
After=syslog.target network.target
[Service]
SuccessExitStatus=143
User=adifproc
Group=adifproc

Type=forking

ExecStart=/home/adifproc/adifproc.sh
ExecStop=/bin/kill -15 $MAINPID

[Install]
WantedBy=multi-user.target
```

Enable the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable adifproc.service
sudo systemctl start adifproc.service
```

## Postgres Docker Container

Instructions here: https://www.docker.com/blog/how-to-use-the-postgres-docker-official-image/

```bash
docker pull postgres
#docker run --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -d postgres
docker container create --name postgres-container -e POSTGRES_PASSWORD=<password> -p 5432:5432 postgres
docker update --restart=always postgres-container
```

Check the database connection:

```bash
psql -h localhost -U postgres -p 5432 -d 
```

```sql
select * from log;
```

Open the port 5432 in the firewall:

```bash
sudo ufw allow from any to any port 5432 proto tcp
```

## SSL Certificate

Following these instructions: https://certbot.eff.org/instructions?ws=nginx&os=ubuntufocal
As superuser:

```bash
apt install nginx
apt install snapd
snap install --classic certbot
```

### Certbot Configuration

```bash
sudo certbot --nginx
```

### Nginx Configuration for reverse proxy to java application

```bash
sudo vim /etc/nginx/sites-available/default
```

Add the following contents:

```
server {
    server_name www.adif.uk; # managed by Certbot

    listen [::]:443 ssl ipv6only=on; # managed by Certbot
    listen 443 ssl; # managed by Certbot
    ssl_certificate /etc/letsencrypt/live/www.adif.uk/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/www.adif.uk/privkey.pem; # managed by Certbot
    include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_ssl_trusted_certificate /etc/nginx/ssl/unifi.cer;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

}

server {
    if ($host = www.adif.uk) {
        return 301 https://$host$request_uri;
    } # managed by Certbot


	listen 80 ;
	listen [::]:80 ;
    server_name www.adif.uk;
    return 404; # managed by Certbot


}
```

```bash
sudo service nginx restart
```
