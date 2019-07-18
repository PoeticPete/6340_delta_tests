export JUNIT_HOME="/home/cs6340"
export CLASSPATH=$JUNIT_HOME/junit-4.12.jar:/home/cs6340/hamcrest-core-1.3.jar:.:..

javac TestJunit.java TestRunner.java ../*.java
java TestRunner

rm *.class