package software.baig.helios.persistence.dto;


public interface DtoMapper<DatabaseEntity, Dto> {

    /**
     * Transforms a DTO into a entity for persistence.
     */
	DatabaseEntity toDatabaseEntity(Dto dto);

    /**
     * Transforms an entity to a DTO for network transfer.
     */
	Dto toDto(DatabaseEntity entity);

}
