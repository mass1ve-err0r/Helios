package software.baig.helios.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.baig.helios.api.models.responses.MicroserviceBuildInformation;
import software.baig.helios.utilities.Constants;
import software.baig.helios.utilities.EnvironmentUtil;


@RestController
@RequestMapping("api/system")
public class ServiceInformationAPI {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceInformationAPI.class);

	private final BuildProperties buildProperties;


	@Autowired
	public ServiceInformationAPI(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@GetMapping("health")
	public ResponseEntity<Void> healthcheck() {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

	@GetMapping("environment")
	public ResponseEntity<String> obtainServiceEnvironment() {
		String env = EnvironmentUtil.getSystemEnv(Constants.ENV_SERVICE_MODE, "DEVELOPMENT");
		return ResponseEntity.ok(env);
	}

	@GetMapping("build-info")
	public ResponseEntity<MicroserviceBuildInformation> getBuildInformation() {
		MicroserviceBuildInformation buildInfo = new MicroserviceBuildInformation(
				buildProperties.getVersion(),
				buildProperties.get("commit-hash")
		);

		return ResponseEntity.ok(buildInfo);
	}

}
