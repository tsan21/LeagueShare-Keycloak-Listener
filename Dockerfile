FROM jboss/keycloak:latest
COPY ./target/keycloak-to-rabbit-2.1.jar /opt/jboss/keycloak/standalone/deployments/
COPY ./utils/cli/KEYCLOAK_TO_RABBIT.cli /opt/jboss/startup-scripts/

ENTRYPOINT ["/opt/jboss/tools/docker-entrypoint.sh"]

CMD ["-b", "0.0.0.0"]
