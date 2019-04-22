FROM openjdk:11.0-stretch

EXPOSE 8080

USER root

ADD backend/build/distributions/backend.tar app/

WORKDIR app/backend/bin

CMD while true; do ./backend; done
