package com.nubari.licensingservice.service;

import com.nubari.licensingservice.model.License;

public interface LicenseService {
    License getLicense(String licenseId, String organizationId);
    License createLicense(License license);
    License updateLicense(License license);
    String deleteLicense(String licenseId);
}
