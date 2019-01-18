BASEDIR=./

all : project

project :
	javac -cp . `find ${BASEDIR}/src/ -type f -name "*.java"`

fullcleanup:
	rm `find ${BASEDIR} -type f -name "*.class"`

