# Project (~2018/2019 startup)
Start-up project for "anonynomous" chat. 
 - Microservice for fetching tv-shows
 - Android for chatting
 - backend for handling everything

## Dependencies in project
Backend uses core and common
Android uses common
Showfetch core and common

# Backend
## build
	./gradlew clean war -Penv=prod 
## Move to server
	Pitää siirtää käsin serverille. (Kirjaudu anochatn tunnusille eli heikki) sftp tai filezilla
	/asennukset/yyyy-mm-dd -kansioon (luotava)
## Deploy

	sudo service tomcat stop
	cd /usr/share/tomcat/webapps
	sudo rm -rf *
	cd ~/asennukset/
	sudo cp backend.war /usr/share/tomcat/webapps/ROOT.war
	sudo service tomcat start

# TV-sync
## Build
	./gradlew clean :showfetch:build -Penv=cloud_mysql
## Move
	Pitää siirtää käsin serverille. (Kirjaudu anochatn tunnusille eli heikki) sftp tai filezilla
	/asennukset/yyyy-mm-dd -kansioon (luotava)
## Deploy
	Laitetaan käyttäjän juureen. crontab hoitaa käynnistyksen. 
## Manual start
	java -jar subprojects/showfetch/build/libs/showfetch.jar

# Android
## Projekti
	Android Drawable Importer Plugin: https://plugins.jetbrains.com/plugin/7658-android-drawable-importer
	/anochat/subprojects/android/app

 Android multiproject (intellij)
 laita android-kansion settings.gradleen seuraava koodi
	include ':app'
	include ":common"
	project(":common").projectDir = file("../common")
	1) Poista kaikki intellij-kansiot
	2) Import project from gradle
	   subprojects/android -kansio
 Näin projektista tulee saadaan linkki common-projektiin.

	Eli
	0) Muokkaa settings.gradlea siten, ett� sis�lt�� mainitut tiedot
	1) Avaa IntelliJ
	2) Import project from gradle
	   - subproject
	3) Varmista, että app/build.gradle sisältää compile project(":common")

## Rakentaminen
	Päivitä versionumero (app/build.gradle)
	./gradlew clean assembleProd

## Paikallisen kannan asennus
 ./gradlew flywayclean flywaymigrate

## GCLOUD komentoja
 ## gcloud app instances list
 	aktiiviset instanssit
 ## gcloud config set app/num_file_upload_processes 1
	Siirrä tiedot yhdessä prosessissa
 ## glcoud init 
 	Initialisoi käyttäjä eli valitse oikea käyttäjä asennukseen. (aina  [7] europe-west1-b )
 ## gcloud app logs read
 	Lue asennuslokeja 
 ## gradlew appengineStage -Penv=
 	Katso, että siirrettäv image sisältää kaikki tarvittavat (build-kansio)
 ## gcloud auth list
 	Käyttäjät
 	
# Tietokanta
 ## Googlen pilvikantaa vasten voi testata järjestelmän toiminnallisuutta asettamalla GAE-access controlliin koneesi ip ja 
    rakentamalla ohjelmat komennolla
	./gradlew explodedWar -Penv=beta_cloud
	./gradlew :showfetch:build -Penv=beta_cloud
 ## Yhteyden kantaa saan millä ohjelmalla tahansa (dbeaver, squirrel)

# Järjestelmän debuggaus lokaalisti
 ## eclipse convert to facet => jos debugataan Eclipsessä.
  	project facets -> dynamic webmodule 
  	deployment assembly -> build/exploded

# Kehityksen tietokannan luonti lokaalisti  

  create user 'anochatuser'@'localhost' identified by 'anochat1#asdfasdf';
  grant all privileges on anochat.* to anochatuser@'%' identified by 'anochat1#asdfasdf';
  grant all privileges on snope.* to anochatuser@'127.0.0.1' identified by 'anochat#as121dfaddsdf';
  grant all privileges on anochat.* to anochatuser@'localhost' identified by 'anochat#asdfddasdf';

  flush privileges;
  SHOW GRANTS FOR 'anochatuser'@'localhost';
  select * from db;
  
  mysql.exe -u anochat -panochat#asdfasdf
