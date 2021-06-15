package com.nubari.licensingservice.service;

import com.nubari.licensingservice.config.ServiceConfig;
import com.nubari.licensingservice.model.License;
import com.nubari.licensingservice.model.Organization;
import com.nubari.licensingservice.repository.LicenseRepository;
import com.nubari.licensingservice.service.client.OrganizationDiscoveryClient;
import com.nubari.licensingservice.service.client.OrganizationFeignClient;
import com.nubari.licensingservice.service.client.OrganizationRestTemplateClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LicenseServiceImpl implements LicenseService {
    @Autowired
    MessageSource messages;

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private ServiceConfig serviceConfig;


    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestClient;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;


    public License getLicense(String licenseId, String organizationId, String clientType) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId).orElseThrow(
                () -> new IllegalArgumentException
                        (String.format(messages.getMessage("license.search.error.message", null, null)
                                , licenseId, organizationId))
        );


        Organization organization = retrieveOrganizationInfo(organizationId, clientType);
        if (null != organization) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(serviceConfig.getExampleProperty());
    }

    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
        Organization organization = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
                break;
        }

        return organization;
    }

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
    public List<License> getALL() {
        return licenseRepository.findAll();
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
