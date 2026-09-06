package software.baig.helios.api.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;


public record MicroserviceBuildInformation(
		String version,

		@JsonProperty("hash")
		String buildHash
) { }
