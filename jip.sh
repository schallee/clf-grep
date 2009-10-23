#! /bin/sh

dir=/home/schallee/devel/java/clf

exec java	\
	-javaagent:$dir/jip-1.1.1/profile/profile.jar	\
	-Dprofile.properties=$dir/jip.properties	\
	"$@"
