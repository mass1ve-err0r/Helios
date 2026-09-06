package software.baig.helios.api.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;


public record RegisterReceiverRequest(
        String name,
        String token,

        @JsonProperty("registration_token")
        String registrationToken
) { }
