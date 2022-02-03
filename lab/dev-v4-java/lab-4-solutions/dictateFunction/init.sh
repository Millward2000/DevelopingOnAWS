sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

sudo update-alternatives --set java /usr/lib/jvm/java-11-amazon-corretto.x86_64/bin/java
sudo update-alternatives --set javac /usr/lib/jvm/java-11-amazon-corretto.x86_64/bin/javac

mvn -version