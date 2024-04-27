package com.SSAssignment.onboardify.apigateway.configuration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user-management-service")
public interface UserManagementProxy {

	@GetMapping("/user-management-service/authentication/user/{username}")
	public ResponseEntity<Object> retrieveAuthenticatedUserInfo(@PathVariable String username);
}
