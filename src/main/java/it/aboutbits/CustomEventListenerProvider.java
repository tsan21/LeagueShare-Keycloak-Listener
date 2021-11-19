package it.aboutbits;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;

import java.util.Map;
import java.util.Set;

public class CustomEventListenerProvider implements EventListenerProvider {

    private Set<EventType> excludedEvents;
    private Set<OperationType> excludedAdminOperations;

    public CustomEventListenerProvider(Set<EventType> excludedEvents, Set<OperationType> excludedAdminOperations) {
        this.excludedEvents = excludedEvents;
        this.excludedAdminOperations = excludedAdminOperations;
    }

    @Override
    public void onEvent(Event event) {
        // Ignore excluded events
        if (excludedEvents != null && excludedEvents.contains(event.getType())) {
        } else {
            System.out.println("EVENT: " + toString(event));
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        // Ignore excluded operations
        if (excludedAdminOperations != null && excludedAdminOperations.contains(event.getOperationType())) {
        } else {
            System.out.println("EVENT: " + toString(event));
        }
    }

    private String toString(Event event) {
        StringBuilder sb = new StringBuilder();

        sb.append("type=");
        sb.append(event.getType());
        sb.append(", realmId=");
        sb.append(event.getRealmId());
        sb.append(", clientId=");
        sb.append(event.getClientId());
        sb.append(", userId=");
        sb.append(event.getUserId());
        sb.append(", ipAddress=");
        sb.append(event.getIpAddress());

        if (event.getError() != null) {
            sb.append(", error=");
            sb.append(event.getError());
        }

        if (EventType.REGISTER.equals(event.getType())) {
            System.out.println("New user registered with userID: " + event.getUserId());
            System.out.println("New user registered with username: " + event.getDetails().get("username"));
            System.out.println("New user registered with email: " + event.getDetails().get("email"));
        }

        if (event.getDetails() != null) {
            for (Map.Entry<String, String> e : event.getDetails().entrySet()) {
                sb.append(", ");
                sb.append(e.getKey());
                if (e.getValue() == null || e.getValue().indexOf(' ') == -1) {
                    sb.append("=");
                    sb.append(e.getValue());
                } else {
                    sb.append("='");
                    sb.append(e.getValue());
                    sb.append("'");
                }
            }
        }

        return sb.toString();
    }

    private String toString(AdminEvent adminEvent) {
        StringBuilder sb = new StringBuilder();

        sb.append("operationType=");
        sb.append(adminEvent.getOperationType());
        sb.append(", realmId = ");
        sb.append(adminEvent.getAuthDetails().getRealmId());
        sb.append(", clientId = ");
        sb.append(adminEvent.getAuthDetails().getClientId());
        sb.append(", userId = ");
        sb.append(adminEvent.getAuthDetails().getUserId());
        sb.append(", ipAddress = ");
        sb.append(adminEvent.getAuthDetails().getIpAddress());
        sb.append(", resourceType = ");
        sb.append(adminEvent.getResourceTypeAsString());
        sb.append(", resourcePath = ");
        sb.append(adminEvent.getResourcePath());
        sb.append(", representation = ");
        sb.append(adminEvent.getRepresentation());

        if (adminEvent.getError() != null) {
            sb.append(", error=");
            sb.append(adminEvent.getError());
        }

        return sb.toString();
    }

    @Override
    public void close() {
    }
}
