set SCRIPT_DIR=%~dp0
java -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m  -Xss2M -jar "%SCRIPT_DIR%sbt-launch.jar" %*