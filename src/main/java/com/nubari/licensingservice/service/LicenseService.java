package com.nubari.licensingservice.service;

import com.nubari.licensingservice.model.License;

import java.util.List;

public interface LicenseService {
    License getLicense(String licenseId, String organizationId);
    List<License> getALL();
    List<License> getLicensesByOrganization(String organizationId);

    License createLicense(License license);

    License updateLicense(License license);

    String deleteLicense(String licenseId);

    License getLicense(String licenseId, String organizationId, String clientType);
}
