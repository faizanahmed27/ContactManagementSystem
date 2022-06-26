package com.smart.contact.audit.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import com.smart.contact.config.SecurityUtils;

class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {

		//return Optional.of("V1008895");
		
		return Optional.of(SecurityUtils.getCurrentUserLogin().get());
		
		// Can use Spring Security to return currently logged in user
		// return ((User)
		// SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()
	}
}