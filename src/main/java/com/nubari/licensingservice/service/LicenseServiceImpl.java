package com.nubari.licensingservice.service;

import com.nubari.licensingservice.config.ServiceConfig;
import com.nubari.licensingservice.model.License;
import com.nubari.licensingservice.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.UUID;

public class LicenseServiceImpl implements LicenseService {
    @Autowired
    MessageSource messages;

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private ServiceConfig serviceConfig;

    @Override
    public License getLicense(String licenseId, String organizationId) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                String.format(messages.getMessage("license.search.error.message",
                                        null, null), licenseId, organizationId)
                        ));
        return license.withComment(serviceConfig.getExampleProperty());
    }

    @Override
    public License createLicense(License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license.withComment(serviceConfig.getExampleProperty());
    }

    @Override
    public License updateLicense(License license) {
        licenseRepository.save(license);
        return license.withComment(serviceConfig.getExampleProperty());
    }

    @Override
    public String deleteLicense(String licenseId) {
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMessage = String.format(messages.getMessage("license.delete.message", null, null), licenseId);
        return responseMessage;
    }
}
