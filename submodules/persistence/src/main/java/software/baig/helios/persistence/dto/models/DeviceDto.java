package software.baig.helios.persistence.dto.models;

import com.fasterxml.jackson.annotation.JsonProperty;


public class DeviceDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("token")
    private String token;


    public DeviceDto() { }

    public String getToken() {
        return token;
    }

    public DeviceDto setToken(String token) {
        this.token = token;
        return this;
    }

    public String getName() {
        return name;
    }

    public DeviceDto setName(String name) {
        this.name = name;
        return this;
    }

}
