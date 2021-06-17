package com.nubari.licensingservice.service;

import com.nubari.licensingservice.model.License;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface LicenseService {
    License getLicense(String licenseId, String organizationId);
    List<License> getALL();
    List<License> getLicensesByOrganization(String organizationId) throws TimeoutException;

    License createLicense(License license);

    License updateLicense(License license);

    String deleteLicense(String licenseId);

    License getLicense(String licenseId, String organizationId, String clientType);
}
