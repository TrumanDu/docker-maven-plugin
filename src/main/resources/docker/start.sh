#!/bin/sh
option=
for arg in "$@"; do
	key=${arg%%=*}
	value=${arg#*=}
	case $key in
	--publicConfig)
		publicConfig=$value
		;;
	*)
		option="$option $key=$value"
		;;
	esac
done

if [ -n "$publicConfig" ]; then
	if wget -O application.properties $publicConfig; then
		echo "echo  run with git application.properties"
		cp application.properties ${BASE_DIR}/config/
	else
		if [ ! -f application.properties ]; then
			echo "not find publicConfig application.properties"
			exit 2
		else
			echo "echo run with cache  application.properties"
		fi
	fi
fi

java -jar {0}.jar $option
