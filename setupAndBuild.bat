@echo off
@echo "Downloading libraries..."
mkdir Libs
curl https://ci.opencollab.dev//job/GeyserMC/job/Geyser/job/master/lastSuccessfulBuild/artifact/bootstrap/standalone/target/Geyser.jar
move Geyser.jar Lib
@echo "Building..."
mvn clean package