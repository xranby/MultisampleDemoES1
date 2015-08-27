wget https://dl.dropboxusercontent.com/u/4969127/bg.png
wget http://jogamp.org/deployment/archive/master/gluegen_885-joal_611-jogl_1425-jocl_1074/fat/jogamp-fat.jar
javac -cp jogamp-fat.jar:. Run.java
java -cp jogamp-fat.jar:. Run
