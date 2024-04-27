package com.SSAssignment.onboardify.apigateway.configuration;

import java.nio.charset.Charset;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;

import reactor.core.publisher.Mono;

@Configuration
public class ApiGatewayConfiguration {
	
	@Autowired
	private Config configuration;
	
	//private Controller controller = new Controller();

//	@Lazy
//	@Autowired
//	UserManagementProxy userManagementProxy;
	
	@Bean
	public RouteLocator gatewayRoute(RouteLocatorBuilder builder, @Lazy AuthFilterFactory authFilterFactory) {
		
		//ResponseEntity<Object> entity = userManagementProxy.retrieveAuthenticatedUserInfo("govindKumar");
		return builder.routes()
				.route(p -> p.path("/user-management-service/**")
					.filters(f -> f.filter(authFilterFactory.apply(Integer.valueOf(1))))
					.uri("lb://user-management-service"))
				.route(p -> p.path("/task-management-service/**")
						.filters(f -> f.filter(authFilterFactory.apply(Integer.valueOf(1))))
						.uri("lb://task-management-service"))
				.route(p -> p.path("/training-content-management-service/**")
						.filters(f -> f.filter(authFilterFactory.apply(Integer.valueOf(1))))
						.uri("lb://training-content-management-service"))
				.build();
	}
	
	@Bean
    public AuthFilterFactory authFilterFactory(@Lazy UsersRepository userRepository) {
        return new AuthFilterFactory(configuration, userRepository);
    }
	
    public static class AuthFilterFactory extends AbstractGatewayFilterFactory<Object> {
    	private final Config configuration;
    	private final UsersRepository userRepository;
    	//private final Controller controller;

        public AuthFilterFactory(Config configuration, UsersRepository userRepository) {
            this.configuration = configuration;
            this.userRepository = userRepository;
        }
    	
    	@Override
        public GatewayFilter apply(Object config) {
    		final Config configClass = configuration;

            return (exchange, chain) -> {
            	ServerHttpRequest request = exchange.getRequest();
	            String uri = request.getURI().toString();
	            HttpMethod method = request.getMethod();
	            System.out.println("URI of the incoming request: " + uri);
                HttpHeaders headers = exchange.getRequest().getHeaders();
                String token = headers.getFirst("Authorization");
                if(token == null) {
                	exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                	String errorMessage = "Authentication failed";
                	return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes())));
                }
                else
                	token = token.substring(6);
                String credentials[] = new String(
						Base64.getDecoder().decode(token), Charset.defaultCharset()).split(":");
                if(uri.contains("/user-management-service/") && credentials[0].equals(configClass.getUsername()) && credentials[1].equals(configClass.getPassword()))
                	return chain.filter(exchange);
                else if(uri.contains("/training-content-management-service/")) {
                	Users user = null;
                	try {
                		user = userRepository.findByUsername(credentials[0]);
                	}catch(Exception ex) {
                		System.out.println(ex.getMessage());
                	}
                	if(user == null) {
                		String errorMessage = "Authentication failed, please provide valid credentials";
                    	return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes())));
                	} else {
//                		JSONObject responseBody = (JSONObject) entity.getBody();
//                		String password = null;
//						try {
//							password = responseBody.getString("password");
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
                		if(!credentials[1].equals(user.getPassword())) {
                			String errorMessage = "Authentication failed, please provide valid credentials";
                        	return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes())));
                		} else {
                			int accessLevel = 0;
//							try {
//								accessLevel = responseBody.getInt("accessLevel");
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
                			if(!(user.getAccessLevel() == configuration.getAccessLevelPrevilegedAdmin() || user.getAccessLevel() == configuration.getAccessLevelAdmin())) {
                				String errorMessage = "User is not authorized to perform this action";
                            	return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes())));
                			}
                		}
                	}
                } else if(uri.contains("/task-management-service/task")) {
                	Users user = null;
                	try {
                		user = userRepository.findByUsername(credentials[0]);
                	}catch(Exception ex) {
                		System.out.println(ex.getMessage());
                	}
                	if(user == null) {
                		String errorMessage = "Authentication failed, please provide valid credentials";
                    	return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes())));
                	} else {
//                		JSONObject responseBody = (JSONObject) entity.getBody();
//                		String password = null;
//						try {
//							password = responseBody.getString("password");
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
                		if(!credentials[1].equals(user.getPassword())) {
                			String errorMessage = "Authentication failed, please provide valid credentials";
                        	return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes())));
                		} else {
                			int accessLevel = 0;
//							try {
//								accessLevel = responseBody.getInt("accessLevel");
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
                			if(!method.equals(HttpMethod.PUT) && !(user.getAccessLevel() == configuration.getAccessLevelPrevilegedAdmin())){
                				String errorMessage = "User is not authorized to perform this action";
                            	return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes())));
                			}
                		}
                	}
                }
                else {
                	exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                	String errorMessage = "Authentication failed, please provide valid credentials";
                	return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes())));
                }
                // Perform authentication logic using the token
                
                // If authentication fails, return an error response
                // Example:
                return chain.filter(exchange);
                
                // If authentication succeeds, continue to the next filter or route
                
            };
        }
    }

}
