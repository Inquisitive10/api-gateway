package com.SSAssignment.onboardify.apigateway.configuration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>{

	
	Users findByUsername(String username);

	@Transactional
    @Query(value = "SELECT USERNAME FROM USERS WHERE TEAM_ID = :teamId AND ACCESS_LEVEL = :accessLevel", nativeQuery = true)
    String getPrivilegedUserForTeam(@Param("teamId") long teamId, @Param("accessLevel") int accessLevel);
}
