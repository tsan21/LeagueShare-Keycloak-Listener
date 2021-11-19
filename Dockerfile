FROM jboss/keycloak:latest
COPY ./target/keycloak-event-listener-0.0.1-SNAPSHOT.jar /opt/jboss/keycloak/standalone/deployments/

ENTRYPOINT ["/opt/jboss/tools/docker-entrypoint.sh"]

CMD ["-b", "0.0.0.0"]