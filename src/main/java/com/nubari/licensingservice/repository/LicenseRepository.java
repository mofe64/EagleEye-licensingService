package com.nubari.licensingservice.repository;

import com.nubari.licensingservice.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, String> {
    List<License> findByOrganizationId(String organizationId);
  Optional<License> findByOrganizationIdAndLicenseId(String organizationId, String licenseId);
}
