#####################
#truman create
#####################
FROM java:8-jre-alpine
MAINTAINER Truman

ENV BASE_DIR /opt/app/{0}
WORKDIR ${BASE_DIR}
ADD app ${BASE_DIR}/
RUN mkdir -p ${BASE_DIR}/config
CMD ["sh","start.sh"]