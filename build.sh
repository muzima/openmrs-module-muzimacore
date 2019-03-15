mvn clean package -DskipTests
echo 'Deleting installed module .... ....'
sudo rm /var/lib/OpenMRS/modules/muzimacore-1.4.1.omod
echo 'Deploying new module ... ...'
sudo cp omod/target/muzimacore-1.4.1.omod /var/lib/OpenMRS/modules/
echo 'Restarting tomcat ... ...'
sudo service tomcat7 restart
sudo tail -f /var/log/tomcat7/catalina.out

