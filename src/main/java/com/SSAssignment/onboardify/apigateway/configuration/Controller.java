package com.SSAssignment.onboardify.apigateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class Controller {
	
//	@Autowired
//	private UserManagementProxy userManagementProxy;
	
	public ResponseEntity<Object> fetchAuthUserInfo(String username){
		return null;
		//return userManagementProxy.retrieveAuthenticatedUserInfo(username);
	}

}
