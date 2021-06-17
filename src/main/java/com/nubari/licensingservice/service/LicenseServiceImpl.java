package com.nubari.licensingservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.nubari.licensingservice.config.ServiceConfig;
import com.nubari.licensingservice.model.License;
import com.nubari.licensingservice.model.Organization;
import com.nubari.licensingservice.repository.LicenseRepository;
import com.nubari.licensingservice.service.client.OrganizationDiscoveryClient;
import com.nubari.licensingservice.service.client.OrganizationFeignClient;
import com.nubari.licensingservice.service.client.OrganizationRestTemplateClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
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

//    @Override
//    @HystrixCommand(commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "12000")})
//    public List<License> getLicensesByOrganization(String organizationId) {
//        randomlyRunLong();
//        return licenseRepository.findByOrganizationId(organizationId);
//    }

    //    @Override
//    @HystrixCommand(fallbackMethod = "fallbackLicenseList")
//    public List<License> getLicensesByOrganization(String organizationId) {
//        randomlyRunLong();
//        return licenseRepository.findByOrganizationId(organizationId);
//    }
    @Override
    @CircuitBreaker(name = "licenseService", fallbackMethod = "fallbackLicenseList")
    public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private List<License> fallbackLicenseList(String organizationId, Throwable t)  {
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("000-0000-000");
        license.setOrganizationId("OrganizationId");
        license.setProductName("Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
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


    private void randomlyRunLong() throws TimeoutException {
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;
        if (randomNum == 3) sleep();
    }

    private void sleep() throws TimeoutException {
        try {
            Thread.sleep(5000);
            throw new java.util.concurrent.TimeoutException("Err");
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

}
